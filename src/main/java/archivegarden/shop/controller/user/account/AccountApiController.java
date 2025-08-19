package archivegarden.shop.controller.user.account;

import archivegarden.shop.dto.user.account.*;
import archivegarden.shop.entity.auth.TokenType;
import archivegarden.shop.service.user.account.AccountService;
import archivegarden.shop.service.user.token.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Tag(name = "회원 계정-사용자-API", description = "사용자 페이지에서 회원 계정 관련 데이터를 처리하는 API입니다.")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AccountApiController {

    private final AccountService accountService;
    private final TokenService tokenService;

    @Operation(
            summary = "이메일로 아이디 찾기",
            description = "회원 이름과 이메일을 입력받아 가입된 계정이 있는지 확인합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원 존재 여부를 확인합니다."),
                    @ApiResponse(responseCode = "400", description = "요청 형식이 올바르지 않습니다."),
                    @ApiResponse(responseCode = "403", description = "인증된 사용자는 사용할 수 없는 기능입니다.")
            }
    )
    @PostMapping("/find-id/email")
    public ResponseEntity<FindAccountResponseDto> findLoginIdByEmail(@RequestBody FindIdByEmailRequestDto req) {
        Optional<Long> memberIdOpt = accountService.findLoginIdByEmail(req.name(), req.email());
        if (memberIdOpt.isEmpty()) {
            return ResponseEntity.ok(
                    new FindAccountResponseDto("NOT_FOUND", "가입 시 입력하신 회원 정보가 맞는지\n다시 한번 확인해 주세요.", null));
        }

        String token = tokenService.issueToken(memberIdOpt.get(), TokenType.FIND_LOGIN_ID);
        return ResponseEntity.ok(
                new FindAccountResponseDto("OK", "회원 존재 여부를 확인했습니다.",  token));
    }

    @Operation(
            summary = "휴대전화번호로 아이디 찾기",
            description = "회원 이름과 휴대전화번호를 입력받아 가입된 계정이 있는지 확인합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원 존재 여부를 확인합니다."),
                    @ApiResponse(responseCode = "400", description = "요청 형식이 올바르지 않습니다."),
                    @ApiResponse(responseCode = "403", description = "인증된 사용자는 사용할 수 없는 기능입니다.")
            }
    )
    @PostMapping("/find-id/phone")
    public ResponseEntity<FindAccountResponseDto> findLoginIdByPhone(@Validated @RequestBody FindIdByPhoneRequestDto req) {
        Optional<Long> memberIdOpt = accountService.findLoginIdByPhone(req.name(), req.phonenumber());
        if (memberIdOpt.isEmpty()) {
            return ResponseEntity.ok(
                    new FindAccountResponseDto("NOT_FOUND", "가입 시 입력하신 회원 정보가 맞는지\n다시 한번 확인해 주세요.", null));
        }

        String token = tokenService.issueToken(memberIdOpt.get(), TokenType.FIND_LOGIN_ID);
        return ResponseEntity.ok(
                new FindAccountResponseDto("OK", "회원 존재 여부를 확인했습니다.", token));
    }

    @Operation(
            summary = "이메일로 비밀번호 찾기",
            description = "회원 로그인 아이디와 이메일을 입력받아 가입된 계정이 있는지 확인합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원 존재 여부를 확인합니다."),
                    @ApiResponse(responseCode = "400", description = "요청 형식이 올바르지 않습니다."),
                    @ApiResponse(responseCode = "403", description = "인증된 사용자는 사용할 수 없는 기능입니다.")
            }
    )
    @PostMapping("/find-password/email")
    public ResponseEntity<FindAccountResponseDto> findPasswordByEmail(@Validated @RequestBody FindPasswordByEmailRequestDto req) {
        Optional<Long> memberIdOpt = accountService.findPasswordByEmail(req.loginId(), req.name(), req.email());
        if (memberIdOpt.isEmpty()) {
            return ResponseEntity.ok(
                    new FindAccountResponseDto("NOT_FOUND", "가입 시 입력하신 회원 정보가 맞는지\n다시 한번 확인해 주세요.", null));
        }

        String token = tokenService.issueToken(memberIdOpt.get(), TokenType.FIND_PASSWORD);
        return ResponseEntity.ok(
                new FindAccountResponseDto("OK", "회원 존재 여부를 확인했습니다.", token));
    }

    @Operation(
            summary = "휴대전화번호로 비밀번호 찾기",
            description = "회원 로그인 아이디와 휴대전화번호를 입력받아 가입된 계정이 있는지 확인합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원 존재 여부를 확인합니다."),
                    @ApiResponse(responseCode = "400", description = "요청 형식이 올바르지 않습니다."),
                    @ApiResponse(responseCode = "403", description = "인증된 사용자는 사용할 수 없는 기능입니다.")
            }
    )
    @PostMapping("/find-password/phonenumber")
    public ResponseEntity<FindAccountResponseDto> findPasswordByPhonenumber(@Validated @RequestBody FindPasswordByPhoneRequestDto req) {
        Optional<Long> memberIdOpt = accountService.findPasswordByPhone(req.loginId(), req.name(), req.phonenumber());
        if (memberIdOpt.isEmpty()) {
            return ResponseEntity.ok(
                    new FindAccountResponseDto("NOT_FOUND", "가입 시 입력하신 회원 정보가 맞는지\n다시 한번 확인해 주세요.", null));
        }

        String token = tokenService.issueToken(memberIdOpt.get(), TokenType.FIND_PASSWORD);
        return ResponseEntity.ok(
                new FindAccountResponseDto("OK", "회원 존재 여부를 확인했습니다.", token));
    }
}
