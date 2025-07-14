package archivegarden.shop.controller.user.community.notice;

import archivegarden.shop.dto.admin.help.notice.NoticeDetailsDto;
import archivegarden.shop.dto.user.community.notice.NoticeSearchForm;
import archivegarden.shop.dto.user.community.notice.NoticeListDto;
import archivegarden.shop.service.user.community.NoticeService;
import archivegarden.shop.util.PageRequestUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Tag(name = "공지사항", description = "사용자 페이지에서 공지사항 관련 API")
@Controller
@RequestMapping("/community/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @Operation(
            summary = "공지사항 상세 조회",
            description = "공지사항 상세 정보를 조회합니다."
    )
    @GetMapping("/{noticeId}")
    public String notice(@PathVariable("noticeId") Long noticeId, Model model) {
        NoticeDetailsDto noticeDetailsDto = noticeService.getNotice(noticeId);
        model.addAttribute("notice", noticeDetailsDto);
        return "user/community/notice/notice_details";
    }

    @Operation(
            summary = "공지사항 목록 조회",
            description = "검색 조건에 따라 공지사항 목록을 페이징하여 조회합니다"
    )
    @GetMapping
    public String notices(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @ModelAttribute("searchForm") NoticeSearchForm form,
            Model model
    ) {
        PageRequest pageRequest = PageRequestUtil.of(page);
        Page<NoticeListDto> noticeListDtos = noticeService.getNotices(form, pageRequest);
        model.addAttribute("notices", noticeListDtos);
        return "user/community/notice/notice_list";
    }
}
