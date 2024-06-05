package archivegarden.shop.controller.admin.help;

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

    @GetMapping("/add")
    public String addNoticeForm(@ModelAttribute("form") AddNoticeForm form) {
        return "admin/help/notice/add_notice";
    }

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

    @GetMapping("/{noticeId}")
    public String notice(@PathVariable("noticeId") Long noticeId, Model model) {
        NoticeDetailsDto noticeDto = noticeService.getNotice(noticeId);
        model.addAttribute("notice", noticeDto);
        return "admin/help/notice/notice_details";
    }

    @GetMapping
    public String notices(@RequestParam(name = "page", defaultValue = "1") int page, @ModelAttribute("form") NoticeSearchForm form, Model model) {
        PageRequest pageRequest = PageRequest.of(page - 1, 10);
        Page<NoticeListDto> noticeDtos = noticeService.getNotices(form, pageRequest);
        model.addAttribute("notices", noticeDtos);
        return "admin/help/notice/notice_list";
    }

    @GetMapping("/{noticeId}/edit")
    public String editNoticeForm(@PathVariable("noticeId") Long noticeId, Model model) {
        EditNoticeForm form = noticeService.getEditNoticeForm(noticeId);
        model.addAttribute("form", form);
        return "admin/help/notice/edit_notice";
    }

    @PostMapping("/{noticeId}/edit")
    public String editNotice(@Valid @ModelAttribute("form") EditNoticeForm form, BindingResult bindingResult, @PathVariable("noticeId") Long noticeId) {
        if (bindingResult.hasErrors()) {
            return "admin/help/notice/edit_notice";
        }

        noticeService.editNotice(noticeId, form);
        return "redirect:/admin/notice/{noticeId}";
    }

    @GetMapping("/{noticeId}/delete")
    public String deleteNotice(@PathVariable("noticeId") Long noticeId) {
        noticeService.deleteNotice(noticeId);
        return "redirect:/admin/notice";
    }
}
