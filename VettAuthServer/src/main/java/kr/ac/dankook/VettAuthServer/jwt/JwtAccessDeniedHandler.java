package kr.ac.dankook.VettAuthServer.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.ac.dankook.VettAuthServer.exception.TokenErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final JwtErrorResponseHandler jwtErrorResponseHandler;

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException {
        jwtErrorResponseHandler.sendErrorResponseProcess(response, TokenErrorCode.FORBIDDEN_UNAUTHORIZED_ACTION);
    }
}