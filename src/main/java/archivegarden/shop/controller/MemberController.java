package archivegarden.shop.controller;

import archivegarden.shop.dto.member.*;
import archivegarden.shop.entity.FindAccountType;
import archivegarden.shop.service.member.MemberService;
import archivegarden.shop.web.validation.FindIdValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;
    private final FindIdValidator findIdValidator;

    @InitBinder
    public void init(WebDataBinder dataBinder) {
        dataBinder.addValidators(findIdValidator);
    }

    @ModelAttribute("findAccountTypes")
    public FindAccountType[] findAccountTypes() {
        return FindAccountType.values();
    }

    @GetMapping("/join")
    public String addMemberForm(@ModelAttribute("form") MemberSaveForm form) {
        return "members/join";
    }

    @PostMapping("/join")
    public String join(@Validated @ModelAttribute("form") MemberSaveForm form, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        //비밀번호 == 비밀번호 확인 검증
        if (StringUtils.hasText(form.getPassword())) {
            if (!form.getPassword().equals(form.getPasswordConfirm())) {
                bindingResult.rejectValue("passwordConfirm", "passwordNotEqual", "비밀번호가 일치하지 않습니다.");
            }
        }

        //핸드폰 번호 검증
        if (bindingResult.hasFieldErrors("phonenumber1") || bindingResult.hasFieldErrors("phonenumber2") || bindingResult.hasFieldErrors("phonenumber3")) {
            bindingResult.rejectValue("phonenumber1", "Invaild", "유효하지 않은 휴대폰 번호입니다. 입력한 번호를 확인해 주세요.");
        }

        if (bindingResult.hasErrors()) {
            return "members/join";
        }

        Long memberId = memberService.join(new MemberSaveDto(form));

        redirectAttributes.addFlashAttribute("memberId", memberId);
        return "redirect:/members/join/complete";
    }

    @GetMapping("/join/complete")
    public String joinComplete(@ModelAttribute(name = "memberId") Long memberId, Model model) {
        NewMemberInfo newMemberInfo = memberService.getNewMemberInfo(memberId);
        model.addAttribute("member", newMemberInfo);
        return "members/join_complete";
    }

    @ResponseBody
    @PostMapping("/verification/loginId")
    public boolean checkLoginId(@RequestParam(name = "loginId") String loginId) {
        return memberService.duplicateLoginId(loginId);
    }

    @ResponseBody
    @PostMapping("/verification/email")
    public boolean checkEmail(@RequestParam(name = "email") String email) {
        return memberService.duplicateEmail(email);
    }

    @ResponseBody
    @PostMapping("/send/verificationNo")
    public PhonenumberResponseDto sendSms(@Valid @ModelAttribute PhonenumberRequestDto requestDto, BindingResult bindingResult) {

        //정규식 검증
        if (bindingResult.hasErrors()) {
            return new PhonenumberResponseDto(false, "유효하지 않은 휴대폰 번호입니다. 입력한 번호를 확인해 주세요.");
        }

        //중복 검증
        String phonenumber = requestDto.getPhonenumber1() + requestDto.getPhonenumber2() + requestDto.getPhonenumber3();
        boolean isAvailable = memberService.duplicatePhonenumber(phonenumber);
        if (!isAvailable) {
            return new PhonenumberResponseDto(false, "사용하려는 휴대폰 번호는 이미 다른 계정에 등록되어 있습니다.");
        }

        //인증번호 전송
        try {
            memberService.sendVerificationNo(phonenumber);
            return new PhonenumberResponseDto(true, "인증번호가 발송되었습니다. 인증번호를 받지 못하셨다면 휴대폰 번호를 확인해 주세요.");
        } catch (Exception e) {
            return new PhonenumberResponseDto(false, "인증번호 발송 중 오류가 발생했습니다. 다시 시도해 주세요.");
        }
    }

    @ResponseBody
    @PostMapping("/verification/verificationNo")
    public boolean checkVerificationNo(@ModelAttribute VerificationRequestDto requestDto) {
        return memberService.validateVerificationNo(requestDto);
    }

    @GetMapping("/find-id")
    public String findIdForm(@ModelAttribute("form") FindIdForm form) {
        return "members/find_id";
    }

    @PostMapping("/find-id")
    public String findId(@Validated @ModelAttribute("form") FindIdForm form, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return "members/find_id";
        }

        Optional<FindIdResultDto> result = memberService.findId(form);
        if(result.isPresent()) {
            model.addAttribute("member", result);
            return "redirect:/members/find-id/result";
        } else {
            bindingResult.reject("memberNotFound");
            return "members/find_id";
        }
    }
}
