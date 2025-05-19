package kr.ac.dankook.VettChatServer.service;

import jakarta.transaction.Transactional;
import kr.ac.dankook.VettChatServer.document.ChatMessage;
import kr.ac.dankook.VettChatServer.dto.request.ChatMessageRequest;
import kr.ac.dankook.VettChatServer.dto.response.ChatResponse;
import kr.ac.dankook.VettChatServer.entity.Member;
import kr.ac.dankook.VettChatServer.repository.ChatMessageRepository;
import kr.ac.dankook.VettChatServer.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Flux<ChatResponse> findAllChatMessagesProcess(String roomId) {
        Flux<ChatMessage> chatMessages = chatMessageRepository.findAllByChatRoomId(roomId);
        return chatMessages.publishOn(Schedulers.boundedElastic()).map(this::convertChatMessageToChatResponse);
    }
    @Transactional
    public Mono<ChatMessage> saveChatMessageProcess(ChatMessageRequest messageRequest){
        ChatMessage newMessage = ChatMessage.builder()
                .content(messageRequest.getContent())
                .chatRoomId(messageRequest.getRoomId())
                .memberId(messageRequest.getMemberId())
                .createdTime(LocalDateTime.now())
                .build();
        return chatMessageRepository.save(newMessage);
    }
    public ChatResponse convertChatMessageToChatResponse(ChatMessage message){
        Optional<Member> targetMember = memberRepository.findById(message.getMemberId());
        String name = targetMember.isPresent() ? targetMember.get().getName() : "알 수 없음";
        String memberId = targetMember.isPresent() ? targetMember.get().getId() : "알 수 없음";
        return ChatResponse.builder()
                .content(message.getContent())
                .time(message.getCreatedTime())
                .name(name).memberId(memberId).build();
    }
}
