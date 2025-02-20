package kr.ac.dankook.VettAuthServer.repository;


import kr.ac.dankook.VettAuthServer.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long> {
    Optional<Member> findByUserId(String userId);
    boolean existsByUserId(String userId);
    List<Member> findByNameAndEmail(String name, String email);
}
