package archivegarden.shop.controller.user.email;

import archivegarden.shop.dto.common.ApiResponseDto;
import archivegarden.shop.entity.Member;
import archivegarden.shop.service.user.email.EmailService;
import archivegarden.shop.web.annotation.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "이메일-사용자-API", description = "사용자 페이지에서 이메일을 전송하는 API입니다.")
@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailApiController {

    private final EmailService emailService;

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
    public ResponseEntity<ApiResponseDto> verifyEmailLink(@CurrentUser Member loginMember) {
        emailService.sendEmailVerificationLinkInMyPage(loginMember.getEmail(), loginMember.getName());
        return ResponseEntity.ok(
                new ApiResponseDto("OK", "이메일 인증 요청 메일을 전송했습니다."));
    }
}
