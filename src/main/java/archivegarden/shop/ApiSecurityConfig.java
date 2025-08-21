package archivegarden.shop;

import archivegarden.shop.security.common.ApiAuthenticationEntryPoint;
import archivegarden.shop.security.handler.ApiAccessDeniedHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

@Order(0)
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class ApiSecurityConfig {

    private final ObjectMapper objectMapper;

    public ApiSecurityConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    public SecurityFilterChain apiFilterChain(HttpSecurity http,
                                              @Qualifier("apiAuthenticationEntryPoint") AuthenticationEntryPoint authenticationEntryPoint,
                                              @Qualifier("apiAccessDeniedHandler") AccessDeniedHandler accessDeniedHandler) throws Exception {

        http
                .securityMatcher("/api/**")
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/check/login", "/api/inquiries", "/api/inquiries/{id}").permitAll()
                        .requestMatchers("/api/member/**", "/api/find-id/**", "/api/find-password/**",
                                                    "/api/admin/login-id/**", "/api/admin/email/**").anonymous()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/**").hasRole("USER")
                        .anyRequest().authenticated()
                );

        http
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                );

        return http.build();
    }

    @Bean(name = "apiAuthenticationEntryPoint")
    public AuthenticationEntryPoint apiAuthenticationEntryPoint() {
        return new ApiAuthenticationEntryPoint(objectMapper);
    }

    @Bean(name = "apiAccessDeniedHandler")
    public AccessDeniedHandler apiAccessDeniedHandler() {
        return new ApiAccessDeniedHandler(objectMapper);
    }
}
