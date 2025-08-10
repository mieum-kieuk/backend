package archivegarden.shop.controller.user.email;

import archivegarden.shop.service.user.email.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "회원-사용자", description = "사용자 페이지에서 이메일 인증 관련 화면을 처리하는 컨트롤러입니다.")
@Controller
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;

    @Operation(
            summary = "이메일 인증 처리",
            description = "사용자가 받은 이메일 인증 링크를 검증하고 인증을 완료합니다."
    )
    @GetMapping("/verify")
    public String verifyEmailLink(@RequestParam(name = "address") String address, @RequestParam(name = "uuid") String uuid) {
        return emailService.verifyEmail(address, uuid);
    }
}
