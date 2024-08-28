package archivegarden.shop.controller.user.member;

import archivegarden.shop.dto.ResultResponse;
import archivegarden.shop.dto.user.member.PhonenumberRequestDto;
import archivegarden.shop.dto.user.member.VerificationRequestDto;
import archivegarden.shop.service.member.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Pattern;

@RestController
@RequestMapping("/ajax/members")
@RequiredArgsConstructor
public class MemberAjaxController {

    private final MemberService memberService;

    /**
     *  로그인 아이디 중복 여부를 검사하는 메서드
     */
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/check/loginId")
    public ResultResponse checkLoginId(@RequestParam(name = "loginId") String loginId) {
        boolean isAvailable = memberService.isAvailableLoginId(loginId);
        if(isAvailable) {
            return new ResultResponse(HttpStatus.OK.value(), "사용 가능한 아이디입니다.");
        } else {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), "이미 사용 중인 아이디입니다.");
        }
    }

    /**
     *  이메일 중복 여부를 검사하는 메서드
     */
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/check/email")
    public ResultResponse checkEmail(@RequestParam(name = "email") String email) {
        boolean isAvailable = memberService.isAvailableEmail(email);
        if(isAvailable) {
            return new ResultResponse(HttpStatus.OK.value(), "사용 가능한 이메일입니다.");
        } else {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), "이미 사용 중인 이메일입니다.");
        }
    }

    /**
     * 휴대전화번호 인증번호를 발급하는 메서드
     */
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/send/verificationNo")
    public ResultResponse sendSms(@Valid @ModelAttribute PhonenumberRequestDto requestDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), "유효하지 않은 휴대전화번호입니다. 입력한 번호를 확인해 주세요.");
        }

        String phonenumber = requestDto.getPhonenumber();
        boolean isAvailable = memberService.isAvailablePhonenumber(phonenumber);
        if (!isAvailable) {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), "입력하신 휴대전화번호는 이미 다른 계정에 등록되어 있습니다.");
        }

        try {
            memberService.sendVerificationNo(phonenumber);
            return new ResultResponse(HttpStatus.OK.value(), "인증번호가 발송되었습니다.\n인증번호를 받지 못하셨다면 휴대전화번호를 확인해 주세요.");
        } catch (Exception e) {
            return new ResultResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "인증번호 발송 중 오류가 발생했습니다.\n다시 시도해 주세요.");
        }
    }

    /**
     * 사용자가 입력한 인증번호를 검증하는 메서드
     */
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/check/verificationNo")
    public ResultResponse checkVerificationNo(@ModelAttribute VerificationRequestDto requestDto) {
        boolean isValidated = memberService.validateVerificationNo(requestDto);
        if(isValidated) {
            return new ResultResponse(HttpStatus.OK.value(), "인증번호 확인에 성공하였습니다.");
        } else {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), "인증번호가 일치하지 않습니다.\n확인 후 다시 시도해 주세요.");
        }
    }

    /**
     * 이메일을 통해 로그인 아이디를 찾는 메서드
     */
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/find-id/email")
    public ResultResponse findIdByEmail(@RequestParam("name") String name, @RequestParam("email") String email, HttpSession session) {
        String errorMessage = validateFindIdByEmail(name, email);
        if(StringUtils.hasText(errorMessage)) {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), errorMessage);
        }

        Long memberId = memberService.checkLoginIdExistsByEmail(name, email);
        if(memberId == null) {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), "가입 시 입력하신 회원 정보가 맞는지\n다시 한번 확인해 주세요.");
        }

        session.setAttribute("findId:memberId", memberId);
        return new ResultResponse(HttpStatus.OK.value(), "아이디 찾기에 성공하였습니다.");
    }

    /**
     * 휴대전화번호를 통해 로그인 아이디를 찾는 메서드
     */
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/find-id/phonenumber")
    public ResultResponse findIdByPhonenumber(@RequestParam("name") String name, @RequestParam("phonenumber") String phonenumber, HttpServletRequest request) {
        String errorMessage = validateFindIdByPhonenumber(name, phonenumber);
        if(StringUtils.hasText(errorMessage)) {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), errorMessage);
        }

        Long memberId = memberService.checkIdExistsByPhonenumber(name, phonenumber);
        if(memberId == null) {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), "가입 시 입력하신 회원 정보가 맞는지\n다시 한번 확인해 주세요.");
        }

        HttpSession session = request.getSession();
        session.setAttribute("findId:memberId", memberId);
        return new ResultResponse(HttpStatus.OK.value(), "아이디 찾기에 성공하였습니다.");
    }

    /**
     * 이메일을 통해 비밀번호를 찾는 메서드
     */
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/find-password/email")
    public ResultResponse findPasswordByEmail(@RequestParam("loginId") String loginId, @RequestParam("name") String name,
                                              @RequestParam("email") String email, HttpSession session) {
        String errorMessage = validateFindPasswordByEmail(loginId, name, email);
        if(StringUtils.hasText(errorMessage)) {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), errorMessage);
        }

        String foundEmail = memberService.checkPasswordExistsByEmail(loginId, name, email);
        if(foundEmail == null) {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), "가입 시 입력하신 회원 정보가 맞는지\n다시 한번 확인해 주세요.");
        }

        session.setAttribute("findPassword:email", foundEmail);
        return new ResultResponse(HttpStatus.OK.value(), "비밀번호 찾기에 성공하였습니다.");
    }

    /**
     * 휴대전화번호를 통해 비밀번호를 찾는 메서드
     */
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/find-password/phonenumber")
    public ResultResponse findPasswordByPhonenumber(@RequestParam("loginId") String loginId, @RequestParam("name") String name,
                                                    @RequestParam("phonenumber") String phonenumber, HttpSession session) {
        String errorMessage = validateFindPasswordByPhonenumber(loginId, name, phonenumber);
        if(StringUtils.hasText(errorMessage)) {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), errorMessage);
        }

        String foundEmail = memberService.checkPasswordExistsByPhonenumber(loginId, name, phonenumber);
        if(foundEmail == null) {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), "가입 시 입력하신 회원 정보가 맞는지\n다시 한번 확인해 주세요.");
        }

        session.setAttribute("findPassword:email", foundEmail);
        return new ResultResponse(HttpStatus.OK.value(), "비밀번호 찾기에 성공하였습니다.");
    }

    /**
     * 이메일을 통해 아이디를 찾는 경우 유효성 검사를 수행하는 메서드
     */
    private String validateFindIdByEmail(String name, String email) {
        String errorMessage = "";
        if (!StringUtils.hasText(name)) {
            errorMessage = "이름을 입력해 주세요.";
        }

        if (!StringUtils.hasText(email)) {
            errorMessage = "이메일을 입력해 주세요.";
        } else if (!Pattern.matches("^[A-Za-z0-9_\\.\\-]+@[A-Za-z0-9\\-]+\\.[A-Za-z0-9\\-]+$", email)) {
            errorMessage = "유효한 이메일을 입력해 주세요.";
        }

        return errorMessage;
    }

    /**
     * 휴대전화번호로 통해 아이디를 찾는 경우 유효성 검사를 수행하는 메서드
     */
    private String validateFindIdByPhonenumber(String name, String phonenumber) {
        String errorMessage = "";
        if (!StringUtils.hasText(name)) {
            errorMessage = "이름을 입력해 주세요.";
        }

        if (!Pattern.matches("^01(0|1|[6-9])-(\\d){3,4}-(\\d){4}$", phonenumber)) {
            errorMessage = "유효한 휴대전화번호를 입력해 주세요.";
        }

        return errorMessage;
    }

    /**
     * 이메일을 통해 비밀번호를 찾는 경우 유효성 검사를 수행하는 메서드
     */
    private String validateFindPasswordByEmail(String loginId, String name, String email) {
        String errorMessage = "";
        if (!StringUtils.hasText(loginId)) {
            errorMessage = "아이디를 입력해 주세요.";
        } else if (!Pattern.matches("(?=.*[a-z])(?=.*\\d)[a-z\\d]{5,20}+$", loginId)) {
            errorMessage = "유효한 아이디를 입력해 주세요.";
        }

        if (!StringUtils.hasText(name)) {
            errorMessage = "이름을 입력해 주세요.";
        }

        if (!StringUtils.hasText(email)) {
            errorMessage = "이메일을 입력해 주세요.";
        } else if (!Pattern.matches("^[A-Za-z0-9_\\.\\-]+@[A-Za-z0-9\\-]+\\.[A-Za-z0-9\\-]+$", email)) {
            errorMessage = "유효한 이메일을 입력해 주세요.";
        }

        return errorMessage;
    }

    /**
     * 휴대전화번호를 통해 비밀번호를 찾는 경우 유효성 검사를 수행하는 메서드
     */
    private String validateFindPasswordByPhonenumber(String loginId, String name, String phonenumber) {
        String errorMessage = "";
        if (!StringUtils.hasText(loginId)) {
            errorMessage = "아이디를 입력해 주세요.";
        } else if (!Pattern.matches("(?=.*[a-z])(?=.*\\d)[a-z\\d]{5,20}+$", loginId)) {
            errorMessage = "유효한 아이디를 입력해 주세요.";
        }

        if (!StringUtils.hasText(name)) {
            errorMessage = "이름을 입력해 주세요.";
        }

        if (!Pattern.matches("^01(0|1|[6-9])-(\\d){3,4}-(\\d){4}$", phonenumber)) {
            errorMessage = "유효한 휴대전화번호를 입력해 주세요.";
        }

        return errorMessage;
    }
}
