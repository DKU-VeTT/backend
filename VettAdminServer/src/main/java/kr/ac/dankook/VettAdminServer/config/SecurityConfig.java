package kr.ac.dankook.VettAdminServer.config;

import kr.ac.dankook.VettAdminServer.config.principal.PrincipalDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    private final PrincipalDetailsService principalDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .headers((headerConfig) ->
                        headerConfig.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .authorizeHttpRequests((authorizeRequests) ->
                    authorizeRequests.requestMatchers(
                            "/css/**","/js/**","/assets/**",
                            "/admin/auth/**","/actuator/health", "/eureka/**"
                    ).permitAll().anyRequest().hasRole("ADMIN")
                )
                .exceptionHandling((exceptionConfig) ->
                    exceptionConfig.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/admin/auth/login"))
                            .accessDeniedHandler((request,response,accessDeniedException) -> {
                                response.sendRedirect("/admin/auth/except");
                            })
                )
                .formLogin((formLogin) ->
                    formLogin.loginPage("/admin/auth/login")
                            .usernameParameter("userId")
                            .passwordParameter("password")
                            .loginProcessingUrl("/admin/auth/login-proc")
                            .failureUrl("/admin/auth/login?error=true")
                            .defaultSuccessUrl("/admin/main",true)
                            .permitAll()
                )
                .logout((logoutConfig) ->
                    logoutConfig.logoutUrl("/admin/auth/logout")
                            .deleteCookies("JSESSIONID")
                            .invalidateHttpSession(true)
                            .logoutSuccessUrl("/admin/auth/login")
                )
                .userDetailsService(principalDetailsService);
                return http.build();
    }
}
