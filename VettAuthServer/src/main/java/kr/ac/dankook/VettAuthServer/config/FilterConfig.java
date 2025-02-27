package kr.ac.dankook.VettAuthServer.config;

import jakarta.servlet.Filter;
import kr.ac.dankook.VettAuthServer.admin.AdminAuthenticationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<Filter> adminAuthenticationFilterBean(AdminAuthenticationFilter adminAuthenticationFilter) {
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(adminAuthenticationFilter);
        registrationBean.addUrlPatterns("/admin/auth/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }
}
