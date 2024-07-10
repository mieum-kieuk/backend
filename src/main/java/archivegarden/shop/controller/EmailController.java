package archivegarden.shop.controller;

import archivegarden.shop.dto.ResultResponse;
import archivegarden.shop.service.email.EmailService;
import jakarta.servlet.http.HttpServletRequest;
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

    @GetMapping("/verification/link")
    public String verifyEmailLink(@RequestParam(name = "address") String address, @RequestParam(name = "uuid") String uuid) {
        return emailService.verifyEmailLink(address, uuid);
    }

    //임시 비밀번호 이메일로 전송
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/temp-password")
    public ResultResponse tempPassword(@RequestParam(name = "email") String email, HttpServletRequest request) {
        //임시 비밀번호 전송
        emailService.sendTempPassword(email);

        //임시 비밀번호 전송된 이메일 세션에 저장
        HttpSession session = request.getSession();
        session.setAttribute("findPasswordSendEmail", email);
        return new ResultResponse(HttpStatus.OK.value(), "임시 비밀번호 전송에 성공했습니다.");
    }
}
