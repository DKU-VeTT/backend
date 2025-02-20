package kr.ac.dankook.VettAuthServer.jwt;

import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.ac.dankook.VettAuthServer.exception.TokenErrorCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtErrorResponseHandler jwtErrorResponseHandler;
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/auth/identity/") ||
                requestURI.startsWith("/actuator/health") ||
                requestURI.startsWith("/eureka/")) {
            filterChain.doFilter(request, response);
            return;
        }
        String authToken = resolveToken(request);
        if (!StringUtils.hasText(authToken)) {
            jwtErrorResponseHandler.sendErrorResponseProcess(response,
                    TokenErrorCode.UNAUTHORIZED_ACCESS_TOKEN_REQUIRED);
            return;
        }
        try{
            Authentication authentication = jwtTokenProvider.validateToken(authToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }catch (JWTVerificationException e) {
            request.setAttribute("exception", e);
        }
        filterChain.doFilter(request,response);
    }

    private String resolveToken(HttpServletRequest request){
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)){
            return bearerToken.substring(7);
        }
        return null;
    }
}
