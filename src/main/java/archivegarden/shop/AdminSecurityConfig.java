package archivegarden.shop;

import archivegarden.shop.security.common.AdminAuthenticationEntryPoint;
import archivegarden.shop.security.filter.AdminAuthenticationFilter;
import archivegarden.shop.security.handler.AdminAuthenticationFailureHandler;
import archivegarden.shop.security.handler.AdminAuthenticationSuccessHandler;
import archivegarden.shop.security.handler.CustomAccessDeniedHandler;
import archivegarden.shop.security.manager.AdminAuthenticationManager;
import archivegarden.shop.security.provider.AdminAuthenticationProvider;
import archivegarden.shop.security.service.AdminUserDetailsService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@Order(1)
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class AdminSecurityConfig {

    private final PasswordEncoder passwordEncoder;
    private final AdminAuthenticationSuccessHandler authenticationSuccessHandler;
    private final AdminAuthenticationFailureHandler authenticationFailureHandler;

    @Bean
    public SecurityFilterChain adminFilterChain(HttpSecurity http) throws Exception {

        http
                .securityMatcher("/admin/**")
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/admin/join/**", "/admin/login").anonymous()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                );

        http
                .addFilterAt(adminAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        http
                .logout(form -> form
                        .logoutUrl("/admin/logout")
                        .logoutSuccessUrl("/admin/login")
                        .addLogoutHandler(((request, response, authentication) -> {
                                    HttpSession session = request.getSession();
                                    if (session != null) {
                                        session.invalidate();
                                    }
                                }))
                                .deleteCookies("remember-me")
                );

        http
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(adminAuthenticationEntryPoint())
                        .accessDeniedHandler(adminAccessDeniedHandler()));

        return http.build();
    }

    @Bean
    public AdminAuthenticationFilter adminAuthenticationFilter() {
        AdminAuthenticationFilter adminAuthenticationFilter = new AdminAuthenticationFilter();
        adminAuthenticationFilter.setAuthenticationManager(adminAuthenticationManager());
        adminAuthenticationFilter.setSecurityContextRepository(adminSecurityContextRepository());
        adminAuthenticationFilter.setFilterProcessesUrl("/admin/login");
        adminAuthenticationFilter.setUsernameParameter("loginId");
        adminAuthenticationFilter.setPasswordParameter("password");
        adminAuthenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        adminAuthenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler);
        return adminAuthenticationFilter;
    }

    @Bean
    public AuthenticationManager adminAuthenticationManager() {
        return new AdminAuthenticationManager(adminAuthenticationProvider());
    }

    @Bean
    public AuthenticationProvider adminAuthenticationProvider() {
        AdminAuthenticationProvider authenticationProvider = new AdminAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        authenticationProvider.setUserDetailsService(adminUserDetailsService());
        return authenticationProvider;
    }

    @Bean
    public UserDetailsService adminUserDetailsService() {
        return new AdminUserDetailsService();
    }

    @Bean
    public SecurityContextRepository adminSecurityContextRepository() {
        return new DelegatingSecurityContextRepository(new HttpSessionSecurityContextRepository(), new RequestAttributeSecurityContextRepository());
    }

    @Bean
    public AuthenticationEntryPoint adminAuthenticationEntryPoint() {
        return new AdminAuthenticationEntryPoint();
    }

    @Bean
    public AccessDeniedHandler adminAccessDeniedHandler() {
        CustomAccessDeniedHandler accessDeniedHandler = new CustomAccessDeniedHandler();
        accessDeniedHandler.setErrorPage("/accessDenied");
        return accessDeniedHandler;
    }
}
