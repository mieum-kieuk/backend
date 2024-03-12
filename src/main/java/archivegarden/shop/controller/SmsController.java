package archivegarden.shop.controller;

import archivegarden.shop.dto.member.FindPasswordDto;
import archivegarden.shop.entity.FindAccountType;
import archivegarden.shop.util.SmsUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/sms")
public class SmsController {

    private final SmsUtil smsUtil;

    @GetMapping("/temp-password")
    public String tempPassword(@RequestParam(name = "value") String phonenumber, RedirectAttributes redirectAttributes) {
//        smsUtil.sendTempPassword(phonenumber);

        FindPasswordDto findPasswordDto = new FindPasswordDto(FindAccountType.SMS, phonenumber);
        redirectAttributes.addFlashAttribute("dto", findPasswordDto);
        return "redirect:/members/find-password/complete";
    }
}
