package archivegarden.shop.controller.user.email;

import archivegarden.shop.dto.ResultResponse;
import archivegarden.shop.service.email.EmailService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;

    /**
     * 이메일 인증을 처리하는 메서드
     */
    @GetMapping("/verification")
    public String verifyEmailLink(@RequestParam(name = "address") String address, @RequestParam(name = "uuid") String uuid) {
        return emailService.verifyEmail(address, uuid);
    }

    /**
     * 사용자 이메일로 임시 비밀번호를 이메일로 전송하는 메서드
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/temp-password")
    public ResultResponse tempPassword(@RequestParam(name = "email") String email, HttpSession session) {
        emailService.sendTempPassword(email);
        session.setAttribute("findPassword:email", email);
        return new ResultResponse(HttpStatus.OK.value(), "임시 비밀번호가 이메일로 전송되었습니다.");
    }
}
