package archivegarden.shop.controller.admin.help;

import archivegarden.shop.dto.admin.AdminSearchForm;
import archivegarden.shop.dto.admin.help.notice.*;
import archivegarden.shop.entity.Admin;
import archivegarden.shop.service.admin.help.AdminNoticeService;
import archivegarden.shop.web.annotation.CurrentAdmin;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/notice")
@RequiredArgsConstructor
public class AdminNoticeController {

    private final AdminNoticeService noticeService;

    //공지사항 등록 폼
    @GetMapping("/add")
    public String addNoticeForm(@ModelAttribute("form") AddNoticeForm form) {
        return "admin/help/notice/add_notice";
    }

    //공지사항 등록
    @PostMapping("/add")
    public String addNotice(@Valid @ModelAttribute("form") AddNoticeForm form, BindingResult bindingResult,
                            @CurrentAdmin Admin loginAdmin, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "admin/help/notice/add_notice";
        }

        Long noticeId = noticeService.saveNotice(form, loginAdmin);
        redirectAttributes.addAttribute("noticeId", noticeId);
        return "redirect:/admin/notice/{noticeId}";
    }

    //공지사항 단건 조회
    @GetMapping("/{noticeId}")
    public String notice(@PathVariable("noticeId") Long noticeId, Model model) {
        NoticeDetailsDto noticeDetailsDto = noticeService.getNotice(noticeId);
        model.addAttribute("notice", noticeDetailsDto);
        return "admin/help/notice/notice_details";
    }

    //공지사항 목록 조회
    @GetMapping
    public String notices(@RequestParam(name = "page", defaultValue = "1") int page, @ModelAttribute("form") AdminSearchForm form, Model model) {
        PageRequest pageRequest = PageRequest.of(page - 1, 10);
        Page<NoticeListDto> noticeListDtos = noticeService.getNotices(form, pageRequest);
        model.addAttribute("notices", noticeListDtos);
        return "admin/help/notice/notice_list";
    }

    //공지사항 수정폼
    @GetMapping("/{noticeId}/edit")
    public String editNoticeForm(@PathVariable("noticeId") Long noticeId, Model model) {
        EditNoticeForm form = noticeService.getEditNoticeForm(noticeId);
        model.addAttribute("form", form);
        return "admin/help/notice/edit_notice";
    }

    //공지사항 수정
    @PostMapping("/{noticeId}/edit")
    public String editNotice(@Valid @ModelAttribute("form") EditNoticeForm form, BindingResult bindingResult, @PathVariable("noticeId") Long noticeId) {
        if (bindingResult.hasErrors()) {
            return "admin/help/notice/edit_notice";
        }

        noticeService.editNotice(noticeId, form);
        return "redirect:/admin/notice/{noticeId}";
    }

    //공지사항 삭제
    @GetMapping("/{noticeId}/delete")
    public String deleteNotice(@PathVariable("noticeId") Long noticeId) {
        noticeService.deleteNotice(noticeId);
        return "redirect:/admin/notice";
    }
}
