package kr.ac.dankook.VettLLMChatServer.repository;


import kr.ac.dankook.VettLLMChatServer.entity.LLMChatSection;
import kr.ac.dankook.VettLLMChatServer.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LLMChatSectionRepository extends JpaRepository<LLMChatSection,Long> {
    List<LLMChatSection> findByMember(Member member);
}
