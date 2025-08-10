package archivegarden.shop.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

public class MemberAccessDeniedHandler implements AccessDeniedHandler {

    private String errorPage;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {

        String deniedUrl = errorPage;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isRoleUser = authentication != null &&
                authentication.getAuthorities().stream()
                        .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER"));

        if (isRoleUser) {
            response.sendError(HttpStatus.FORBIDDEN.value(), "로그인한 사용자는 이용할 수 없는 기능입니다.");
        } else {
            HttpSession session = request.getSession();
            if (session != null) {
                session.invalidate();
                deniedUrl = "/login";
            }

            response.sendRedirect(deniedUrl);
        }
    }

    public void setErrorPage(String errorPage) {
        this.errorPage = errorPage;
    }
}
