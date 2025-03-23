package archivegarden.shop.controller.user.email;

import archivegarden.shop.dto.ResultResponse;
import archivegarden.shop.entity.Member;
import archivegarden.shop.service.user.email.EmailService;
import archivegarden.shop.web.annotation.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ajax/email")
@RequiredArgsConstructor
public class EmailAjaxController {

    private final EmailService emailService;

    /**
     * 이메일 인증 메일 전송 요청을 처리하는 메서드
     */
    @PostMapping("/verification")
    @PreAuthorize("#loginMember.loginId == principal.username")
    public ResultResponse verifyEmailLink(@CurrentUser Member loginMember) {
        emailService.sendValidationRequestEmailInMyPage(loginMember.getEmail(), loginMember.getName());
        return new ResultResponse(HttpStatus.OK.value(), "이메일 인증 요청 메일을 전송했습니다.");
    }
}
