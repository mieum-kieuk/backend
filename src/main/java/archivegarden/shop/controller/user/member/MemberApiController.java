package archivegarden.shop.controller.user.member;

import archivegarden.shop.constant.SessionConstants;
import archivegarden.shop.dto.ResultResponse;
import archivegarden.shop.dto.user.member.VerificationCodeRequestDto;
import archivegarden.shop.service.user.member.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.regex.Pattern;

@Tag(name = "회원-사용자-API", description = "사용자 페이지에서 회원 관련 데이터를 처리하는 API입니다.")
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @Operation(
            summary = "로그인 아이디 중복 검사",
            description = "로그인 아이디 사용 가능 여부를 확인합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "사용 가능한 아이디입니다."),
                    @ApiResponse(responseCode = "409", description = "이미 사용 중인 아이디입니다."),
                    @ApiResponse(responseCode = "403", description = "인증된 사용자는 사용할 수 없는 기능입니다.")
            }
    )
    @GetMapping("/login-id/exists")
    public ResultResponse existsLoginId(@RequestParam(name = "value") String loginId) {
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
                    @ApiResponse(responseCode = "200", description = "사용 가능한 이메일입니다."),
                    @ApiResponse(responseCode = "409", description = "이미 사용 중인 이메일입니다."),
                    @ApiResponse(responseCode = "403", description = "인증된 사용자는 사용할 수 없는 기능입니다.")

            }
    )
    @GetMapping("/email/exists")
    public ResultResponse existsEmail(@RequestParam(name = "value") String email) {
        boolean isAvailable = memberService.isAvailableEmail(email);
        if (isAvailable) {
            return new ResultResponse(HttpStatus.OK.value(), "사용 가능한 이메일입니다.");
        } else {
            return new ResultResponse(HttpStatus.CONFLICT.value(), "이미 사용 중인 이메일입니다.");
        }
    }

    @Operation(
            summary = "휴대전화번호 인증번호 전송",
            description = "입력받은 휴대전화번호로 인증번호를 전송합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "인증번호를 성공적으로 전송했습니다."),
                    @ApiResponse(responseCode = "400", description = "휴대전화번호가 유효하지 않습니다."),
                    @ApiResponse(responseCode = "409", description = "이미 사용 중인 휴대전화번호입니다."),
                    @ApiResponse(responseCode = "403", description = "인증된 사용자는 사용할 수 없는 기능입니다."),
                    @ApiResponse(responseCode = "500", description = "인증번호 전송 중 오류가 발생했습니다.")
            }
    )
    @PostMapping("/phonenumber/verification-code")
    public ResultResponse sendPhonenumberVerificationCode(
            @RequestParam("phonenumber1") String phonenumber1,
            @RequestParam("phonenumber2") String phonenumber2,
            @RequestParam("phonenumber3") String phonenumber3
    ) {
        String errorMessage = validatePhonenumber(phonenumber1, phonenumber2, phonenumber3);
        if (StringUtils.hasText(errorMessage)) {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), errorMessage);
        }

        String phonenumber = String.join("-", phonenumber1, phonenumber2, phonenumber3);
        boolean isAvailable = memberService.isAvailablePhonenumber(phonenumber);
        if (!isAvailable) {
            return new ResultResponse(HttpStatus.CONFLICT.value(), "입력하신 휴대전화번호는 이미 다른 계정에서 사용 중 입니다.");
        }

        try {
            memberService.sendVerificationCode(phonenumber);
            return new ResultResponse(HttpStatus.OK.value(), "인증번호가 발송되었습니다.\n인증번호를 받지 못하셨다면 휴대전화번호를 확인해 주세요.");
        } catch (Exception e) {
            return new ResultResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "인증번호 발송 중 오류가 발생했습니다.\n다시 시도해 주세요.");
        }
    }

    @Operation(
            summary = "휴대전화번호 인증번호 검증",
            description = "사용자가 입력한 인증번호가 유효한지 확인합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "인증번호가 일치합니다."),
                    @ApiResponse(responseCode = "400", description = "인증번호가 일치하지 않습니다."),
                    @ApiResponse(responseCode = "403", description = "인증된 사용자는 사용할 수 없는 기능입니다."),
            }
    )
    @PostMapping("/phonenumber/verification-code/verify")
    public ResultResponse verifyVerificationCode(@ModelAttribute VerificationCodeRequestDto verificationCodeRequest) {
        String errorMessage = validatePhonenumber(verificationCodeRequest.getPhonenumber1(), verificationCodeRequest.getPhonenumber2(), verificationCodeRequest.getPhonenumber3());
        if (StringUtils.hasText(errorMessage)) {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), errorMessage);
        }

        return memberService.verifyVerificationCode(verificationCodeRequest);
    }

    @Operation(
            summary = "이메일로 아이디 찾기",
            description = "회원 이름과 이메일을 입력받아 가입된 계정이 있는지 확인합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원 정보 확인에 성공했습니다."),
                    @ApiResponse(responseCode = "400", description = "이름 또는 휴대전화번호가 유효하지 않습니다"),
                    @ApiResponse(responseCode = "404", description = "일치하는 회원 정보를 찾을 수 없습니다."),
                    @ApiResponse(responseCode = "403", description = "인증된 사용자는 사용할 수 없는 기능입니다.")
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
        return new ResultResponse(HttpStatus.OK.value(), "회원 정보 확인에 성공했습니다.");
    }

    @Operation(
            summary = "휴대전화번호로 아이디 찾기",
            description = "회원 이름과 휴대전화번호를 입력받아 가입된 계정이 있는지 확인합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원 정보 확인에 성공했습니다."),
                    @ApiResponse(responseCode = "400", description = "이름 또는 휴대전화번호가 유효하지 않습니다"),
                    @ApiResponse(responseCode = "404", description = "일치하는 회원 정보를 찾을 수 없습니다."),
                    @ApiResponse(responseCode = "403", description = "인증된 사용자는 사용할 수 없는 기능입니다.")
            }
    )
    @PostMapping("/find-id/phonenumber")
    public ResultResponse findLoginIdByPhonenumber(
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
        return new ResultResponse(HttpStatus.OK.value(), "회원 정보 확인에 성공했습니다.");
    }

    @Operation(
            summary = "이메일로 비밀번호 찾기",
            description = "회원 로그인 아이디, 이름, 이메일을 입력받아 가입된 계정이 있는지 확인합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원 정보 확인에 성공했습니다."),
                    @ApiResponse(responseCode = "400", description = "로그인 아이디 도는 이름 또는 이메일이 유효하지 않습니다"),
                    @ApiResponse(responseCode = "404", description = "일치하는 회원 정보를 찾을 수 없습니다."),
                    @ApiResponse(responseCode = "403", description = "인증된 사용자는 사용할 수 없는 기능입니다.")
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
        return new ResultResponse(HttpStatus.OK.value(), "회원 정보 확인에 성공했습니다.");
    }

    @Operation(
            summary = "휴대전화번호로 비밀번호 찾기",
            description = "회원 로그인 아이디, 이름, 휴대전화번호를 입력받아 가입된 계정이 있는지 확인합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원 정보 확인에 성공했습니다."),
                    @ApiResponse(responseCode = "400", description = "로그인 아이디 도는 이름 또는 휴대전화번호가 유효하지 않습니다"),
                    @ApiResponse(responseCode = "404", description = "일치하는 회원 정보를 찾을 수 없습니다."),
                    @ApiResponse(responseCode = "403", description = "인증된 사용자는 사용할 수 없는 기능입니다.")
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
        return new ResultResponse(HttpStatus.OK.value(), "회원 정보 확인에 성공했습니다.");
    }

    /**
     * 휴대전화번호 유효성 검증
     *
     * 휴대전화번호를 구성하는 각 항목(앞자리, 중간, 뒷자리)의 입력 여부와 형식을 검사합니다.
     */
    private String validatePhonenumber(String phonenumber1, String phonenumber2, String phonenumber3) {
        String errorMessage = "";

        if (StringUtils.hasText(phonenumber1) && StringUtils.hasText(phonenumber2) && StringUtils.hasText(phonenumber3)) {
            if (!Pattern.matches("^01(0|1|[6-9])$", phonenumber1) || !Pattern.matches("^[\\d]{3,4}$", phonenumber2) || !Pattern.matches("^[\\d]{4}$", phonenumber3)) {
                errorMessage = "유효한 휴대전화번호를 입력해 주세요.";
            }
        } else {
            errorMessage = "휴대전화번호를 입력해 주세요.";
        }

        return errorMessage;
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
     * @param name        회원 이름
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
     * @param name    회원 이름
     * @param email   회원 이메일
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
     * @param loginId     회원 로그인 아이디
     * @param name        회원 이름
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
