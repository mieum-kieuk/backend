package archivegarden.shop.controller.user.member;

import archivegarden.shop.dto.common.ApiResponseDto;
import archivegarden.shop.dto.common.AvailabilityResponseDto;
import archivegarden.shop.entity.auth.VerificationCodeResult;
import archivegarden.shop.service.user.member.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Pattern;

@Tag(name = "회원-사용자-API", description = "사용자 페이지에서 회원 관련 데이터를 처리하는 API입니다.")
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    private static final Pattern PATTERN_LOGIN_ID = Pattern.compile("^(?=.*[a-z])(?=.*\\d)[a-z\\d]{5,20}$");
    private static final Pattern PATTERN_EMAIL = Pattern.compile("^[a-zA-Z\\d]([-_.]?[a-zA-Z\\d])*@[a-zA-Z\\d]*\\.[a-zA-Z]{2,3}$");
    private static final Pattern PATTERN_PHONE_NUMBER = Pattern.compile("^01[016789]\\d{7,8}$");


    @Operation(
            summary = "로그인 아이디 중복 검사",
            description = "로그인 아이디 사용 가능 여부를 확인합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청을 정상 처리하고 사용 가능 여부 결과를 반환합니다."),
                    @ApiResponse(responseCode = "400", description = "요청 형식이 올바르지 않습니다."),
                    @ApiResponse(responseCode = "403", description = "인증된 사용자는 해당 기능을 이용할 수 없습니다.")
            }
    )
    @GetMapping("/login-id/exists")
    public ResponseEntity<AvailabilityResponseDto> existsLoginId(@RequestParam("loginId") String loginId) {
        String normalized = loginId == null ? "" : loginId.trim();
        if (!PATTERN_LOGIN_ID.matcher(normalized).matches()) {
            return ResponseEntity.badRequest()
                    .body(new AvailabilityResponseDto(false, "INVALID_FORMAT", "아이디 형식이 올바르지 않습니다."));
        }

        boolean isAvailable = memberService.isAvailableLoginId(normalized);
        String code = isAvailable ? "AVAILABLE" : "DUPLICATED";
        String message = isAvailable ? "사용 가능한 아이디입니다." : "이미 사용 중인 아이디입니다.";

        return ResponseEntity.ok()
                .body(new AvailabilityResponseDto(isAvailable, code, message));
    }

    @Operation(
            summary = "이메일 중복 검사",
            description = "이메일 사용 가능 여부를 확인합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청을 정상 처리하고 사용 가능 여부 결과를 반환합니다."),
                    @ApiResponse(responseCode = "400", description = "요청 형식이 올바르지 않습니다."),
                    @ApiResponse(responseCode = "403", description = "인증된 사용자는 해당 기능을 이용할 수 없습니다.")
            }
    )
    @GetMapping("/email/exists")
    public ResponseEntity<AvailabilityResponseDto> existsEmail(@RequestParam("email") String email) {
        String normalized = email == null ? "" : email.trim();
        if(!PATTERN_EMAIL.matcher(normalized).matches()) {
            return ResponseEntity.badRequest()
                    .body(new AvailabilityResponseDto(false, "INVALID_FORMAT", "이메일 형식이 올바르지 않습니다."));

        }

        boolean isAvailable = memberService.isAvailableEmail(email);
        String code = isAvailable ? "AVAILABLE" : "DUPLICATED";
        String message = isAvailable ? "사용 가능한 이메일입니다." : "이미 사용 중인 이메일입니다.";

        return ResponseEntity.ok()
                .body(new AvailabilityResponseDto(isAvailable, code, message));
    }

    @Operation(
            summary = "휴대전화번호 인증번호 발송",
            description = "입력받은 휴대전화번호로 인증번호를 발송합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청을 정상 처리하고 발송 결과를 반환합니다."),
                    @ApiResponse(responseCode = "400", description = "요청 형식이 올바르지 않습니다."),
                    @ApiResponse(responseCode = "403", description = "인증된 사용자는 해당 기능을 이용할 수 없습니다."),
                    @ApiResponse(responseCode = "409", description = "이미 사용 중인 휴대전화번호입니다."),
                    @ApiResponse(responseCode = "500", description = "인증번호 발송 중 오류가 발생했습니다.")
            }
    )
    @PostMapping("/phone/verification-code")
    public ResponseEntity<ApiResponseDto> sendVerificationCode(@RequestParam("phonenumber") String phonenumber) {
        if (!PATTERN_PHONE_NUMBER.matcher(phonenumber).matches()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponseDto("INVALID_FORMAT", "휴대전화번호 형식이 올바르지 않습니다."));
        }

        if (!memberService.isAvailablePhonenumber(phonenumber)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponseDto("DUPLICATED", "입력하신 휴대전화번호는 이미 다른 계정에서 사용 중 입니다."));
        }


        memberService.sendVerificationCode(phonenumber);
        return ResponseEntity.ok(new ApiResponseDto("OK", "인증번호가 발송되었습니다. 받지 못하셨다면 휴대전화번호를 확인해 주세요."));
    }

    @Operation(
            summary = "휴대전화번호 인증번호 검증",
            description = "사용자가 입력한 인증번호가 유효한지 확인합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "인증번호 검증에 성공하였습니다."),
                    @ApiResponse(responseCode = "400", description = "요청 형식이 올바르지 않거나 인증번호가 일치하지 않거나 인증번호가 만료되었습니다."),
                    @ApiResponse(responseCode = "403", description = "인증된 사용자는 사용할 수 없는 기능입니다."),
            }
    )
    @PostMapping("/phone/verification-code/verify")
    public ResponseEntity<ApiResponseDto> verifyVerificationCode(
            @RequestParam("phonenumber") String phonenumber,
            @RequestParam("verificationCode") String verificationCode
    ) {
        if (!PATTERN_PHONE_NUMBER.matcher(phonenumber).matches()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponseDto("INVALID_FORMAT", "휴대전화번호 형식이 올바르지 않습니다."));
        }

        VerificationCodeResult result = memberService.verifyVerificationCode(phonenumber, verificationCode);

        switch (result) {
            case SUCCESS:
                return ResponseEntity.ok(
                        new ApiResponseDto("VERIFICATION_SUCCESS", "인증번호 검증에 성공하였습니다."));
            case MISMATCH:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponseDto("VERIFICATION_MISMATCH", "인증번호가 일치하지 않습니다."));
            case EXPIRED:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponseDto("VERIFICATION_EXPIRED", "인증번호가 만료되었습니다."));
            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ApiResponseDto("UNKNOWN_ERROR", "알 수 없는 오류가 발생했습니다."));
        }
    }
}
