package kr.ac.dankook.VettDiagnosisServer.repository;

import kr.ac.dankook.VettDiagnosisServer.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, String> { }

