package archivegarden.shop.controller;

import archivegarden.shop.service.MemberService;
import archivegarden.shop.web.form.MemberSaveDto;
import archivegarden.shop.web.form.MemberSaveForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
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
    public String join(@Validated @ModelAttribute("form") MemberSaveForm form, BindingResult bindingResult) {

        //비밀번호 == 비밀번호 확인 검증
        if(StringUtils.hasText(form.getPassword())) {
            if (!form.getPassword().equals(form.getPasswordConfirm())) {
                bindingResult.rejectValue("passwordConfirm", "passwordNotEqual", "비밀번호가 일치하지 않습니다.");
            }
        }

        //핸드폰 번호 검증
        if(bindingResult.hasFieldErrors("phonenumber1") || bindingResult.hasFieldErrors("phonenumber2") || bindingResult.hasFieldErrors("phonenumber3")) {
            bindingResult.rejectValue("phonenumber1", "Invaild", "유효하지 않은 휴대폰 번호입니다. 입력한 번호를 확인해 주세요.");
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
