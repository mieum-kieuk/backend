package archivegarden.shop.controller.admin.member.membership;

import archivegarden.shop.dto.ResultResponse;
import archivegarden.shop.service.admin.member.membership.AdminMembershipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Membership 관리", description = "관리자 페이지에서 회원 등급 정보를 관리하는 AJAX API")
@RestController
@RequestMapping("/ajax/admin/membership")
@RequiredArgsConstructor
public class AdminMembershipAjaxController {

    private final AdminMembershipService membershipService;

    @Operation(
            summary = "회원 등급명 중복 검사",
            description = "회원 등급명 사용 가능 여부를 확인합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "사용 가능한 회원 등급명"),
                    @ApiResponse(responseCode = "400", description = "이미 사용 중인 회원 등급명")
            }
    )
    @PostMapping("/check/name")
    public ResultResponse checkName(@RequestParam("name") String name) {
        boolean isAvailable = membershipService.isAvailableName(name);
        if(isAvailable) {
            return new ResultResponse(HttpStatus.OK.value(), "사용 가능한 회원 등급명입니다.");
        } else {
            return new ResultResponse(HttpStatus.OK.value(), "이미 존재하는 회원 등급명입니다.");

        }
    }

    @Operation(
            summary = "회원 등급 삭제 요청",
            description = "회원 등급을 삭제합니다. 해당 ID의 회원 등급이 존재하지 않으면 오류를 반환합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원 등급 삭제 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 등급"),
                    @ApiResponse(responseCode = "422", description = "기본 회원 등급은 삭제 불가")
            }
    )
    @DeleteMapping
    public ResultResponse deleteDiscount(@RequestParam("membershipId") Long membershipId) {
        try {
            membershipService.deleteMembership(membershipId);
        } catch(UnsupportedOperationException e) {
            return new ResultResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "기본 회원 등급은 삭제할 수 없습니다.");
        }

        return new ResultResponse(HttpStatus.OK.value(), "삭제가 완료되었습니다.");
    }
}
