package archivegarden.shop.controller.user.community;

import archivegarden.shop.dto.admin.help.notice.NoticeDetailsDto;
import archivegarden.shop.dto.user.community.notice.NoticeSearchForm;
import archivegarden.shop.dto.user.community.notice.NoticeListDto;
import archivegarden.shop.service.user.community.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/community/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    /**
     * 공지사항 상세 페이지를 조회하는 요청을 처리하는 메서드
     */
    @GetMapping("/{noticeId}")
    public String notice(@PathVariable("noticeId") Long noticeId, Model model) {
        NoticeDetailsDto noticeDto = noticeService.getNotice(noticeId);
        model.addAttribute("notice", noticeDto);
        return "user/community/notice/notice_details";
    }

    /**
     * 공지사항 목록을 조회하는 요청을 처리하는 메서드
     */
    @GetMapping
    public String notices(@RequestParam(name = "page", defaultValue = "1") int page, @ModelAttribute("form") NoticeSearchForm form, Model model) {
        PageRequest pageRequest = PageRequest.of(page - 1, 10);
        Page<NoticeListDto> noticeDtos = noticeService.getNotices(form, pageRequest);
        model.addAttribute("notices", noticeDtos);
        return "user/community/notice/notice_list";
    }
}
