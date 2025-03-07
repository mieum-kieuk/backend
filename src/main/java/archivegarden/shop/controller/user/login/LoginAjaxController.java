package archivegarden.shop.controller.user.login;

import archivegarden.shop.dto.ResultResponse;
import archivegarden.shop.entity.Member;
import archivegarden.shop.web.annotation.CurrentUser;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginAjaxController {

    @GetMapping("/ajax/check/login")
    public ResultResponse checkLogin(@CurrentUser Member loginMember) {
        if(loginMember == null) {
            return new ResultResponse(HttpStatus.UNAUTHORIZED.value(), "로그인이 필요한 서비스입니다.");
        }

        return new ResultResponse(HttpStatus.OK.value(), "로그인된 사용자입니다.");
    }
}
