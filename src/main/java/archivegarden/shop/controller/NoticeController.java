package archivegarden.shop.controller;

import archivegarden.shop.dto.community.notice.AddNoticeForm;
import archivegarden.shop.dto.community.notice.EditNoticeForm;
import archivegarden.shop.dto.community.notice.NoticeDetailsDto;
import archivegarden.shop.dto.community.notice.NoticeListDto;
import archivegarden.shop.entity.Member;
import archivegarden.shop.service.community.NoticeService;
import archivegarden.shop.web.annotation.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/community/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping
    public String notices(@RequestParam(name = "page", defaultValue = "1") int page, Model model) {
        PageRequest pageRequest = PageRequest.of(page - 1, 10);
        Page<NoticeListDto> noticeDtos = noticeService.getNotices(pageRequest);
        model.addAttribute("notices", noticeDtos);
        return "community/notice/notice_list";
    }

    @GetMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String addNoticeForm(@ModelAttribute("notice") AddNoticeForm form) {
        return "community/notice/add_notice";
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String addNotice(@Valid @ModelAttribute("notice") AddNoticeForm form, BindingResult bindingResult, @CurrentUser Member loginMember, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "community/notice/add_notice";
        }

        Long noticeId = noticeService.saveNotice(form, loginMember);
        redirectAttributes.addAttribute("noticeId", noticeId);
        return "redirect:/community/notice/{noticeId}";
    }

    @GetMapping("/{noticeId}")
    public String notice(@PathVariable("noticeId") Long noticeId, Model model) {
        NoticeDetailsDto noticeDto = noticeService.getNotice(noticeId);
        model.addAttribute("notice", noticeDto);
        return "community/notice/notice_details";
    }

    @GetMapping("/{noticeId}/edit")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String editNoticeForm(@PathVariable("noticeId") Long noticeId, Model model) {
        EditNoticeForm form = noticeService.getEditNoticeForm(noticeId);
        model.addAttribute("notice", form);
        return "community/notice/edit_notice";
    }

    @PostMapping("/{noticeId}/edit")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String editNotice(@Valid @ModelAttribute("notice") EditNoticeForm form, BindingResult bindingResult, @PathVariable("noticeId") Long noticeId) {
        if (bindingResult.hasErrors()) {
            return "community/notice/edit_notice";
        }

        noticeService.editNotice(noticeId, form);
        return "redirect:/community/notice/{noticeId}";
    }

    @GetMapping("/{noticeId}/delete")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String deleteNotice(@PathVariable("noticeId") Long noticeId) {
        noticeService.deleteNotice(noticeId);
        return "redirect:/community/notice";
    }
}
