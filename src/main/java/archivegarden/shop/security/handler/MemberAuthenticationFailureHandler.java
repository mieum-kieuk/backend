package archivegarden.shop.security.handler;

import archivegarden.shop.security.exception.IllegalLoginException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static archivegarden.shop.constant.SessionConstants.MEMBER_LOGIN_ERROR;

@Slf4j
@Component
public class MemberAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        String errorMessage = "아이디 또는 비밀번호를 확인해 주세요.";

        if(exception instanceof IllegalLoginException) {
            errorMessage = exception.getMessage();
            log.info("[{}] {} cause=[{}, message={}]", "MemberAuthenticationProvider.authenticate()", "아이디 또는 비밀번호를 입력하지 않음", "UsernameNotFoundException", exception.getMessage());
        } else if(exception instanceof UsernameNotFoundException) {
            errorMessage = "해당 아이디로 가입된 계정이 존재하지 않습니다.";
            log.info("[{}] {} cause=[{}, message={}]", "MemberUserDetailsService.loadUserByUsername()", "존재하지 않는 아이디로 로그인 시도", "UsernameNotFoundException", exception.getMessage());
        } else if(exception instanceof BadCredentialsException) {
            errorMessage = "비밀번호가 일치하지 않습니다.\n비밀번호를 확인해 주세요.";
            log.info("[{}] {} cause=[{}, message={}]", "MemberAuthenticationProvider.authenticate()", "비밀번호가 일치하지 않음", "BadCredentialsException", exception.getMessage());
        }

        request.getSession().setAttribute(MEMBER_LOGIN_ERROR, errorMessage);
        setDefaultFailureUrl("/login");

        super.onAuthenticationFailure(request, response, exception);
    }
}
