package kr.ac.dankook.VettChatServer.config.converter;

import kr.ac.dankook.VettChatServer.entity.Member;
import kr.ac.dankook.VettChatServer.exception.ApiErrorCode;
import kr.ac.dankook.VettChatServer.exception.ApiException;
import kr.ac.dankook.VettChatServer.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MemberEntityConverter {

    private final MemberRepository memberRepository;

    private Member getMemberFromRepository(String memberId){
        Optional<Member> targetMember = memberRepository.findById(memberId);
        if(targetMember.isPresent()){
            return targetMember.get();
        }
        throw new ApiException(ApiErrorCode.MEMBER_NOT_FOUND);
    }

    @Transactional(readOnly = true)
    public Member getMemberByMemberId(String memberId){
        return getMemberFromRepository(memberId);
    }
}
