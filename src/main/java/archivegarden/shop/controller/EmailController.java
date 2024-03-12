package archivegarden.shop.controller;

import archivegarden.shop.dto.member.FindPasswordDto;
import archivegarden.shop.entity.FindAccountType;
import archivegarden.shop.service.email.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;

    @GetMapping("/verification/link")
    public String verifyEmailLink(@RequestParam(name = "address") String address, @RequestParam(name = "uuid") String uuid) {
        return emailService.verifyEmailLink(address, uuid);
    }

    @GetMapping("/temp-password")
    public String tempPassword(@RequestParam(name = "value") String email, RedirectAttributes redirectAttributes) throws MessagingException {
        emailService.sendTempPassword(email);

        FindPasswordDto dto = new FindPasswordDto(FindAccountType.EMAIL, email);
        redirectAttributes.addFlashAttribute("dto", dto);
        return "redirect:/members/find-password/complete";
    }
}
