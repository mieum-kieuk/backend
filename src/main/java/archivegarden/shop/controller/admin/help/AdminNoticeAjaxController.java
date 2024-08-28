package archivegarden.shop.controller.admin.help;

import archivegarden.shop.dto.ResultResponse;
import archivegarden.shop.service.admin.help.AdminNoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ajax/admin/notice")
@RequiredArgsConstructor
public class AdminNoticeAjaxController {

    private final AdminNoticeService noticeService;

    /**
     * 공지사항을 삭제 요청을 처리하는 메서드
     */
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping
    public ResultResponse deleteNotice(@RequestParam("noticeId") Long noticeId) {
        noticeService.deleteNotice(noticeId);
        return new ResultResponse(HttpStatus.OK.value(), "삭제가 완료되었습니다.");
    }
}
