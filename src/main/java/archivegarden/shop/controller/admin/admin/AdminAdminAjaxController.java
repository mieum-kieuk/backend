package archivegarden.shop.controller.admin.admin;

import archivegarden.shop.dto.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ajax/admin/join")
@RequiredArgsConstructor
public class AdminAdminAjaxController {

    private final AdminJoinService adminService;

    /**
     *  관리자 로그인 아이디 중복 여부를 검사하는 메서드
     */
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/loginId/check")
    public ResultResponse checkLoginId(@RequestParam(name = "loginId") String loginId) {
        boolean isAvailableLoginId = adminService.isAvailableLoginId(loginId);
        if(isAvailableLoginId) {
            return new ResultResponse(HttpStatus.OK.value(), "사용 가능한 아이디입니다.");
        } else {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), "이미 사용 중인 아이디입니다.");
        }
    }

    /**
     *  관리자 이메일 중복 여부를 검사하는 메서드
     */
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/email/check")
    public ResultResponse checkEmail(@RequestParam(name = "email") String email) {
        boolean isAvailableEmail = adminService.isAvailableEmail(email);
        if(isAvailableEmail) {
            return new ResultResponse(HttpStatus.OK.value(), "사용 가능한 이메일입니다.");
        } else {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), "이미 사용 중인 이메일입니다.");
        }
    }
}
