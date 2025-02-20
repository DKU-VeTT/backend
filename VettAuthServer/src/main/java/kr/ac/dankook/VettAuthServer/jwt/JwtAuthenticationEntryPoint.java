package kr.ac.dankook.VettAuthServer.jwt;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.ac.dankook.VettAuthServer.exception.TokenErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final JwtErrorResponseHandler jwtErrorResponseHandler;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {

        JWTVerificationException jwtVerificationException =
                (JWTVerificationException) request.getAttribute("exception");

        if (authException instanceof BadCredentialsException) {
            jwtErrorResponseHandler.sendErrorResponseProcess(
                    response,
                    TokenErrorCode.UNAUTHORIZED_INVALID_CREDENTIALS
            );
            return;
        }
        // 토큰 만료의 경우 다른 응답
        if (jwtVerificationException instanceof TokenExpiredException) {
            jwtErrorResponseHandler.sendErrorResponseProcess(response,TokenErrorCode.CLIENT_CLOSED_REQUEST_ACCESS_TOKEN_EXPIRED);
            return;
        }
        // 유효한 토큰이 아닌 경우 다른 응답
        if (jwtVerificationException != null) {
            jwtErrorResponseHandler.sendErrorResponseProcess(response, TokenErrorCode.UNAUTHORIZED_INVALID_ACCESS_TOKEN);
            return;
        }
        // 토큰 처리 에러 응답
        jwtErrorResponseHandler.sendErrorResponseProcess(response, TokenErrorCode.INTERNAL_SERVER_ERROR_TOKEN_PARSING);
    }

}
