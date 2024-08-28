package archivegarden.shop.controller.user.member;

import archivegarden.shop.dto.user.member.JoinMemberForm;
import archivegarden.shop.dto.user.member.FindIdResultDto;
import archivegarden.shop.dto.common.JoinCompletionInfoDto;
import archivegarden.shop.service.user.member.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Pattern;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원가입 폼을 반환하는 메서드
     */
    @GetMapping("/join")
    public String addMemberForm(@ModelAttribute("joinForm") JoinMemberForm form) {
        return "user/member/join";
    }

    /**
     * 회원가입 요청을 처리하는 메서드
     */
    @PostMapping("/join")
    public String join(@Validated @ModelAttribute("joinForm") JoinMemberForm form, BindingResult bindingResult, HttpSession session) {
        validateJoin(form, bindingResult);
        if (bindingResult.hasErrors()) {
            return "user/member/join";
        }

        Long memberId = memberService.join(form);
        session.setAttribute("join:memberId", memberId);
        return "redirect:/members/join/complete";
    }

    /**
     * 회원가입 완료 후 결과 페이지를 반환하는 메서드
     */
    @GetMapping("/join/complete")
    public String joinComplete(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        Long memberId = (Long) session.getAttribute("join:memberId");
        if (session == null || memberId == null) {
            return "redirect:/members/join";
        }
        session.removeAttribute("join:memberId");

        JoinCompletionInfoDto joinCompletionInfoDto = memberService.joinComplete(memberId);
        model.addAttribute("memberInfo", joinCompletionInfoDto);
        return "user/member/join_complete";
    }

    /**
     * 아이디 찾기 폼을 반환하는 메서드
     */
    @GetMapping("/find-id")
    public String findId() {
        return "user/member/find_id";
    }

    /**
     * 아이디 찾기 완료 후 결과 페이지를 처리하는 메서드
     */
    @GetMapping("/find-id/complete")
    public String findIdResult(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        Long memberId = (Long) session.getAttribute("findId:memberId");
        if (session == null || memberId == null) {
            return "redirect:/members/find-id";
        }
        session.removeAttribute("findId:memberId");

        FindIdResultDto findIdResultDto = memberService.findIdComplete(memberId);
        model.addAttribute("member", findIdResultDto);
        return "user/member/find_id_complete";
    }

    /**
     * 비밀번호 찾기 폼을 반환하는 메서드
     */
    @GetMapping("/find-password")
    public String findPassword() {
        return "user/member/find_password";
    }

    /**
     * 임시 비밀번호가 전송될 이메일 주소를 확인하는 페이지를 반환하는 메서드
     */
    @GetMapping("find-password/send")
    public String verifyEmail(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        String email = (String) session.getAttribute("findPassword:email");
        if (session == null || email == null) {
            return "redirect:/members/find-password";
        }

        model.addAttribute("email", email);
        return "user/member/send_temporary_password";
    }

    /**
     * 비밀번호 찾기 완료 후 결과 페이지를 반환하는 메서드
     */
    @GetMapping("/find-password/complete")
    public String findPasswordResult(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        String email = (String) session.getAttribute("findPassword:email");
        if (session == null || email == null) {
            return "redirect:/members/find-password";
        }
        session.removeAttribute("findPassword:email");

        model.addAttribute("email", email);
        return "user/member/find_password_complete";
    }

    /**
     * 회원가입 폼의 복합적인 유효성 검증을 수행하는 메서드
     * - 비밀번호 확인: 비밀번호와 비밀번호 확인이 일치하는지 검사합니다.
     * - 주소: 우편번호와 기본 주소의 형식을 검사합니다.
     * - 휴대전화번호: 휴대전화번호의 형식이 올바른지 검사합니다.
     */
    private void validateJoin(JoinMemberForm form, BindingResult bindingResult) {
        if (StringUtils.hasText(form.getPassword())) {
            if (!form.getPassword().equals(form.getPasswordConfirm())) {
                bindingResult.rejectValue("passwordConfirm", "mismatch");
            }
        }

        if (StringUtils.hasText(form.getZipCode()) && StringUtils.hasText(form.getBasicAddress())) {
            if (!Pattern.matches("^[\\d]{5}$", form.getZipCode()) || !Pattern.matches("^[가-힣\\d\\W]{1,40}$", form.getBasicAddress())) {
                bindingResult.rejectValue("zipCode", "invalid");
            }
        } else {
            bindingResult.rejectValue("zipCode", "required");
        }

        if (StringUtils.hasText(form.getPhonenumber1()) && StringUtils.hasText(form.getPhonenumber2()) && StringUtils.hasText(form.getPhonenumber3())) {
            if (!Pattern.matches("^01(0|1|[6-9])$", form.getPhonenumber1()) || !Pattern.matches("^[\\d]{3,4}$", form.getPhonenumber2()) || !Pattern.matches("^[\\d]{4}$", form.getPhonenumber3())) {
                bindingResult.rejectValue("phonenumber1", "invalid");
            }
        } else {
            bindingResult.rejectValue("phonenumber1", "required");
        }
    }
}
