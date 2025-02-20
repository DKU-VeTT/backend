package kr.ac.dankook.VettAuthServer.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import kr.ac.dankook.VettAuthServer.exception.ErrorResponse;
import kr.ac.dankook.VettAuthServer.exception.TokenErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class JwtErrorResponseHandler {

    private final ObjectMapper objectMapper;

    public void sendErrorResponseProcess(
            HttpServletResponse response, TokenErrorCode errorCode)
            throws IOException {
        ErrorResponse errorResponse = new ErrorResponse(errorCode);
        String body = objectMapper.writeValueAsString(errorResponse);
        response.setStatus(errorResponse.getStatusCode());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(body);
    }
}
