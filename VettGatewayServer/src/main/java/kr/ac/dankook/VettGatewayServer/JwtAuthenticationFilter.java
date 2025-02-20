package kr.ac.dankook.VettGatewayServer;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Component
@Slf4j
public class JwtAuthenticationFilter implements GlobalFilter {

    @Value("${jwt.secret}")
    private String secretKey;
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (path.startsWith("/auth/")) {
            return chain.filter(exchange);
        }
        log.info("Jwt Authentication in Resource Server -{}",path);
        String token = extractToken(exchange);
        if (token == null) {
            exchange.getResponse().getHeaders().add("Access-Control-Expose-Headers", "Token-Error-Message");
            exchange.getResponse().getHeaders().add("Token-Error-Message", "Token Not Exist");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        if (!validateToken(token,exchange)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
    }

    @SuppressWarnings("ConstantConditions")
    private String extractToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(AUTHORIZATION_HEADER);
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(7);
        }
        return null;
    }

    private boolean validateToken(String token, ServerWebExchange exchange){
        try{
            String payload = JWT.require(Algorithm.HMAC512(secretKey))
                    .build().verify(token).getPayload();
            log.info("JWT Token Payload - {}",payload);
            return true;
        }catch (JWTVerificationException e){
            exchange.getResponse().getHeaders().add("Access-Control-Expose-Headers", "Token-Error-Message");
            if (e instanceof TokenExpiredException){
                exchange.getResponse().getHeaders().add("Token-Error-Message", "Token Expired");
            }else{
                exchange.getResponse().getHeaders().add("Token-Error-Message", e.getMessage());
            }
            return false;
        }
    }
}
