package kr.ac.dankook.VettLLMChatServer.repository;

import kr.ac.dankook.VettLLMChatServer.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, String> { }
