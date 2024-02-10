package archivegarden.shop.controller;

import archivegarden.shop.service.MemberService;
import archivegarden.shop.web.form.MemberSaveDto;
import archivegarden.shop.web.form.MemberSaveForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/join")
    public String addMemberForm(@ModelAttribute("form") MemberSaveForm form) {
        return "members/join";
    }

    @PostMapping("/join")
    public String join(@Valid @ModelAttribute MemberSaveForm form, BindingResult bindingResult) {

        if(bindingResult.hasErrors()) {
            return "members/join";
        }

        memberService.join(new MemberSaveDto(form));
        return "members/join/complete";
    }

    @GetMapping("/join/complete")
    public String joinComplete() {
        return "members/join_complete";
    }
}
