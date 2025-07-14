package archivegarden.shop.controller.user.member;

import archivegarden.shop.constant.SessionConstants;
import archivegarden.shop.dto.common.JoinSuccessDto;
import archivegarden.shop.dto.user.member.FindIdResultDto;
import archivegarden.shop.dto.user.member.JoinMemberForm;
import archivegarden.shop.exception.global.DuplicateEntityException;
import archivegarden.shop.service.user.member.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.regex.Pattern;

@Tag(name = "회원", description = "사용자 페이지에서 회원 관련 API")
@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @Operation(
            summary = "회원가입 폼 표시",
            description = "회원가입을 위한 화면을 반환합니다."
    )
    @GetMapping("/join")
    public String addMemberForm(@ModelAttribute("joinForm") JoinMemberForm form) {
        return "user/member/join";
    }

    @Operation(
            summary = "회원가입 요청",
            description = "회원가입 요청을 처리합니다. 회원가입 성공 시 완료 페이지로 리다이렉트합니다."
    )
    @PostMapping("/join")
    public String join(
            @Validated @ModelAttribute("joinForm") JoinMemberForm form,
            BindingResult bindingResult,
            HttpSession session
    ) {
        validateJoin(form, bindingResult);
        if (bindingResult.hasErrors()) {
            return "user/member/join";
        }

        try {
            memberService.checkMemberDuplicate(form);
        } catch(DuplicateEntityException e) {
            bindingResult.reject("error.global.member.duplicate");
            return "user/member/join";
        }

        Long memberId = memberService.join(form);
        session.setAttribute(SessionConstants.JOIN_MEMBER_ID_KEY, memberId);
        return "redirect:/member/join/complete";
    }

    @Operation(
            summary = "회원가입 완료 페이지",
            description = "회원가입이 완료된 후, 회원가입 완료 페이지를 반환합니다."
    )
    @GetMapping("/join/complete")
    public String joinComplete(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        if(session == null) return "redirect:/member/join";
        Long memberId = (Long) session.getAttribute(SessionConstants.JOIN_MEMBER_ID_KEY);
        if (memberId == null) return "redirect:/member/join";
        session.removeAttribute(SessionConstants.JOIN_MEMBER_ID_KEY);

        JoinSuccessDto joinCompletionInfoDto = memberService.joinComplete(memberId);
        model.addAttribute("memberInfo", joinCompletionInfoDto);
        return "user/member/join_complete";
    }

    @Operation(
            summary = "아이디 찾기 폼 표시",
            description = "아이디 찾기 화면을 반환합니다."
    )
    @GetMapping("/find-id")
    public String findId() {
        return "user/member/find_id";
    }

    @Operation(
            summary = "아이디 찾기 결과 페이지",
            description = "아이디 찾기 결과 페이지를 반환합니다."
    )
    @GetMapping("/find-id/complete")
    public String findIdResult(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        if(session == null) return "redirect:/member/find-id";
        Long memberId = (Long) session.getAttribute(SessionConstants.FIND_LOGIN_ID_MEMBER_ID_KEY);
        if (memberId == null) return "redirect:/member/find-id";
        session.removeAttribute(SessionConstants.FIND_LOGIN_ID_MEMBER_ID_KEY);

        FindIdResultDto findIdResultDto = memberService.findIdComplete(memberId);
        model.addAttribute("member", findIdResultDto);
        return "user/member/find_id_complete";
    }

    @Operation(
            summary = "비밀번호 찾기 폼 표시",
            description = "비밀번호 찾기 화면을 반환합니다."
    )
    @GetMapping("/find-password")
    public String findPassword() {
        return "user/member/find_password";
    }

    @Operation(
            summary = "임시 비밀번호 전송 화면 표시",
            description = "임시 비밀번호가 전송될 이메일 주소를 확인합니다."
    )
    @GetMapping("/find-password/send")
    public String displaySendTempPasswordEmailPage(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        if (session == null) return "redirect:/member/find-password";
        String email = (String) session.getAttribute(SessionConstants.FIND_PASSWORD_EMAIL_KEY);
        if (email == null) return "redirect:/member/find-password";

        model.addAttribute("email", email);
        return "user/member/send_temporary_password";
    }

    @Operation(
            summary = "비밀번호 찾기 결과 페이지",
            description = "비밀번호 찾기 결과를 보여주는 페이지를 반환합니다."
    )
    @GetMapping("/find-password/complete")
    public String findPasswordResult(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        if(session == null)  return "redirect:/member/find-password";
        String email = (String) session.getAttribute(SessionConstants.FIND_PASSWORD_EMAIL_KEY);
        if (email == null) return "redirect:/member/find-password";
        session.removeAttribute(SessionConstants.FIND_PASSWORD_EMAIL_KEY);

        model.addAttribute("email", email);
        return "user/member/find_password_complete";
    }

    /**
     * 회원가입 폼의 복합적인 유효성 검증
     *
     * 각 개별 필드에 대해 유효성 검증 메서드를 호출합니다.
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
     * 우편번호와 기본주소가 모두 입력되었는지, 형식이 올바른지 검사합니다.
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
     * 휴대전화번호 각 필드가 입력되었는지, 형식이 올바른지 검사합니다.
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
