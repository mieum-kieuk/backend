package archivegarden.shop.controller.admin.help.notice;

import archivegarden.shop.dto.admin.AdminSearchCondition;
import archivegarden.shop.dto.admin.help.notice.*;
import archivegarden.shop.entity.Admin;
import archivegarden.shop.service.admin.help.notice.AdminNoticeService;
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

@Tag(name = "Notice 관리", description = "관리자 페이지에서 공지사항 관련 API")
@Controller
@RequestMapping("/admin/notice")
@RequiredArgsConstructor
public class AdminNoticeController {

    private final AdminNoticeService noticeService;

    @Operation(
            summary = "공지사항 등록 폼 표시",
            description = "새로운 공지사항 등록을 위한 화면을 반환합니다.",
            responses = { @ApiResponse(responseCode = "200", description = "성공적으로 공지사항 등록 폼 반환") }
    )
    @GetMapping("/add")
    public String addNoticeForm(@ModelAttribute("addNoticeForm") AddNoticeForm form) {
        return "admin/help/notice/add_notice";
    }

    @Operation(
            summary = "공지사항 등록 요청",
            description = "새로운 공지사항을 등록하고 상세 페이지로 리다이렉트합니다.",
            responses = {
                    @ApiResponse(responseCode = "302", description = "등록 성공 후 상세 페이지로 리다이렉트"),
                    @ApiResponse(responseCode = "400", description = "유효성 검증 실패 시 공지사항 등록 폼으로 다시 이동")
            }
    )
    @PostMapping("/add")
    public String addNotice(
            @Valid @ModelAttribute("addNoticeForm") AddNoticeForm form,
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
            description = "특정 공지사항의 상세 정보를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "공지사항 상세 페이지 반환"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 공지사항")
            }
    )
    @GetMapping("/{noticeId}")
    public String notice(@PathVariable("noticeId") Long noticeId, Model model
    ) {
        NoticeDetailsDto noticeDetailsDto = noticeService.getNotice(noticeId);
        model.addAttribute("notice", noticeDetailsDto);
        return "admin/help/notice/notice_details";
    }

    @Operation(
            summary = "공지사항 목록 조회",
            description = "검색 조건에 따라 공지사항 목록을 페이징하여 조회합니다",
            responses = { @ApiResponse(responseCode = "200", description = "성공적으로 공지사항 목록을 반환") }
    )
    @GetMapping
    public String notices(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @ModelAttribute("cond") AdminSearchCondition cond,
            Model model
    ) {
        PageRequest pageRequest = PageRequest.of(page - 1, 10);
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
        model.addAttribute("editNoticeForm", form);
        return "admin/help/notice/edit_notice";
    }

    @Operation(
            summary = "공지사항 수정 요청",
            description = "기존 공지사항을 수정하고 상세 페이지로 리다이렉트합니다.",
            responses = {
                    @ApiResponse(responseCode = "302", description = "수정 성공 후 상세 페이지로 리다이렉트"),
                    @ApiResponse(responseCode = "400", description = "유효성 검사 실패 시 수정 폼으로 다시 이동"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 공지사항")
            }
    )
    @PostMapping("/{noticeId}/edit")
    public String editNotice(
            @Valid @ModelAttribute("editNoticeForm") EditNoticeForm form,
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
