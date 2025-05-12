package kr.ac.dankook.VettLLMChatServer.repository;


import kr.ac.dankook.VettLLMChatServer.entity.LLMChat;
import kr.ac.dankook.VettLLMChatServer.entity.LLMChatSection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LLMChatRepository extends JpaRepository<LLMChat, Long> {
    List<LLMChat> findByLlmChatSection(LLMChatSection section);
}
