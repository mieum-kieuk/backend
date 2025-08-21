package archivegarden.shop.controller.admin.admin;

import archivegarden.shop.dto.common.ApiResponseDto;
import archivegarden.shop.dto.common.AvailabilityResponseDto;
import archivegarden.shop.service.admin.admin.AdminAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Pattern;

@Tag(name = "관리자-관리자-API", description = "관리자 페이지에서 관리자 관련 데이터를 처리하는 API입니다.")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminAdminApiController {

    private final AdminAdminService adminService;

    private static final Pattern PATTERN_LOGIN_ID = Pattern.compile("^(?=.*[a-z])(?=.*\\d)[a-z\\d]{5,20}$");
    private static final Pattern PATTERN_EMAIL = Pattern.compile("^[a-zA-Z\\d]([-_.]?[a-zA-Z\\d])*@[a-zA-Z\\d]*\\.[a-zA-Z]{2,3}$");

    @Operation(
            summary = "로그인 아이디 중복 검사",
            description = "로그인 아이디의 사용 가능 여부를 확인합니다.",
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

        boolean isAvailable = adminService.isAvailableLoginId(normalized);
        String code = isAvailable ? "AVAILABLE" : "DUPLICATED";
        String message = isAvailable ? "사용 가능한 아이디입니다." : "이미 사용 중인 아이디입니다.";

        return ResponseEntity.ok()
                .body(new AvailabilityResponseDto(isAvailable, code, message));
    }

    @Operation(
            summary = "이메일 중복 검사",
            description = "이메일 주소의 사용 가능 여부를 확인합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청을 정상 처리하고 사용 가능 여부 결과를 반환합니다."),
                    @ApiResponse(responseCode = "400", description = "요청 형식이 올바르지 않습니다."),
                    @ApiResponse(responseCode = "403", description = "인증된 사용자는 해당 기능을 이용할 수 없습니다.")
            })
    @GetMapping("/email/exists")
    public ResponseEntity<AvailabilityResponseDto> existsEmail(@RequestParam(name = "email") String email) {
        String normalized = email == null ? "" : email.trim();
        if(!PATTERN_EMAIL.matcher(normalized).matches()) {
            return ResponseEntity.badRequest()
                    .body(new AvailabilityResponseDto(false, "INVALID_FORMAT", "이메일 형식이 올바르지 않습니다."));
        }

        boolean isAvailable = adminService.isAvailableEmail(email);
        String code = isAvailable ? "AVAILABLE" : "DUPLICATED";
        String message = isAvailable ? "사용 가능한 이메일입니다." : "이미 사용 중인 이메일입니다.";

        return ResponseEntity.ok()
                .body(new AvailabilityResponseDto(isAvailable, code, message));
    }

    @Operation(
            summary = "관리자 삭제",
            description = "관리자 계정을 삭제합니다",
            responses = {
                    @ApiResponse(responseCode = "200", description = "관리자 삭제 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 관리자")
            })
    @DeleteMapping("/admin")
    public ResponseEntity<ApiResponseDto> deleteAdmin(@RequestParam("adminId") Long adminId) {
        adminService.deleteAdmin(adminId);
        return ResponseEntity.ok(new ApiResponseDto("OK", "관리자가 삭제되었습니다."));
    }

    @Operation(
            summary = "관리자 권한 부여",
            description = "관리자 권한을 부여하고 권한 부여 완료 이메일을 전송합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "관리자 권한 부여 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 관리자"),
                    @ApiResponse(responseCode = "500", description = "권한 부여 완료 이메일 전송중 오류 발생")
            }
    )
    @PostMapping("/admin/auth")
    public ResponseEntity<ApiResponseDto> authorizeAdmin(@RequestParam("adminId") Long adminId) {
        adminService.authorizeAdmin(adminId);
        return ResponseEntity.ok(new ApiResponseDto("OK", "관리자 권한이 부여되었습니다."));
    }
}
