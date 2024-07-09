package archivegarden.shop.controller;

import archivegarden.shop.dto.ResultResponse;
import archivegarden.shop.dto.member.*;
import archivegarden.shop.service.member.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    //회원가입 폼
    @GetMapping("/join")
    public String addMemberForm(@ModelAttribute("form") AddMemberForm form) {
        return "members/join";
    }

    //회원가입
    @PostMapping("/join")
    public String join(@Valid @ModelAttribute("form") AddMemberForm form, BindingResult bindingResult, HttpServletRequest request) {

        //복합 룰 검증
        validateJoin(form, bindingResult);

        if (bindingResult.hasErrors()) {
            return "members/join";
        }

        //회원가입
        Long memberId = memberService.join(form);

        //회원가입한 회원 아이디 세션에 저장
        HttpSession session = request.getSession();
        session.setAttribute("joinMemberId", memberId);
        return "redirect:/members/join/complete";
    }

    //회원가입 완료
    @GetMapping("/join/complete")
    public String joinComplete(HttpServletRequest request, Model model) {

        HttpSession session = request.getSession(false);
        Long memberId = (Long) session.getAttribute("joinMemberId");
        if(memberId == null){
            return "redirect:/members/join";
        }
        session.invalidate();

        NewMemberInfo newMemberInfo = memberService.joinComplete(memberId);
        model.addAttribute("member", newMemberInfo);
        return "members/join_complete";
    }

    //아이디 찾기 폼
    @GetMapping("/find-id")
    public String findIdForm() {
        return "members/find_id";
    }

    //아이디 찾기 완료
    @GetMapping("/find-id/complete")
    public String findIdResult(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        Long memberId = (Long) session.getAttribute("findMemberId");
        if(memberId == null){
            return "redirect:/members/find-id";
        }
        session.invalidate();

        FindIdResultDto findIdResultDto = memberService.findIdComplete(memberId);
        model.addAttribute("member", findIdResultDto);
        return "members/find_id_complete";
    }

    //이메일을 통해 아이디 찾기
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @PostMapping("/find-id/email")
    public ResultResponse checkEmailExistsByEmail(@RequestParam("name") String name, @RequestParam("email") String email, HttpServletRequest request) {

        //유효성 검사
        String errorMessage = validateFindIdByEmail(name, email);
        if(StringUtils.hasText(errorMessage)) {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), errorMessage);
        }

        Long memberId = memberService.checkLoginIdExistsByEmail(name, email);
        if(memberId == null) {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), "가입 시 입력하신 회원 정보가 맞는지 다시 한번 확인해 주세요.");
        }

        //회원가입한 회원 아이디 세션에 저장
        HttpSession session = request.getSession();
        session.setAttribute("findMemberId", memberId);
        return new ResultResponse(HttpStatus.OK.value(), "아이디 찾기에 성공하였습니다.");
    }

    //휴대전화번호를 통해 아이디 찾기
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @PostMapping("/find-id/phonenumber")
    public ResultResponse findIdByPhonenumber(@RequestParam("name") String name, @RequestParam("phonenumber") String phonenumber, HttpServletRequest request) {

        //유효성 검사
        String errorMessage = validateFindIdByPhonenumber(name, phonenumber);
        if(StringUtils.hasText(errorMessage)) {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), errorMessage);
        }

        Long memberId = memberService.checkIdExistsByPhonenumber(name, phonenumber);
        if(memberId == null) {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), "가입 시 입력하신 회원 정보가 맞는지 다시 한번 확인해 주세요.");
        }

        //회원가입한 회원 아이디 세션에 저장
        HttpSession session = request.getSession();
        session.setAttribute("findMemberId", memberId);
        return new ResultResponse(HttpStatus.OK.value(), "아이디 찾기에 성공하였습니다.");
    }

    @GetMapping("/find-password")
    public String findPasswordForm(@ModelAttribute("findPasswordForm") FindPasswordForm form) {
        return "members/find_pw";
    }

    @PostMapping("/find-password")
    public String verifyFindPassword(@Validated @ModelAttribute("findPasswordForm") FindPasswordForm form, BindingResult bindingResult, Model model) {

        if(bindingResult.hasErrors()) {
            return "members/find_pw";
        }

        String findTypeValue = memberService.findPassword(form);
        if(findTypeValue == null) {
            bindingResult.reject("memberNotFound");
            return "members/find_pw";
        } else {
//            FindPasswordDto findPasswordDto = new FindPasswordDto(form.getFindType(), findTypeValue);
//            model.addAttribute("dto", findPasswordDto);
            return "members/find_pw_send";
        }
    }

    @GetMapping("/find-password/complete")
    public String findPasswordResult(@ModelAttribute("dto") FindPasswordDto dto, Model model) {
        model.addAttribute("dto", dto);
        return "members/find_pw_complete";
    }

    //로그인 아이디 중복검사
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @PostMapping("/check/loginId")
    public ResultResponse checkLoginId(@RequestParam(name = "loginId") String loginId) {
        boolean isAvailable = memberService.isAvailableLoginId(loginId);
        if(isAvailable) {
            return new ResultResponse(HttpStatus.OK.value(), "사용 가능한 아이디입니다.");
        } else {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), "이미 사용 중인 아이디입니다.");
        }
    }

    //이메일 중복검사
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @PostMapping("/check/email")
    public ResultResponse checkEmail(@RequestParam(name = "email") String email) {
        boolean isAvailable = memberService.isAvailableEmail(email);
        if(isAvailable) {
            return new ResultResponse(HttpStatus.OK.value(), "사용 가능한 이메일입니다.");
        } else {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), "이미 사용 중인 이메일입니다.");
        }
    }

    //휴대전화번호 인증번호 발급
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @PostMapping("/send/verificationNo")
    public ResultResponse sendSms(@Valid @ModelAttribute PhonenumberRequestDto requestDto, BindingResult bindingResult) {

        //정규식 검증
        if (bindingResult.hasErrors()) {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), "유효하지 않은 휴대전화번호입니다. 입력한 번호를 확인해 주세요.");
        }

        //중복 검증
        String phonenumber = requestDto.getPhonenumber();
        boolean isAvailable = memberService.isAvailablePhonenumber(phonenumber);
        if (!isAvailable) {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), "입력하신 휴대전화번호는 이미 다른 계정에 등록되어 있습니다.");
        }

        //인증번호 전송
        try {
            memberService.sendVerificationNo(phonenumber);
            return new ResultResponse(HttpStatus.OK.value(), "인증번호가 발송되었습니다.\n인증번호를 받지 못하셨다면 휴대전화번호를 확인해 주세요.");
        } catch (Exception e) {
            return new ResultResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "인증번호 발송 중 오류가 발생했습니다. 다시 시도해 주세요.");
        }
    }

    //인증번호 확인
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @PostMapping("/check/verificationNo")
    public ResultResponse checkVerificationNo(@ModelAttribute VerificationRequestDto requestDto) {
        boolean isValidated = memberService.validateVerificationNo(requestDto);
        if(isValidated) {
            return new ResultResponse(HttpStatus.OK.value(), "인증번호 확인에 성공하였습니다.");
        } else {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), "인증번호가 일치하지 않습니다.\n확인 후 다시 시도해 주세요.");
        }
    }

    //회원가입 폼 복합 룰 검증
    private void validateJoin(AddMemberForm form, BindingResult bindingResult) {
        //주소 검증
        if (StringUtils.hasText(form.getZipCode()) && StringUtils.hasText(form.getBasicAddress())) {
            if(!Pattern.matches("^[\\d]{5}$", form.getZipCode()) || !Pattern.matches("^[가-힣\\d\\W]{1,40}$", form.getBasicAddress())) {
                bindingResult.rejectValue("zipCode", "addressInvalid");
            }
        } else {
            bindingResult.rejectValue("zipCode", "requiredAddress", "주소를 입력해 주세요.");
        }

        //휴대전화번호 검증
        if (StringUtils.hasText(form.getPhonenumber1()) &&  StringUtils.hasText(form.getPhonenumber2()) && StringUtils.hasText(form.getPhonenumber3())) {
            if(!Pattern.matches("^01(0|1|[6-9])$", form.getPhonenumber1()) || !Pattern.matches("^[\\d]{3,4}$", form.getPhonenumber2()) || !Pattern.matches("^[\\d]{4}$", form.getPhonenumber3())) {
                bindingResult.rejectValue("phonenumber1", "phonenumberInvalid");
            }
        } else {
            bindingResult.rejectValue("phonenumber1", "requiredPhonenumber", "휴대전화번호를 입력해 주세요.");
        }

        //비밀번호 == 비밀번호 확인 검증
        if (StringUtils.hasText(form.getPassword())) {
            if (!form.getPassword().equals(form.getPasswordConfirm())) {
                bindingResult.rejectValue("passwordConfirm", "passwordNotMatch");
            }
        }
    }

    //이메일로 아이디 찾는 경우 유효성 검사
    private String validateFindIdByEmail(String name, String email) {

        String errorMessage = "";

        //이름
        if (!StringUtils.hasText(name)) {
            errorMessage = "이름을 입력해 주세요.";
        }

        //이메일
        if (!StringUtils.hasText(email)) {
            errorMessage = "이메일을 입력해 주세요.";
        } else if (!Pattern.matches("^[A-Za-z0-9_\\.\\-]+@[A-Za-z0-9\\-]+\\.[A-Za-z0-9\\-]+$", email)) {
            errorMessage = "유효한 이메일을 입력해 주세요.";
        }

        return errorMessage;
    }

    //휴대전화번호로 아이디 찾는 경우 유효성 검사
    private String validateFindIdByPhonenumber(String name, String phonenumber) {

        String errorMessage = "";

        //이름
        if (!StringUtils.hasText(name)) {
            errorMessage = "이름을 입력해 주세요.";
        }

        //휴대전화번호
        if (!Pattern.matches("^01(0|1|[6-9])-(\\d){3,4}-(\\d){4}$", phonenumber)) {
           errorMessage = "유효한 휴대전화번호를 입력해 주세요.";
        }

        return errorMessage;
    }
}
