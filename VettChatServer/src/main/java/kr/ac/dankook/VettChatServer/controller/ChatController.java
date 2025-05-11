package kr.ac.dankook.VettChatServer.controller;

import kr.ac.dankook.VettChatServer.dto.request.ChatMessageRequest;
import kr.ac.dankook.VettChatServer.dto.response.ApiMessageResponse;
import kr.ac.dankook.VettChatServer.dto.response.ApiResponse;
import kr.ac.dankook.VettChatServer.dto.response.ChatResponse;
import kr.ac.dankook.VettChatServer.service.ChatRedisService;
import kr.ac.dankook.VettChatServer.service.ChatRoomService;
import kr.ac.dankook.VettChatServer.service.ChatService;
import kr.ac.dankook.VettChatServer.util.DecryptId;
import kr.ac.dankook.VettChatServer.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatService chatService;
    private final ChatRoomService chatRoomService;
    private final ChatRedisService chatRedisService;

    @GetMapping("/chat/message/{roomId}")
    public Mono<ResponseEntity<List<ChatResponse>>> getChatMessages(@PathVariable("roomId") String roomId) {
        Flux<ChatResponse> chatResponses = chatService.findAllChatMessagesProcess(roomId);
        return chatResponses.collectList().map(ResponseEntity::ok);
    }
    @PostMapping("/chat/unread-clear/{roomId}/{memberId}")
    public ResponseEntity<ApiMessageResponse> clearUnreadCount(
            @PathVariable @DecryptId Long roomId,
            @PathVariable String memberId
    ){
        chatRedisService.clearUnreadCount(roomId,memberId);
        return ResponseEntity.ok(new ApiMessageResponse
                (true,200,"Clear unread count"));
    }

    @GetMapping("/chat/unread-count/{roomId}/{memberId}")
    public ResponseEntity<ApiResponse<Integer>> getUnreadCount(
            @PathVariable @DecryptId Long roomId,
            @PathVariable String memberId
    ){
        return ResponseEntity.ok(new ApiResponse<>(200,
                chatRedisService.getUnreadCount(roomId,memberId)));
    }

    @MessageMapping("/chat/message")
    public Mono<ResponseEntity<Void>> receiveMessage(
            @Payload ChatMessageRequest chatMessageRequest){

        return chatService.saveChatMessageProcess(chatMessageRequest).publishOn(Schedulers.boundedElastic()).doOnNext(message -> {
            chatRoomService.saveLastMessageProcess(EncryptionUtil.decrypt(chatMessageRequest.getRoomId()),chatMessageRequest.getContent());

            Long roomId = EncryptionUtil.decrypt(chatMessageRequest.getRoomId());
            chatRedisService.updateAllUnreadCount(roomId);

            messagingTemplate.convertAndSend("/sub/chatroom/" + chatMessageRequest.getRoomId(),
                    chatService.convertChatMessageToChatResponse(message));
        }).thenReturn(ResponseEntity.ok().build());
    }
}
