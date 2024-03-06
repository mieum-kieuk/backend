package archivegarden.shop.controller;

import archivegarden.shop.service.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @GetMapping("/verification/email/link")
    public String verifyEmailLink(@RequestParam(name = "address") String address, @RequestParam(name = "uuid") String uuid) {
        return emailService.verifyEmailLink(address, uuid);
    }
}
