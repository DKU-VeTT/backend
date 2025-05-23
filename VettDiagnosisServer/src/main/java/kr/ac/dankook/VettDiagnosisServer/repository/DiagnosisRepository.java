package kr.ac.dankook.VettDiagnosisServer.repository;

import kr.ac.dankook.VettDiagnosisServer.entity.Diagnosis;
import kr.ac.dankook.VettDiagnosisServer.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {
    List<Diagnosis> findByMember(Member member);
}
