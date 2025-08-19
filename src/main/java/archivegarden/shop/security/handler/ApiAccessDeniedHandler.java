package archivegarden.shop.security.handler;

import archivegarden.shop.dto.common.ErrorResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.time.Instant;

public class ApiAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public ApiAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean hasRoleUser = authentication != null &&
                authentication.getAuthorities().stream()
                        .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER"));

        final String message = hasRoleUser
                ? "로그인한 사용자는 이용할 수 없는 기능입니다."
                : "죄송합니다. 해당 서비스에 대한 권한이 없습니다.";

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json;charset=UTF-8");
        ErrorResponseDto errorResponseDto = new ErrorResponseDto("FORBIDDEN", message, request.getRequestURI(), Instant.now());
        String json = objectMapper.writeValueAsString(errorResponseDto);
        response.getWriter().write(json);
    }
}
