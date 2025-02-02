package archivegarden.shop.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private String errorPage;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {

        //ADMIN 권한 없는 페이지에 접근할 경우 홈으로 이동
        String deniedUrl = errorPage;

        //이미 로그인한 경우
        String requestURI = request.getRequestURI();
        if(requestURI.startsWith("/admin/login") || requestURI.startsWith("/admin/join")) {
            deniedUrl = "/admin";
        }

        response.sendRedirect(deniedUrl);
    }

    public void setErrorPage(String errorPage) {
        this.errorPage = errorPage;
    }
}
