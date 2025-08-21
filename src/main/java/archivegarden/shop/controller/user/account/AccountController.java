package archivegarden.shop.controller.user.account;

import archivegarden.shop.dto.user.member.FindIdResultDto;
import archivegarden.shop.entity.auth.TokenType;
import archivegarden.shop.exception.global.EmailSendFailedException;
import archivegarden.shop.service.user.account.AccountService;
import archivegarden.shop.service.user.email.EmailService;
import archivegarden.shop.service.user.token.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AccountController {

    private final EmailService emailService;
    private final AccountService accountService;
    private final TokenService tokenService;

    @GetMapping("/find-id")
    public String findId() {
        return "user/account/find_id";
    }

    @GetMapping("/find-id/complete")
    public String findIdResult(
            @RequestParam(name = "token", required = false) String token,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if(token == null) {
            redirectAttributes.addFlashAttribute("error", "요청이 만료되었거나 유효하지 않습니다.\n아이디 찾기를 다시 진행해 주세요.");
            return "redirect:/find-id";
        }

        Optional<Long> memberIdOpt = tokenService.verifyAndUse(token, TokenType.FIND_LOGIN_ID);
        if (memberIdOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "요청이 만료되었거나 유효하지 않습니다.\n아이디 찾기를 다시 진행해 주세요.");
            return "redirect:/find-id";
        }

        FindIdResultDto findIdResultDto = accountService.findIdResult(memberIdOpt.get());
        model.addAttribute("member", findIdResultDto);
        return "user/account/find_id_complete";
    }

    @GetMapping("/find-password")
    public String findPassword(Model model) {
        return "user/account/find_password";
    }

    @GetMapping("/find-password/complete")
    public String findPasswordResult(
            @RequestParam(name = "token", required = false) String token,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if(token == null) {
            redirectAttributes.addFlashAttribute("error", "요청이 만료되었거나 유효하지 않습니다.\n비밀번호 찾기를 다시 진행해 주세요.");
            return "redirect:/find-id";
        }

        Optional<Long> memberIdOpt = tokenService.verifyAndUse(token, TokenType.FIND_PASSWORD);
        if (memberIdOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "요청이 만료되었거나 유효하지 않습니다.\n비밀번호 찾기를 다시 진행해 주세요.");
            return "redirect:/find-password";
        }

        try {
            String email = emailService.sendTempPassword(memberIdOpt.get());
            model.addAttribute("email", email);
            return "user/account/find_password_complete";
        } catch (EmailSendFailedException e) {
            log.warn("[EmailSendFailedApiException] 임시 비밀번호 전송 실패 memberId={}, reason={}", memberIdOpt.get(), e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "메일 발송에 실패했습니다.\n잠시 후 다시 시도해 주세요.");
            return "redirect:/find-password";
        }
    }
}
