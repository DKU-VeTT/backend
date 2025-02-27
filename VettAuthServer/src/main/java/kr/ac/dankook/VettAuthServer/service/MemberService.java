package kr.ac.dankook.VettAuthServer.service;

import kr.ac.dankook.VettAuthServer.config.principal.PrincipalDetails;
import kr.ac.dankook.VettAuthServer.dto.request.EditMemberRequest;
import kr.ac.dankook.VettAuthServer.dto.request.FindIdRequest;
import kr.ac.dankook.VettAuthServer.dto.response.MemberResponse;
import kr.ac.dankook.VettAuthServer.dto.response.TokenResponse;
import kr.ac.dankook.VettAuthServer.entity.Member;
import kr.ac.dankook.VettAuthServer.event.EventObjectMapper;
import kr.ac.dankook.VettAuthServer.event.MemberEvent;
import kr.ac.dankook.VettAuthServer.event.OutboxEvent;
import kr.ac.dankook.VettAuthServer.exception.ApiErrorCode;
import kr.ac.dankook.VettAuthServer.exception.ApiException;
import kr.ac.dankook.VettAuthServer.jwt.JwtRedisHandler;
import kr.ac.dankook.VettAuthServer.jwt.JwtTokenProvider;
import kr.ac.dankook.VettAuthServer.repository.MemberRepository;
import kr.ac.dankook.VettAuthServer.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtRedisHandler jwtRedisHandler;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;
    private final EventObjectMapper eventObjectMapper;

    @Transactional(readOnly = true)
    public List<String> findUserIdProcess(FindIdRequest findIdRequest){
        return memberRepository.findByNameAndEmail(findIdRequest.getName(), findIdRequest.getEmail())
                .stream()
                .map(Member::getUserId)
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public List<Member> findAllMemberProcess(){
        return memberRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Member findMemberByUserIdProcess(String userId){
        Optional<Member> member = memberRepository.findByUserId(userId);
        return member.orElse(null);
    }

    @Transactional(readOnly = true)
    public Member findMemberByIdProcess(Long id){
        Optional<Member> targetMember = memberRepository.findById(id);
        if(targetMember.isPresent()){
            return targetMember.get();
        }
        throw new ApiException(ApiErrorCode.MEMBER_NOT_FOUND);
    }

    public MemberResponse convertMemberEntity(Member member){
        return MemberResponse.builder()
                .id(EncryptionUtil.encrypt(member.getId()))
                .userId(member.getUserId())
                .email(member.getEmail())
                .createTime(member.getCreatedDateTime())
                .roles(member.getRoles())
                .build();
    }

    @Transactional
    public void deleteMemberByIdProcess(Long id){

        Member targetMember = findMemberByIdProcess(id);
        String targetUserId = targetMember.getUserId();
        Optional<String> redisRefreshToken = jwtRedisHandler.findByUserId(targetUserId);
        if (redisRefreshToken.isPresent()){
            jwtRedisHandler.delete(targetUserId);
        }
        String eventId = UUID.randomUUID().toString();
        String payload = eventObjectMapper.eventObjectToJson(
                new MemberEvent(
                        EncryptionUtil.encrypt(targetMember.getId()),
                        eventId,
                        targetMember.getUserId(), targetMember.getName(), targetMember.getEmail()
                ));
        OutboxEvent eventBox = OutboxEvent.builder()
                .id(eventId)
                .aggregateType("User")
                .eventType("UserDeleted")
                .payload(payload).build();
        eventPublisher.publishEvent(eventBox);
        memberRepository.deleteById(targetMember.getId());
    }

    @Transactional
    public TokenResponse editMemberProcess(Long id, EditMemberRequest editMemberRequest){

        Member targetMember = findMemberByIdProcess(id);
        if (!targetMember.getUserId().equals(editMemberRequest.getUserId()) &&
                memberRepository.existsByUserId(editMemberRequest.getUserId())){
            throw new ApiException(ApiErrorCode.DUPLICATE_ID);
        }
        deletePreviousRefreshToken(targetMember.getUserId());
        targetMember.setEmail(editMemberRequest.getEmail());
        targetMember.setName(editMemberRequest.getName());
        targetMember.setUserId(editMemberRequest.getUserId());
        Member editMember = memberRepository.save(targetMember);

        String eventId = UUID.randomUUID().toString();
        String payload = eventObjectMapper.eventObjectToJson(
                new MemberEvent(
                        EncryptionUtil.encrypt(editMember.getId()),
                        eventId,
                        editMember.getUserId(), editMember.getName(), editMember.getEmail()
        ));
        OutboxEvent eventBox = OutboxEvent.builder()
                .id(eventId)
                .aggregateType("User")
                .eventType("UserModified")
                .payload(payload).build();
        eventPublisher.publishEvent(eventBox);
        return generateNewTokenUsingUpdatingAuthentication(editMember);
    }

    // Password 변경 시 다시 로그인 필요
    @Transactional
    public void editMemberPasswordProcess(Long id,String newPassword){

        Member targetMember = findMemberByIdProcess(id);
        deletePreviousRefreshToken(targetMember.getUserId());
        SecurityContextHolder.clearContext();

        String encodePassword = passwordEncoder.encode(newPassword);
        targetMember.setPassword(encodePassword);
        memberRepository.save(targetMember);
    }

    private void deletePreviousRefreshToken(String userId){
        Optional<String> redisRefreshToken = jwtRedisHandler.findByUserId(userId);
        if (redisRefreshToken.isPresent()){
            jwtRedisHandler.delete(userId);
        }
    }
    private TokenResponse generateNewTokenUsingUpdatingAuthentication(Member updatedMember) {

        PrincipalDetails principalDetails = new PrincipalDetails(updatedMember);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
        TokenResponse token = jwtTokenProvider.generateToken(currentAuthentication);
        jwtRedisHandler.save(updatedMember.getUserId(), token.getRefreshToken());
        return token;
    }
}
