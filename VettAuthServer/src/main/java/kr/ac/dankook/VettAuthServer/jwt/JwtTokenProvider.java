package kr.ac.dankook.VettAuthServer.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import kr.ac.dankook.VettAuthServer.config.principal.PrincipalDetails;
import kr.ac.dankook.VettAuthServer.dto.response.TokenResponse;
import kr.ac.dankook.VettAuthServer.entity.Member;
import kr.ac.dankook.VettAuthServer.repository.MemberRepository;
import kr.ac.dankook.VettAuthServer.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30; // 30분
    public static final long REFRESH_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 24 * 30; // 30일
    private final MemberRepository memberRepository;

    public TokenResponse generateToken(Authentication authentication) {

        // PrincipalDetails 객체 캐스팅
        // 로그인 완료 시 토큰 생성
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        log.info("Login User Id : {}",principalDetails.getMember().getUserId());

        long now = (new Date()).getTime();

        String accessToken = JWT.create()
                // principalDetails.getUsername() UserId ex) kyu1234
                // UserId를 통해 서명을 다시 한 후 유효성 Entity 검사까지 진행
                .withSubject(principalDetails.getUsername())
                .withExpiresAt(new Date(now + ACCESS_TOKEN_EXPIRE_TIME))
                .withClaim("key", EncryptionUtil.encrypt(principalDetails.getMember().getId()))
                .withClaim("userId",principalDetails.getMember().getUserId())
                .withClaim("name",principalDetails.getMember().getName())
                .sign(Algorithm.HMAC512(secretKey));

        // Refresh Token 생성
        String refreshToken = JWT.create()
                .withSubject(principalDetails.getUsername())
                .withExpiresAt(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
                .withClaim("key",EncryptionUtil.encrypt(principalDetails.getMember().getId()))
                .withClaim("userId",principalDetails.getMember().getUserId())
                .withClaim("name",principalDetails.getMember().getName())
                .sign(Algorithm.HMAC512(secretKey));
        return new TokenResponse(accessToken,refreshToken);
    }

    public Authentication validateToken(String jwtToken){
        String userId = getUserIdFromToken(jwtToken);
        Optional<Member> memberEntity = memberRepository.findByUserId(userId);
        if (memberEntity.isPresent()) {
            PrincipalDetails principalDetails = new PrincipalDetails(memberEntity.get());
            // Member Entity 권한이 주이므로 Session 객체의 목적은 Member 권한
            return new UsernamePasswordAuthenticationToken(
                    principalDetails, jwtToken, principalDetails.getAuthorities());
        }
        return null;
    }

    public String getUserIdFromToken(String jwtToken){
        return JWT.require(Algorithm.HMAC512(secretKey))
                .build().verify(jwtToken).getClaim("userId")
                .asString();
    }
}
