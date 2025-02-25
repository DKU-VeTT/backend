package kr.ac.dankook.VettAdminServer.service;

import kr.ac.dankook.VettAdminServer.entity.Member;
import kr.ac.dankook.VettAdminServer.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public boolean isValidAdminOldPassword(Long adminPrimaryKey, String oldPassword){
        Optional<Member> admin = memberRepository.findById(adminPrimaryKey);
        return admin.filter(member -> passwordEncoder.matches(oldPassword, member.getPassword())).isPresent();
    }
}
