package archivegarden.shop.controller.admin.help.notice;

import archivegarden.shop.dto.ResultResponse;
import archivegarden.shop.service.admin.help.notice.AdminNoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Notice 관리", description = "관리자 페이지 AJAX용 공지사항 API")
@RestController
@RequestMapping("/ajax/admin/notice")
@RequiredArgsConstructor
public class AdminNoticeAjaxController {

    private final AdminNoticeService noticeService;

    @Operation(
            summary = "공지사항 삭제 요청",
            description = "지정된 공지사항을 삭제합니다. 해당 ID의 공지사항이 존재하지 않으면 오류를 반환합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "공지사항 삭제 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 공지사항")
            }
    )
    @DeleteMapping
    public ResultResponse deleteNotice(@RequestParam("noticeId") Long noticeId) {
        noticeService.deleteNotice(noticeId);
        return new ResultResponse(HttpStatus.OK.value(), "삭제되었습니다.");
    }
}
