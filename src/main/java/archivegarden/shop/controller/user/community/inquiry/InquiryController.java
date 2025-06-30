package archivegarden.shop.controller.user.community.inquiry;

import archivegarden.shop.dto.user.community.inquiry.AddInquiryForm;
import archivegarden.shop.dto.user.community.inquiry.EditInquiryForm;
import archivegarden.shop.dto.user.community.inquiry.InquiryDetailsDto;
import archivegarden.shop.dto.user.community.inquiry.InquiryListDto;
import archivegarden.shop.entity.Member;
import archivegarden.shop.service.user.community.InquiryService;
import archivegarden.shop.web.annotation.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.AccessDeniedException;

@Tag(name = "상품 문의", description = "사용자 페이지에서 상품 문의 관련 API")
@Controller
@RequestMapping("/community/inquiries")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    @Operation(
            summary = "상품 문의 등록 폼 표시",
            description = "상품 문의 등록을 위한 화면을 반환합니다."
    )
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/add")
    public String addInquiryForm(@ModelAttribute("addForm") AddInquiryForm form) {
        return "user/community/inquiry/add_inquiry";
    }

    @Operation(
            summary = "상품 문의 등록 요청",
            description = "상품 문의를 등록하고 상세 페이지로 리다이렉트합니다."
    )
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/add")
    public String addInquiry(
            @Valid @ModelAttribute("addForm") AddInquiryForm form,
            BindingResult bindingResult,
            @CurrentUser Member loginMember,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "user/community/inquiry/add_inquiry";
        }

        Long inquiryId = inquiryService.addInquiry(form, loginMember);
        redirectAttributes.addAttribute("inquiryId", inquiryId);
        return "redirect:/community/inquiries/{inquiryId}";
    }

    @Operation(
            summary = "상세 문의 상세 조회",
            description = "상품 문의 상세 정보를 조회합니다."
    )
    @GetMapping("/{inquiryId}")
    public String inquiry(@PathVariable("inquiryId") Long inquiryId, Model model) {
        InquiryDetailsDto inquiryDetailsDto = inquiryService.getInquiry(inquiryId);
        model.addAttribute("inquiry", inquiryDetailsDto);
        return "user/community/inquiry/inquiry_details";
    }

    @Operation(
            summary = "상품 문의 목록 조회",
            description = "상품 목록을 페이징하여 조회합니다"
    )
    @GetMapping
    public String inquires(@RequestParam(name = "page", defaultValue = "1") int page, Model model) {
        PageRequest pageRequest = PageRequest.of(page - 1, 10);
        Page<InquiryListDto> inquiryDtos = inquiryService.getInquires(pageRequest);
        model.addAttribute("inquiries", inquiryDtos);
        return "user/community/inquiry/inquiry_list";
    }

    @Operation(
            summary = "상품 문의 수정 폼 표시",
            description = "기존 상품 문의를 수정하기 위한 화면을 반환합니다."
    )
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{inquiryId}/edit")
    public String editInquiryForm(
            @PathVariable("inquiryId") Long inquiryId,
            @CurrentUser Member loginMember,
            Model model
    ) throws AccessDeniedException
    {
        EditInquiryForm form = inquiryService.getInquiryEditForm(inquiryId, loginMember);
        model.addAttribute("editForm", form);
        return "user/community/inquiry/edit_inquiry";
    }

    @Operation(
            summary = "상품 문의 수정 요청",
            description = "상품 문의를 수정하고 상세 페이지로 리다이렉트합니다."
    )
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{inquiryId}/edit")
    public String editInquiry(
            @PathVariable("inquiryId") Long inquiryId,
            @Valid @ModelAttribute("editForm") EditInquiryForm form,
            BindingResult bindingResult,
            @CurrentUser Member loginMember
    ) throws AccessDeniedException {
        if(bindingResult.hasErrors()) {
            return "user/community/inquiry/edit_inquiry";
        }

        inquiryService.editInquiry(inquiryId, form, loginMember);
        return "redirect:/community/inquiries/{inquiryId}";
    }
}
