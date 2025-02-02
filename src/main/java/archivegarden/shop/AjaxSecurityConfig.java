package archivegarden.shop;

import archivegarden.shop.security.common.AjaxAuthenticationEntryPoint;
import archivegarden.shop.security.handler.AjaxAccessDeniedHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

@Order(0)
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
@RequiredArgsConstructor
public class AjaxSecurityConfig {

    @Bean
    public SecurityFilterChain ajaxFilterChain(HttpSecurity http) throws Exception {

        http
                .securityMatcher("/ajax/**")
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/ajax/member/**", "/ajax/admin/check/**").anonymous()
                        .requestMatchers("/ajax/admin/**").hasRole("ADMIN")
                        .requestMatchers("/ajax/**").hasRole("USER")
                        .anyRequest().authenticated()
                );

        http
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new AjaxAuthenticationEntryPoint())
                        .accessDeniedHandler(ajaxAccessDeniedHandler())
                );

        return http.build();
    }

    @Bean
    public AccessDeniedHandler ajaxAccessDeniedHandler() {
        return new AjaxAccessDeniedHandler();
    }
}
