package archivegarden.shop.controller.admin.help.notice;

import archivegarden.shop.dto.admin.AdminSearchCondition;
import archivegarden.shop.dto.admin.help.notice.*;
import archivegarden.shop.entity.Admin;
import archivegarden.shop.service.admin.help.notice.AdminNoticeService;
import archivegarden.shop.util.PageRequestUtil;
import archivegarden.shop.web.annotation.CurrentAdmin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Tag(name = "공지사항", description = "관리자 페이지에서 공지사항 관련 API")
@Controller
@RequestMapping("/admin/notice")
@RequiredArgsConstructor
public class AdminNoticeController {

    private final AdminNoticeService noticeService;

    @Operation(
            summary = "공지사항 등록 폼 표시",
            description = "공지사항 등록을 위한 화면을 반환합니다."
    )
    @GetMapping("/add")
    public String addNoticeForm(@ModelAttribute("addForm") AddNoticeForm form) {
        return "admin/help/notice/add_notice";
    }

    @Operation(
            summary = "공지사항 등록 요청",
            description = "공지사항을 등록하고 상세 페이지로 리다이렉트합니다."
    )
    @PostMapping("/add")
    public String addNotice(
            @Valid @ModelAttribute("addForm") AddNoticeForm form,
            @NotNull BindingResult bindingResult,
            @CurrentAdmin Admin loginAdmin,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "admin/help/notice/add_notice";
        }

        Long noticeId = noticeService.saveNotice(form, loginAdmin);
        redirectAttributes.addAttribute("noticeId", noticeId);
        return "redirect:/admin/notice/{noticeId}";
    }

    @Operation(
            summary = "공지사항 상세 조회",
            description = "공지사항 상세 정보를 조회합니다."
    )
    @GetMapping("/{noticeId}")
    public String notice(@PathVariable("noticeId") Long noticeId, Model model) {
        NoticeDetailsDto noticeDetailsDto = noticeService.getNotice(noticeId);
        model.addAttribute("notice", noticeDetailsDto);
        return "admin/help/notice/notice_details";
    }

    @Operation(
            summary = "공지사항 목록 조회",
            description = "검색 조건에 따라 공지사항 목록을 페이징하여 조회합니다"
    )
    @GetMapping
    public String notices(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @ModelAttribute("cond") AdminSearchCondition cond,
            Model model
    ) {
        PageRequest pageRequest = PageRequestUtil.of(page - 1);
        Page<NoticeListDto> noticeListDtos = noticeService.getNotices(cond, pageRequest);
        model.addAttribute("notices", noticeListDtos);
        return "admin/help/notice/notice_list";
    }

    @Operation(
            summary = "공지사항 수정 폼 표시",
            description = "기존 공지사항을 수정하기 위한 화면을 반환합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공적으로 공지사항 수정 폼 반환"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 공지사항")
            }
    )
    @GetMapping("/{noticeId}/edit")
    public String editNoticeForm(@PathVariable("noticeId") Long noticeId, Model model) {
        EditNoticeForm form = noticeService.getEditNoticeForm(noticeId);
        model.addAttribute("editForm", form);
        return "admin/help/notice/edit_notice";
    }

    @Operation(
            summary = "공지사항 수정 요청",
            description = "기존 공지사항을 수정하고 상세 페이지로 리다이렉트합니다."
    )
    @PostMapping("/{noticeId}/edit")
    public String editNotice(
            @Valid @ModelAttribute("editForm") EditNoticeForm form,
            BindingResult bindingResult,
            @PathVariable("noticeId") Long noticeId
    ) {
        if (bindingResult.hasErrors()) {
            return "admin/help/notice/edit_notice";
        }

        noticeService.editNotice(noticeId, form);
        return "redirect:/admin/notice/{noticeId}";
    }
}
