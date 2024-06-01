package archivegarden.shop.security.provider;

import archivegarden.shop.security.exception.IllegalLoginException;
import archivegarden.shop.security.service.AccountContext;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

public class MemberAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService userDetailsService;
    private PasswordEncoder passwordEncoder;

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String loginId = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();

        if(!StringUtils.hasText(loginId)) {
            throw new IllegalLoginException("아이디를 입력해 주세요.");
        }

        if(!StringUtils.hasText(password)) {
            throw new IllegalLoginException("비밀번호를 입력해 주세요.");
        }

        AccountContext accountContext = (AccountContext) userDetailsService.loadUserByUsername(loginId);

        if(accountContext == null || !passwordEncoder.matches(password, accountContext.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        return new UsernamePasswordAuthenticationToken(accountContext, null, accountContext.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
