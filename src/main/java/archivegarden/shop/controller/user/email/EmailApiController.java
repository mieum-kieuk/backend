package archivegarden.shop.controller.user.email;

import archivegarden.shop.dto.ResultResponse;
import archivegarden.shop.entity.Member;
import archivegarden.shop.service.user.email.EmailService;
import archivegarden.shop.web.annotation.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static archivegarden.shop.constant.SessionConstants.FIND_PASSWORD_EMAIL_KEY;

@Tag(name = "이메일-사용자-API", description = "사용자 페이지에서 이메일을 전송하는 API입니다.")
@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailApiController {

    private final EmailService emailService;

    @Operation(
            summary = "임시 비밀번호 발급",
            description = "입력한 이메일 주소로 임시 비밀번호를 발급하여 전송합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "임시 비밀번호를 성공적으로 전송했습니다."),
                    @ApiResponse(responseCode = "403", description = "인증된 사용자는 사용할 수 없는 기능입니다.")
            }
    )
    @PostMapping("/temp-password")
    public ResultResponse sendTempPassword(@RequestParam(name = "email") String email, HttpSession session) {
        emailService.sendTempPassword(email);
        session.setAttribute(FIND_PASSWORD_EMAIL_KEY, email);
        return new ResultResponse(HttpStatus.OK.value(), "임시 비밀번호가 이메일로 전송되었습니다.");
    }

    @Operation(
            summary = "이메일 인증 메일 전송",
            description = "로그인한 사용자의 이메일로 인증 링크를 전송합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "이메일을 성공적으로 전송했습니다."),
                    @ApiResponse(responseCode = "401", description = "로그인이 필요한 기능입니다."),
                    @ApiResponse(responseCode = "403", description = "로그인 되어 있으나 다른 사용자의 데이터에 접근할 수 없습니다.")
            }
    )
    @PreAuthorize("#loginMember.loginId == principal.username")
    @PostMapping("/verification")
    public ResultResponse verifyEmailLink(@CurrentUser Member loginMember) {
        emailService.sendEmailVerificationLinkInMyPage(loginMember.getEmail(), loginMember.getName());
        return new ResultResponse(HttpStatus.OK.value(), "이메일 인증 요청 메일을 전송했습니다.");
    }
}
