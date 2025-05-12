package kr.ac.dankook.VettLLMChatServer.service;


import kr.ac.dankook.MemberSync;
import kr.ac.dankook.VettLLMChatServer.entity.Member;
import kr.ac.dankook.VettLLMChatServer.mapper.MemberEvent;
import kr.ac.dankook.VettLLMChatServer.mapper.MemberEventMapper;
import kr.ac.dankook.VettLLMChatServer.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberConsumerService {

    private final MemberEventMapper memberEventMapper;
    private final MemberGrpcService memberGrpcService;
    private final MemberRepository memberRepository;

    @KafkaListener(groupId = "NaHC_LLM_CHAT", topics = "USER_MODIFIED")
    public void consumeFromUserModified(
            String message, Acknowledgment acknowledgment){
        MemberEvent memberEvent = memberEventMapper.jsonToEventMemberObject(message);
        MemberSync.MemberSyncResponse grpcResponse = memberGrpcService.sendMemberTraceInfo(memberEvent.getId());
        if (checkMemberValidation(memberEvent,grpcResponse)){
            Optional<Member> editMember = memberRepository.findById(memberEvent.getId());
            if (editMember.isPresent()){
                Member member = editMember.get();
                member.setUserId(memberEvent.getUserId());
                member.setEmail(memberEvent.getEmail());
                member.setName(memberEvent.getName());
                memberRepository.save(member);
            }
        }
        acknowledgment.acknowledge();
        log.info("User Modified consumed message: {}",message);
    }

    @KafkaListener(groupId = "NaHC_LLM_CHAT",topics = "USER_CREATED")
    public void consumeFromUserCreated(
            String message, Acknowledgment acknowledgment){
        MemberEvent memberEvent = memberEventMapper.jsonToEventMemberObject(message);
        MemberSync.MemberSyncResponse grpcResponse = memberGrpcService.sendMemberTraceInfo(memberEvent.getId());
        if (checkMemberValidation(memberEvent,grpcResponse)){
            Member newMember = Member.builder()
                    .name(memberEvent.getName())
                    .email(memberEvent.getEmail())
                    .id(memberEvent.getId())
                    .userId(memberEvent.getUserId()).build();
            memberRepository.save(newMember);
        }else{
            Member newMember = Member.builder()
                    .name(grpcResponse.getName())
                    .email(grpcResponse.getEmail())
                    .id(grpcResponse.getId())
                    .userId(grpcResponse.getUserId()).build();
            memberRepository.save(newMember);
        }
        acknowledgment.acknowledge();
        log.info("User Created consumed message: {}",message);
    }

    private boolean checkMemberValidation(MemberEvent memberEvent, MemberSync.MemberSyncResponse grpcResponse){
        return memberEvent.getId().equals(grpcResponse.getId()) &&
                memberEvent.getEmail().equals(grpcResponse.getEmail()) &&
                memberEvent.getName().equals(grpcResponse.getName()) &&
                memberEvent.getUserId().equals(grpcResponse.getUserId());
    }

    @KafkaListener(groupId = "NaHC_LLM_CHAT",topics = "USER_DELETED")
    public void consumeFromUserDeleted(
            String message,Acknowledgment acknowledgment){
        MemberEvent memberEvent = memberEventMapper.jsonToEventMemberObject(message);
        memberRepository.deleteById(memberEvent.getId());
        acknowledgment.acknowledge();
        log.info("User Deleted consumed message: {}",message);
    }
}
