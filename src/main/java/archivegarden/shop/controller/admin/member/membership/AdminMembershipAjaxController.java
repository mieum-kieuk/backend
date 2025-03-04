package archivegarden.shop.controller.admin.member.membership;

import archivegarden.shop.dto.ResultResponse;
import archivegarden.shop.service.admin.member.membership.AdminMembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ajax/admin/membership")
@RequiredArgsConstructor
public class AdminMembershipAjaxController {

    private final AdminMembershipService membershipService;

    /**
     * 회원 등급명 중복 여부를 검사하는 메서드
     */
    @PostMapping("/check/name")
    public ResultResponse checkName(@RequestParam("name") String name) {
        boolean isAvailable = membershipService.isAvailableName(name);
        if(isAvailable) {
            return new ResultResponse(HttpStatus.OK.value(), "사용 가능한 회원 등급명입니다.");
        } else {
            return new ResultResponse(HttpStatus.OK.value(), "이미 존재하는 회원 등급명입니다.");

        }
    }

    /**
     * 회원 등급 한개 삭제 요청을 처리하는 메서드
     */
    @DeleteMapping
    public ResultResponse deleteDiscount(@RequestParam("membershipId") Short membershipId) {
        try {
            membershipService.deleteMembership(membershipId);
        } catch(IllegalStateException e) {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), "기본 등급은 삭제할 수 없습니다.");
        }

        return new ResultResponse(HttpStatus.OK.value(), "삭제가 완료되었습니다.");
    }
}
