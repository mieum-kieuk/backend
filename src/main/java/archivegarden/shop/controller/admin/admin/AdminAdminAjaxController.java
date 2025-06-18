package archivegarden.shop.controller.admin.admin;

import archivegarden.shop.dto.ResultResponse;
import archivegarden.shop.service.admin.admin.AdminAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin 관리", description = "관리자 페이지에서 관리자 정보를 관리하는 AJAX API")
@RestController
@RequestMapping("/ajax/admin")
@RequiredArgsConstructor
public class AdminAdminAjaxController {

    private final AdminAdminService adminService;

    @Operation(
            summary = "로그인 아이디 중복 검사",
            description = "로그인 아이디의 사용 가능 여부를 확인합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "사용 가능한 아이디"),
                    @ApiResponse(responseCode = "400", description = "이미 사용 중인 아이디")
            }
    )
    @PostMapping("/check/loginId")
    public ResultResponse checkLoginIdDuplicate(@RequestParam(name = "loginId") String loginId) {
        boolean isLoginIdAvailable = adminService.isLoginIdAvailable(loginId);
        if (isLoginIdAvailable) {
            return new ResultResponse(HttpStatus.OK.value(), "사용 가능한 아이디입니다.");
        } else {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), "이미 사용 중인 아이디입니다.");
        }
    }

    @Operation(
            summary = "이메일 중복 검사",
            description = "이메일 주소의 사용 가능 여부를 확인합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "사용 가능한 이메일"),
                    @ApiResponse(responseCode = "400", description = "이미 사용 중인 이메일")
            })
    @PostMapping("/check/email")
    public ResultResponse checkEmailDuplicate(@RequestParam(name = "email") String email) {
        boolean isEmailAvailable = adminService.isEmailAvailable(email);
        if (isEmailAvailable) {
            return new ResultResponse(HttpStatus.OK.value(), "사용 가능한 이메일입니다.");
        } else {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), "이미 사용 중인 이메일입니다.");
        }
    }

    @Operation(
            summary = "관리자 삭제",
            description = "선택된 관리자 계정을 삭제합니다. 해당 ID의 관리자가 존재하지 않으면 오류를 반환합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "관리자 삭제 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 관리자")
            })
    @DeleteMapping("/admin")
    public ResultResponse deleteAdmin(@RequestParam("adminId") Long adminId) {
        adminService.deleteAdmin(adminId);
        return new ResultResponse(HttpStatus.OK.value(), "관리자가 삭제되었습니다.");
    }

    @Operation(
            summary = "관리자 권한 부여",
            description = "선택된 관리자에게 관리자 권한을 부여하고 권한 부여 완료 이메일을 전송합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "관리자 권한 부여 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 관리자"),
                    @ApiResponse(responseCode = "500", description = "권한 부여 완료 이메일 전송중 오류 발생")
            }
    )
    @PostMapping("/admin/auth")
    public ResultResponse authorizeAdmin(@RequestParam("adminId") Long adminId) {
        adminService.authorizeAdmin(adminId);
        return new ResultResponse(HttpStatus.OK.value(), "관리자 권한이 부여되었습니다.");
    }
}
