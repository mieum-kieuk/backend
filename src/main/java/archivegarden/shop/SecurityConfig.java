package archivegarden.shop;

import archivegarden.shop.security.handler.CustomAccessDeniedHandler;
import archivegarden.shop.security.handler.MemberAuthenticationFailureHandler;
import archivegarden.shop.security.handler.MemberAuthenticationSuccessHandler;
import archivegarden.shop.security.provider.MemberAuthenticationProvider;
import archivegarden.shop.security.service.MemberUserDetailsService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

@Order(2)
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final MemberAuthenticationSuccessHandler authenticationSuccessHandler;
    private final MemberAuthenticationFailureHandler authenticationFailureHandler;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web
                .ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .authenticationProvider(memberAuthenticationProvider());

        http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/login", "/productImages/**", "/", "/members/**", "/error",
                                "/products/**", "/about/**", "/community/inquiry", "/community/inquiry/*", "/community/notice/**", "/search/**",
                                "/payment/webhook").permitAll()
                        .anyRequest().authenticated()
                );

        http
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/")
                        .usernameParameter("loginId")
                        .passwordParameter("password")
                        .loginProcessingUrl("/login")
                        .successHandler(authenticationSuccessHandler)
                        .failureHandler(authenticationFailureHandler)
                );

        http
                .logout(form -> form
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .addLogoutHandler(((request, response, authentication) -> {
                            HttpSession session = request.getSession();
                            if (session != null) {
                                session.invalidate();
                            }
                        }))
                        .logoutSuccessHandler(((request, response, authentication) -> {
                            response.sendRedirect("/login");
                        }))
                        .deleteCookies("remember-me")
                );

        http
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(memberAccessDeniedHandler())
                );

        http

                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/payment/webhook")
                );

        return http.build();
    }

    @Bean
    public AuthenticationProvider memberAuthenticationProvider() {
        MemberAuthenticationProvider memberAuthenticationProvider = new MemberAuthenticationProvider();
        memberAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        memberAuthenticationProvider.setUserDetailsService(memberUserDetailsService());
        return memberAuthenticationProvider;
    }

    @Bean
    public UserDetailsService memberUserDetailsService() {
        return new MemberUserDetailsService();
    }

    @Bean
    public AccessDeniedHandler memberAccessDeniedHandler() {
        CustomAccessDeniedHandler accessDeniedHandler = new CustomAccessDeniedHandler();
        accessDeniedHandler.setErrorPage("/");
        return accessDeniedHandler;
    }
}
