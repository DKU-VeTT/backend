package kr.ac.dankook.VettGatewayServer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.regex.Pattern;

@Component
@Slf4j
public class AdminAuthenticationFilter implements GlobalFilter {

    @Value("${admin.secret}")
    private String ADMIN_SECRET;
    @Value("${admin.header.name}")
    private String HEADER_NAME;

    private boolean isValidApiKey(String apiKey) {

        try {
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(ADMIN_SECRET.getBytes(), "HmacSHA256");
            hmac.init(keySpec);

            byte[] hash = hmac.doFinal(ADMIN_SECRET.getBytes());
            String expectedKey = Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
            return expectedKey.equals(apiKey);

        } catch (Exception e) {
            log.error("Error verifying API Key - ", e);
            return false;
        }
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        if (!Pattern.matches("/admin/.+", path)) {
            return chain.filter(exchange);
        }
        // /admin/ + 1글자 이상시 필터 진행
        log.info("Admin Authentication in Resource Server -{}", path);

        String apiKey = exchange.getRequest().getHeaders().getFirst(HEADER_NAME);

        if (apiKey == null || !isValidApiKey(apiKey)) {
            log.warn("Unauthorized API Key access attempt: {}", apiKey);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        log.info("API Key authentication successful");
        return chain.filter(exchange);
    }

}
