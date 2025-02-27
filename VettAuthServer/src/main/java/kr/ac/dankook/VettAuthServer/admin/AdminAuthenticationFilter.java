package kr.ac.dankook.VettAuthServer.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.ac.dankook.VettAuthServer.exception.ApiErrorCode;
import kr.ac.dankook.VettAuthServer.exception.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminAuthenticationFilter implements Filter {

    @Value("${admin.secret}")
    private String ADMIN_SECRET;
    @Value("${admin.header.name}")
    private String HEADER_NAME;

    private final ObjectMapper objectMapper;

    private boolean isValidApiKey(String apiKey) {

        try {
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(ADMIN_SECRET.getBytes(), "HmacSHA256");
            hmac.init(keySpec);

            byte[] hash = hmac.doFinal(ADMIN_SECRET.getBytes());
            String expectedKey = Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
            return expectedKey.equals(apiKey);

        } catch (Exception e) {
            log.error("Error verifying Admin API Key - ", e);
            return false;
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {
        log.info("AdminAuthenticationFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String apiKey = httpRequest.getHeader(HEADER_NAME);

        if (apiKey == null || !isValidApiKey(apiKey)) {
            log.warn("Unauthorized Admin API Key access attempt : {}", apiKey);
            ErrorResponse errorResponse = new ErrorResponse(ApiErrorCode.ADMIN_DECRYPT_ERROR);
            String body = objectMapper.writeValueAsString(errorResponse);

            httpResponse.setStatus(errorResponse.getStatusCode());
            httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
            httpResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());
            httpResponse.getWriter().write(body);
            return;
        }
        log.info("Admin API Key authentication successful");
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        log.info("AdminAuthenticationFilter destroyed");
    }
}
