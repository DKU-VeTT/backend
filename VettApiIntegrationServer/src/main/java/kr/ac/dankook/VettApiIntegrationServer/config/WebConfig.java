package kr.ac.dankook.VettApiIntegrationServer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final String DEVELOP_FRONT_ADDRESS = "http://localhost:5173";
    private static final String PROD_FRONT_ADDRESS = "https://web-vett-frontend-ss7z32llwmafmaz.sel5.cloudtype.app";
    private static final String PROD_ADMIN_ADDRESS = "https://port-0-vett-admin-ss7z32llwmafmaz.sel5.cloudtype.app";

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(DEVELOP_FRONT_ADDRESS, PROD_FRONT_ADDRESS, PROD_ADMIN_ADDRESS)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("location")
                .allowCredentials(true);
    }
}
