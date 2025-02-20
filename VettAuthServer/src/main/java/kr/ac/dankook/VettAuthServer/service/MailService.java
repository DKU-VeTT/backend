package kr.ac.dankook.VettAuthServer.service;

import kr.ac.dankook.VettAuthServer.dto.response.MailResponse;
import kr.ac.dankook.VettAuthServer.entity.Member;
import kr.ac.dankook.VettAuthServer.exception.ApiErrorCode;
import kr.ac.dankook.VettAuthServer.exception.ApiException;
import kr.ac.dankook.VettAuthServer.exception.MailException;
import kr.ac.dankook.VettAuthServer.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final MailSender mailSender;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String,String> redisTemplate;
    private static final long AUTH_MAIL_TOKEN_EXPIRE_TIME = 1000 * 60 * 15; // 15분

    public MailResponse createMailResponse(Member member){
        String verificationCode = generateVerificationCode();
        saveVerificationCode(member, verificationCode);
        return MailResponse.builder()
                .mailAddress(member.getEmail())
                .title("VETT 인증번호 안내 이메일 입니다.")
                .content("안녕하세요. VETT 인증번호 안내 관련 이메일 입니다. 회원님의 인증 번호는 " + verificationCode + " 입니다. 인증 후에 비밀번호를 변경을 해주세요.")
                .build();
    }

    public void sendMail(MailResponse mailResponse){
        SimpleMailMessage message = new SimpleMailMessage();
        try{
            message.setTo(mailResponse.getMailAddress());
            message.setSubject(mailResponse.getTitle());
            message.setText(mailResponse.getContent());
            message.setFrom("ogyuchan01@gmail.com");
            message.setReplyTo("ogyuchan01@gmail.com");
            log.info("Sending email to: {}", mailResponse.getMailAddress());
            mailSender.send(message);
        }catch(Exception ex){
            throw new MailException(ApiErrorCode.MAIL_SEND_ERROR);
        }
    }

    @Transactional
    public void changePasswordByEmailProcess(String authMailToken, String newPassword){

        String userId = authMailToken.split("_")[1];
        Member member = memberRepository.findByUserId(userId).orElseThrow(() ->
                new ApiException(ApiErrorCode.MEMBER_NOT_FOUND));
        member.setPassword(passwordEncoder.encode(newPassword));
        memberRepository.save(member);
        delete(authMailToken);
    }

    @SuppressWarnings("ConstantConditions")
    public boolean isVerifyCodeProcess(String authMailToken,String verifyCode){
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String storedCode = valueOperations.get(authMailToken);
        return storedCode != null && storedCode.equals(verifyCode);
    }

    private String generateVerificationCode(){
        return RandomStringUtils.randomAlphanumeric(10);
    }

    @SuppressWarnings("ConstantConditions")
    private void saveVerificationCode(Member member, String verificationCode) {

        String authMailToken = member.getEmail()+"_"+member.getUserId();
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        String existingToken = operations.get(authMailToken);
        if (existingToken != null && !existingToken.isEmpty()) {
            delete(authMailToken);
        }
        operations.set(authMailToken, verificationCode, AUTH_MAIL_TOKEN_EXPIRE_TIME, TimeUnit.SECONDS);
    }
    public void delete(String authMailToken){
        redisTemplate.delete(authMailToken);
    }
}
