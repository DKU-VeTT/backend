package kr.ac.dankook.VettChatServer.repository;

import kr.ac.dankook.VettChatServer.document.ChatMessage;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface ChatMessageRepository extends ReactiveMongoRepository<ChatMessage, String> {
    Flux<ChatMessage> findAllByChatRoomId(String chatRoomId);
}
