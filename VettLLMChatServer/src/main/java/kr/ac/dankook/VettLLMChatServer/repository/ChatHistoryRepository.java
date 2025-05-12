package kr.ac.dankook.VettLLMChatServer.repository;

import kr.ac.dankook.VettLLMChatServer.document.ChatHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatHistoryRepository extends MongoRepository<ChatHistory,String> {
    List<ChatHistory> findBySessionId(String sessionId);
}
