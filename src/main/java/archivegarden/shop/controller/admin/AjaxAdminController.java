package archivegarden.shop.controller.admin;

import archivegarden.shop.dto.ResultResponse;
import archivegarden.shop.service.admin.admins.AdminAdminService;
import archivegarden.shop.service.admin.help.AdminNoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ajax/admin")
@RequiredArgsConstructor
public class AjaxAdminController {

    private final AdminAdminService adminService;
    private final AdminNoticeService noticeService;

    //전체 관리자 관리페이지에서 관리자 단건 삭제
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/admins/delete")
    public ResultResponse deleteAdmin(@RequestParam("adminId") Long adminId) {
        adminService.deleteAdmin(adminId);
        return new ResultResponse(HttpStatus.OK.value(), "삭제가 완료되었습니다.");
    }

    //전체 관리자 관리페이지에서 권한 부여
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/admins/auth")
    public ResultResponse authorizeAdmin(@RequestParam("adminId") Long adminId) {
        adminService.authorizeAdmin(adminId);
        return new ResultResponse(HttpStatus.OK.value(), "승인되었습니다.");
    }

    //공지사항 삭제
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/notice/delete")
    public ResultResponse deleteNotice(@RequestParam("noticeId") Long noticeId) {
        noticeService.deleteNotice(noticeId);
        return new ResultResponse(HttpStatus.OK.value(), "삭제가 완료되었습니다.");
    }
}
