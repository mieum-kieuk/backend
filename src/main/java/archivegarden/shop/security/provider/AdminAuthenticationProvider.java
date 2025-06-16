package archivegarden.shop.security.provider;

import archivegarden.shop.security.exception.IllegalLoginException;
import archivegarden.shop.security.service.AdminContext;
import archivegarden.shop.security.token.AdminAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

public class AdminAuthenticationProvider implements AuthenticationProvider {

    private PasswordEncoder passwordEncoder;
    private UserDetailsService userDetailsService;

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String loginId = authentication.getName();
        String password = (String) authentication.getCredentials();

        if(!StringUtils.hasText(loginId)) {
            throw new IllegalLoginException("아이디를 입력해 주세요.");
        }

        if(!StringUtils.hasText(password)) {
            throw new IllegalLoginException("비밀번호를 입력해 주세요.");
        }

        AdminContext adminContext = (AdminContext) userDetailsService.loadUserByUsername(loginId);

        if(adminContext == null || !passwordEncoder.matches(password, adminContext.getPassword())) {
            throw new BadCredentialsException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        return new AdminAuthenticationToken(adminContext, null, adminContext.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(AdminAuthenticationToken.class);
    }
}
