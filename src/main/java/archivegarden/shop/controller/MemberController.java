package archivegarden.shop.controller;

import archivegarden.shop.service.MemberService;
import archivegarden.shop.web.form.MemberSaveDto;
import archivegarden.shop.web.form.MemberSaveForm;
import archivegarden.shop.web.validation.ValidationSequence;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    public String join(@Validated(ValidationSequence.class) @ModelAttribute("form") MemberSaveForm form, BindingResult bindingResult) {

        //복합 룰 검증
        if(form.getPassword() != null && form.getPasswordConfirm() != null) {
            if (!form.getPassword().equals(form.getPasswordConfirm())) {
                bindingResult.reject("passwordNotEqual", "동일한 비밀번호를 입력해주세요.");
            }
        }

        if(bindingResult.hasErrors()) {
            return "members/join";
        }

        memberService.join(new MemberSaveDto(form));
        return "redirect:/members/join/complete";
    }

    @GetMapping("/join/complete")
    public String joinComplete() {
        return "members/join_complete";
    }

    @ResponseBody
    @PostMapping("/verification/loginId")
    public boolean verifyLoginId(@RequestParam String loginId) {
        return memberService.duplicateLoginId(loginId);
    }

    @ResponseBody
    @PostMapping("/verification/email")
    public boolean verifyEmail(@RequestParam String email) {
        return memberService.duplicateEmail(email);
    }
}
