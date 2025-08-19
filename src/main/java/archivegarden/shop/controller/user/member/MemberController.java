package archivegarden.shop.controller.user.member;

import archivegarden.shop.dto.common.JoinSuccessDto;
import archivegarden.shop.dto.user.member.JoinMemberForm;
import archivegarden.shop.exception.global.DuplicateEntityException;
import archivegarden.shop.service.user.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.regex.Pattern;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/join")
    public String addMemberForm(@ModelAttribute("joinForm") JoinMemberForm form) {
        return "user/member/join";
    }

    @PostMapping("/join")
    public String join(
            @Validated @ModelAttribute("joinForm") JoinMemberForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        validateJoin(form, bindingResult);
        if (bindingResult.hasErrors()) {
            return "user/member/join";
        }

        try {
            memberService.checkMemberDuplicate(form);
        } catch (DuplicateEntityException e) {
            bindingResult.reject("error.global.member.duplicate");
            return "user/member/join";
        }

        Long memberId = memberService.join(form);
        redirectAttributes.addFlashAttribute("joinMemberId", memberId);
        return "redirect:/join/complete";
    }

    @GetMapping("/join/complete")
    public String joinComplete(@ModelAttribute("joinMemberId") Long memberId, Model model) {
        if(memberId == null) return "redirect:/member/join";
        JoinSuccessDto joinCompletionInfoDto = memberService.joinComplete(memberId);
        model.addAttribute("memberInfo", joinCompletionInfoDto);
        return "user/member/join_complete";
    }

    /**
     * 회원가입 폼에 대한 복합 유효성 검증 수행
     *
     * 검증 항목:
     * - 비밀번호 확인
     * - 주소
     * - 휴대전화번호
     *
     * 각 항목별 전용 유효성 검증 메서드를 호출합니다.
     */
    private void validateJoin(JoinMemberForm form, BindingResult bindingResult) {
        validatePasswordConfirm(form, bindingResult);
        validateAddress(form, bindingResult);
        validatePhonenumber(form, bindingResult);
    }

    /**
     * 비밀번호 확인 유효성 검증
     *
     * 비밀번호와 비밀번호 확인 값이 일치하는지 검사합니다.
     */
    private void validatePasswordConfirm(JoinMemberForm form, BindingResult bindingResult) {
        if (StringUtils.hasText(form.getPassword())) {
            if (!form.getPassword().equals(form.getPasswordConfirm())) {
                bindingResult.rejectValue("passwordConfirm", "error.field.passwordConfirm.mismatch");
            }
        }
    }

    /**
     * 주소 유효성 검증
     *
     * 우편번호와 기본 주소의 입력 여부와 형식을 검사합니다.
     */
    private void validateAddress(JoinMemberForm form, BindingResult bindingResult) {
        if (StringUtils.hasText(form.getZipCode()) && StringUtils.hasText(form.getBasicAddress())) {
            if (!Pattern.matches("^[\\d]{5}$", form.getZipCode()) || !Pattern.matches("^[가-힣\\d\\W]{1,40}$", form.getBasicAddress())) {
                bindingResult.rejectValue("zipCode", "error.field.address.invalidFormat");
            }
        } else {
            bindingResult.rejectValue("zipCode", "error.field.address.required");
        }
    }

    /**
     * 휴대전화번호 유효성 검증
     *
     * 휴대전화번호를 구성하는 각 항목(앞자리, 중간, 뒷자리)의 입력 여부와 형식을 검사합니다.
     */
    private void validatePhonenumber(JoinMemberForm form, BindingResult bindingResult) {
        if (StringUtils.hasText(form.getPhonenumber1()) &&
                StringUtils.hasText(form.getPhonenumber2()) &&
                StringUtils.hasText(form.getPhonenumber3())) {
            if (!Pattern.matches("^01(0|1|[6-9])$", form.getPhonenumber1()) || !Pattern.matches("^[\\d]{3,4}$", form.getPhonenumber2()) || !Pattern.matches("^[\\d]{4}$", form.getPhonenumber3())) {
                bindingResult.rejectValue("phonenumber1", "error.field.phonenumber.invalidFormat");
            }
        } else {
            bindingResult.rejectValue("phonenumber1", "error.field.phonenumber.required");
        }
    }
}
