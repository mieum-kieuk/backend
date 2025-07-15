package archivegarden.shop.controller.user.login;

import archivegarden.shop.dto.ResultResponse;
import archivegarden.shop.entity.Member;
import archivegarden.shop.web.annotation.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "로그인", description = "사용자 페이지에서 로그인 관련 AJAX API")
@RestController
@RequestMapping("/ajax/login")
public class LoginAjaxController {

    @Operation(
            summary = "로그인 여부 확인",
            description = "현재 사용자가 로그인 상태인지 확인합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그인된 사용자"),
                    @ApiResponse(responseCode = "401", description = "로그인 안된 사용자")
            }
    )
    @GetMapping("/status")
    public ResultResponse checkLogin(@CurrentUser Member loginMember) {
        if(loginMember == null) {
            return new ResultResponse(HttpStatus.UNAUTHORIZED.value(), "로그인이 필요한 서비스입니다.");
        }

        return new ResultResponse(HttpStatus.OK.value(), "로그인된 사용자입니다.");
    }
}
