package archivegarden.shop.controller.user.email;

import archivegarden.shop.service.user.email.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;

    @GetMapping("/verify")
    public String verifyEmailLink(
            @RequestParam(name = "address") String address,
            @RequestParam(name = "uuid") String uuid
    ) {
        return emailService.verifyEmail(address, uuid);
    }
}
