package archivegarden.shop.controller.user.mypage;

import archivegarden.shop.dto.ResultResponse;
import archivegarden.shop.entity.Member;
import archivegarden.shop.service.user.member.MemberService;
import archivegarden.shop.web.annotation.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ajax/mypage")
@RequiredArgsConstructor
public class MypageAjaxController {

    private final MemberService memberService;

    /**
     * 본인 확인 요청을 처리하는 메서드
     */
    @PostMapping("/validate")
    @PreAuthorize("#loginMember.loginId == principal.username")
    public ResultResponse validateMember(@RequestParam("password") String password, @CurrentUser Member loginMember) {
        boolean isCurrentMember = memberService.validateIdentity(loginMember, password);
        if(isCurrentMember) {
            return new ResultResponse(HttpStatus.OK.value(), "본인 확인이 완료되었습니다.");
        } else {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), "비밀번호가 일치하지 않습니다.");
        }
    }

    /**
     * 기존의 비밀번호화 새 비밀번호 다른지 확인하는 메서드
     */
    @PostMapping("/check/password")
    @PreAuthorize("#loginMember.loginId == principal.username")
    public ResultResponse isNewPassword(@RequestParam("newPassword") String newPassword, @CurrentUser Member loginMember) {
        boolean isNewPassword = memberService.isNewPassword(newPassword, loginMember.getPassword());
        if(isNewPassword) {
            return new ResultResponse(HttpStatus.OK.value(), "사용 가능한 비밀번호입니다.");
        } else {
            return new ResultResponse(HttpStatus.CONFLICT.value(), "현재 사용 중인 비밀번호입니다.");
        }
    }
}
