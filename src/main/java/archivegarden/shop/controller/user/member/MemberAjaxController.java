package archivegarden.shop.controller.user.member;

import archivegarden.shop.constant.SessionConstants;
import archivegarden.shop.dto.ResultResponse;
import archivegarden.shop.dto.user.member.PhonenumberRequestDto;
import archivegarden.shop.dto.user.member.VerificationRequestDto;
import archivegarden.shop.service.user.member.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.regex.Pattern;

@Tag(name = "회원", description = "사용자 페이지에서 회원 관련 AJAX API")
@RestController
@RequestMapping("/ajax/member")
@RequiredArgsConstructor
public class MemberAjaxController {

    private final MemberService memberService;

    @Operation(
            summary = "로그인 아이디 중복 검사",
            description = "로그인 아이디의 사용 가능 여부를 확인합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "사용 가능한 아이디"),
                    @ApiResponse(responseCode = "409", description = "이미 사용 중인 아이디")
            }
    )
    @PostMapping("/check/loginId")
    public ResultResponse checkLoginIdDuplicate(@RequestParam(name = "loginId") String loginId) {
        boolean isAvailable = memberService.isAvailableLoginId(loginId);
        if (isAvailable) {
            return new ResultResponse(HttpStatus.OK.value(), "사용 가능한 아이디입니다.");
        } else {
            return new ResultResponse(HttpStatus.CONFLICT.value(), "이미 사용 중인 아이디입니다.");
        }
    }

    @Operation(
            summary = "이메일 중복 검사",
            description = "이메일 사용 가능 여부를 확인합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "사용 가능한 이메일"),
                    @ApiResponse(responseCode = "409", description = "이미 사용 중인 이메일")
            }
    )
    @PostMapping("/check/email")
    public ResultResponse checkEmailDuplicate(@RequestParam(name = "email") String email) {
        boolean isAvailable = memberService.isAvailableEmail(email);
        if (isAvailable) {
            return new ResultResponse(HttpStatus.OK.value(), "사용 가능한 이메일입니다.");
        } else {
            return new ResultResponse(HttpStatus.CONFLICT.value(), "이미 사용 중인 이메일입니다.");
        }
    }

    @Operation(
            summary = "휴대전화번호 인증번호 발급",
            description = "입력받은 휴대전화번호로 인증번호 전송합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "인증번호가 발송"),
                    @ApiResponse(responseCode = "400", description = "휴대전화번호 유효성 검증 실패"),
                    @ApiResponse(responseCode = "409", description = "이미 사용 중인 휴대전화번호"),
                    @ApiResponse(responseCode = "500", description = "인증번호 발송 중 오류 발생")
            }
    )
    @PostMapping("/send/verificationNo")
    public ResultResponse sendSms(@Valid @ModelAttribute PhonenumberRequestDto requestDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), "유효하지 않은 휴대전화번호입니다. 입력한 번호를 확인해 주세요.");
        }

        String phonenumber = requestDto.getFormattedPhonenumber();
        boolean isAvailable = memberService.isAvailablePhonenumber(phonenumber);
        if (!isAvailable) {
            return new ResultResponse(HttpStatus.CONFLICT.value(), "입력하신 휴대전화번호는 이미 다른 계정에서 사용 중 입니다.");
        }

        try {
            memberService.sendVerificationNo(phonenumber);
            return new ResultResponse(HttpStatus.OK.value(), "인증번호가 발송되었습니다.\n인증번호를 받지 못하셨다면 휴대전화번호를 확인해 주세요.");
        } catch (Exception e) {
            return new ResultResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "인증번호 발송 중 오류가 발생했습니다.\n다시 시도해 주세요.");
        }
    }

    @Operation(
            summary = "휴대전화번호 인증번호 검증",
            description = "사용자가 입력한 인증번호가 Redis에 저장된 값과 일치하는지 검증합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "인증번호 확인 성공"),
                    @ApiResponse(responseCode = "400", description = "인증번호 불일치")
            }
    )
    @PostMapping("/check/verificationNo")
    public ResultResponse checkVerificationNo(@ModelAttribute VerificationRequestDto requestDto) {
        boolean isValidated = memberService.validateVerificationNo(requestDto);
        if (isValidated) {
            return new ResultResponse(HttpStatus.OK.value(), "인증번호 확인에 성공하였습니다.");
        } else {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), "인증번호가 일치하지 않습니다.\n확인 후 다시 시도해 주세요.");
        }
    }

    @Operation(
            summary = "이메일로 아이디 찾기",
            description = "회원 이름과 이메일을 입력받아 가입된 계정이 있는지 확인합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "아이디 찾기 성공"),
                    @ApiResponse(responseCode = "400", description = "유효성 검증 실패"),
                    @ApiResponse(responseCode = "404", description = "아이디 찾기 실패")
            }
    )
    @PostMapping("/find-id/email")
    public ResultResponse findIdByEmail(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            HttpSession session
    ) {
        String errorMessage = validateFindIdByEmail(name, email);
        if (StringUtils.hasText(errorMessage)) {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), errorMessage);
        }

        Optional<Long> memberIdOpt = memberService.checkLoginIdExistsByEmail(name, email);
        if (memberIdOpt.isEmpty()) {
            return new ResultResponse(HttpStatus.NOT_FOUND.value(), "가입 시 입력하신 회원 정보가 맞는지\n다시 한번 확인해 주세요.");
        }

        Long memberId = memberIdOpt.get();
        session.setAttribute(SessionConstants.FIND_LOGIN_ID_MEMBER_ID_KEY, memberId);
        return new ResultResponse(HttpStatus.OK.value(), "아이디 찾기에 성공하였습니다.");
    }

    @Operation(
            summary = "휴대전화번호로 아이디 찾기",
            description = "회원 이름과 휴대전화번호를 입력받아 가입된 계정이 있는지 확인합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "아이디 찾기 성공"),
                    @ApiResponse(responseCode = "400", description = "유효성 검증 실패"),
                    @ApiResponse(responseCode = "404", description = "아이디 찾기 실패")
            }
    )
    @PostMapping("/find-id/phonenumber")
    public ResultResponse findIdByPhonenumber(
            @RequestParam("name") String name,
            @RequestParam("phonenumber") String phonenumber,
            HttpSession session) {
        String errorMessage = validateFindIdByPhonenumber(name, phonenumber);
        if (StringUtils.hasText(errorMessage)) {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), errorMessage);
        }

        Optional<Long> memberIdOpt = memberService.checkLoginIdExistsByPhonenumber(name, phonenumber);
        if (memberIdOpt.isEmpty()) {
            return new ResultResponse(HttpStatus.NOT_FOUND.value(), "가입 시 입력하신 회원 정보가 맞는지\n다시 한번 확인해 주세요.");
        }

        Long memberId = memberIdOpt.get();
        session.setAttribute(SessionConstants.FIND_LOGIN_ID_MEMBER_ID_KEY, memberId);
        return new ResultResponse(HttpStatus.OK.value(), "아이디 찾기에 성공하였습니다.");
    }

    @Operation(
            summary = "이메일로 비밀번호 찾기",
            description = "회원 로그인 아이디, 이름, 이메일을 입력받아 가입된 계정이 있는지 확인합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "비밀번호 찾기 성공"),
                    @ApiResponse(responseCode = "400", description = "유효성 검증 실패"),
                    @ApiResponse(responseCode = "404", description = "비밀번호 찾기 실패")
            }
    )
    @PostMapping("/find-password/email")
    public ResultResponse findPasswordByEmail(
            @RequestParam("loginId") String loginId,
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            HttpSession session
    ) {
        String errorMessage = validateFindPasswordByEmail(loginId, name, email);
        if (StringUtils.hasText(errorMessage)) {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), errorMessage);
        }

        Optional<String> emailOpt = memberService.checkPasswordExistsByEmail(loginId, name, email);
        if (emailOpt.isEmpty()) {
            return new ResultResponse(HttpStatus.NOT_FOUND.value(), "가입 시 입력하신 회원 정보가 맞는지\n다시 한번 확인해 주세요.");
        }

        session.setAttribute(SessionConstants.FIND_PASSWORD_EMAIL_KEY, emailOpt.get());
        return new ResultResponse(HttpStatus.OK.value(), "비밀번호 찾기에 성공하였습니다.");
    }

    @Operation(
            summary = "휴대전화번호로 비밀번호 찾기",
            description = "회원 로그인 아이디, 이름, 휴대전화번호를 입력받아 가입된 계정이 있는지 확인합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "비밀번호 찾기 성공"),
                    @ApiResponse(responseCode = "400", description = "유효성 검증 실패"),
                    @ApiResponse(responseCode = "404", description = "비밀번호 찾기 실패")
            }
    )
    @PostMapping("/find-password/phonenumber")
    public ResultResponse findPasswordByPhonenumber(
            @RequestParam("loginId") String loginId,
            @RequestParam("name") String name,
            @RequestParam("phonenumber") String phonenumber,
            HttpSession session
    ) {
        String errorMessage = validateFindPasswordByPhonenumber(loginId, name, phonenumber);
        if (StringUtils.hasText(errorMessage)) {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), errorMessage);
        }

        Optional<String> emailOpt = memberService.checkPasswordExistsByPhonenumber(loginId, name, phonenumber);
        if (emailOpt.isEmpty()) {
            return new ResultResponse(HttpStatus.NOT_FOUND.value(), "가입 시 입력하신 회원 정보가 맞는지\n다시 한번 확인해 주세요.");
        }

        session.setAttribute(SessionConstants.FIND_PASSWORD_EMAIL_KEY, emailOpt.get());
        return new ResultResponse(HttpStatus.OK.value(), "비밀번호 찾기에 성공하였습니다.");
    }

    /**
     * 이메일로 아이디를 찾는 경우 유효성 검사
     *
     * @param name  회원 이름
     * @param email 회원 이메일
     * @return 에러 메시지 (문제가 없다면 빈 문자열)
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
     * 휴대전화번호로 아이디를 찾는 경우 유효성 검사
     *
     * @param name  회원 이름
     * @param phonenumber 회원 휴대전화번호
     * @return 에러 메시지 (문제가 없다면 빈 문자열)
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
     * 이메일로 비밀번호 찾는 경우 유효성 검사
     *
     * @param loginId 회원 로그인 아이디
     * @param name  회원 이름
     * @param email 회원 이메일
     * @return 에러 메시지 (문제가 없다면 빈 문자열)
     */
    private String validateFindPasswordByEmail(String loginId, String name, String email) {
        String errorMessage = "";
        if (!StringUtils.hasText(loginId)) {
            errorMessage = "아이디를 입력해 주세요.";
        } else if (!Pattern.matches("(?=.*[a-z])(?=.*\\d)[a-z\\d]{5,20}$", loginId)) {
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
     * 휴대전화번호로 비밀번호 찾는 경우 유효성 검사
     *
     * @param loginId 회원 로그인 아이디
     * @param name  회원 이름
     * @param phonenumber 회원 휴대전화번호
     * @return 에러 메시지 (문제가 없다면 빈 문자열)
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
