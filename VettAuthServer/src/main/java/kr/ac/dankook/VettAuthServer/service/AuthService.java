package kr.ac.dankook.VettAuthServer.service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import kr.ac.dankook.VettAuthServer.config.principal.PrincipalDetails;
import kr.ac.dankook.VettAuthServer.dto.request.SignInRequest;
import kr.ac.dankook.VettAuthServer.dto.request.SignupRequest;
import kr.ac.dankook.VettAuthServer.dto.request.TokenRequest;
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
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRedisHandler jwtRedisHandler;
    private final ApplicationEventPublisher eventPublisher;
    private final EventObjectMapper eventObjectMapper;

    @Transactional(readOnly = true)
    public boolean isExistUserIdProcess(String userId){
        return memberRepository.existsByUserId(userId);
    }

    @Transactional
    public TokenResponse signupProcess(SignupRequest signupRequest) {

        if (isExistUserIdProcess(signupRequest.getUserId())){
            throw new ApiException(ApiErrorCode.DUPLICATE_ID);
        }
        String encodePassword = passwordEncoder.encode(signupRequest.getPassword());
        Member newMember = Member.builder()
                .email(signupRequest.getEmail())
                .name(signupRequest.getName())
                .password(encodePassword)
                .userId(signupRequest.getUserId())
                .roles("ROLE_USER")
                .build();
        memberRepository.save(newMember);
        memberRepository.flush();
        TokenResponse token = signInProcess(
                new SignInRequest(newMember.getUserId(),signupRequest.getPassword())
        );
        String eventId = UUID.randomUUID().toString();
        String payload = eventObjectMapper.eventObjectToJson(
                new MemberEvent(
                        EncryptionUtil.encrypt(newMember.getId()),
                        eventId,
                        newMember.getUserId(), newMember.getName(), newMember.getEmail()
                ));
        OutboxEvent eventBox = OutboxEvent.builder()
                .id(eventId)
                .aggregateType("User")
                .eventType("UserCreated")
                .payload(payload).build();
        eventPublisher.publishEvent(eventBox);
        return new TokenResponse(token.getAccessToken(),token.getRefreshToken());
    }

    @Transactional
    public TokenResponse signInProcess(SignInRequest signInRequest) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(signInRequest.getUserId(),signInRequest.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        // 만약 로그인 실패 시 해당 로직은 실행되지 않음.
        TokenResponse token = jwtTokenProvider.generateToken(authentication);
        String userId = signInRequest.getUserId();
        jwtRedisHandler.save(userId,token.getRefreshToken());
        return token;

    }

    public TokenResponse reissueTokenProcess(TokenRequest tokenRequest){

        String targetRefreshToken = tokenRequest.getRefreshToken();

        Authentication authentication;
        try {
            authentication = jwtTokenProvider.validateToken(targetRefreshToken);
            log.info("Verification RefreshToken - {}",targetRefreshToken);
        } catch (JWTVerificationException e){
            // 기간 만료 혹은 잘못된 JWT 토큰일 경우
            log.info("Not Valid Refresh Token -{}",targetRefreshToken);
            throw new ApiException(ApiErrorCode.REFRESH_TOKEN_EXPIRED);
        }
        String targetUserId = jwtTokenProvider.getUserIdFromToken(tokenRequest.getRefreshToken());
        Optional<String> redisRefreshToken = jwtRedisHandler.findByUserId(targetUserId);
        if (redisRefreshToken.isEmpty()){
            log.info("Not Exists Refresh Token In Redis DB -{} -{}",targetUserId,targetRefreshToken);
            throw new ApiException(ApiErrorCode.REFRESH_TOKEN_NOT_EXIST);
        }else{
            if (!targetRefreshToken.equals(redisRefreshToken.get())){
                log.info("Not Equals Refresh Token In Redis DB -{} -{}",targetUserId,targetRefreshToken);
                throw new ApiException(ApiErrorCode.REFRESH_TOKEN_NOT_EQUAL);
            }
        }
        TokenResponse newTokenDto = jwtTokenProvider.generateToken(authentication);
        log.info("New Refresh Token -{} -{}",targetUserId,newTokenDto.getRefreshToken());
        jwtRedisHandler.save(targetUserId,newTokenDto.getRefreshToken());
        return newTokenDto;
    }

    public boolean logoutProcess(PrincipalDetails userDetails){
        String userId = userDetails.getUsername();
        Optional<String> redisRefreshToken = jwtRedisHandler.findByUserId(userId);
        if (redisRefreshToken.isPresent()){
            log.info("Logout User. User Id -{}",userId);
            jwtRedisHandler.delete(userId);
        }else{
            log.info("Already Logout User. User Id -{}",userId);
            return false;
        }
        SecurityContextHolder.clearContext();
        return true;
    }
}
