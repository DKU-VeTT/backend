package kr.ac.dankook.VettChatServer.repository;

import kr.ac.dankook.VettChatServer.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, String> { }
