package archivegarden.shop.controller.user.community.inquiry;

import archivegarden.shop.dto.user.community.inquiry.AddInquiryForm;
import archivegarden.shop.dto.user.community.inquiry.EditInquiryForm;
import archivegarden.shop.dto.user.community.inquiry.InquiryDetailsDto;
import archivegarden.shop.dto.user.community.inquiry.InquiryListDto;
import archivegarden.shop.entity.Member;
import archivegarden.shop.service.user.community.InquiryService;
import archivegarden.shop.web.annotation.CurrentUser;
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

@Controller
@RequestMapping("/community/inquiry")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    /**
     * 상품 문의 등록 폼을 반환하는 메서드
     */
    @GetMapping("/add")
    public String addInquiryForm(@ModelAttribute("form") AddInquiryForm form) {
        return "user/community/inquiry/add_inquiry";
    }

    /**
     * 상품 문의 등록 요청을 처리하는 메서드
     */
    @PostMapping("/add")
    public String addInquiry(@Valid @ModelAttribute("form") AddInquiryForm form, BindingResult bindingResult,
                             @CurrentUser Member loginMember, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "user/community/inquiry/add_inquiry";
        }

        Long inquiryId = inquiryService.saveInquiry(form, loginMember);
        redirectAttributes.addAttribute("inquiryId", inquiryId);
        return "redirect:/community/inquiry/{inquiryId}";
    }

    /**
     * 상품 문의 상세 페이지 조회하는 요청을 처리하는 메서드
     */
    @GetMapping("/{inquiryId}")
    public String inquiry(@PathVariable("inquiryId") Long inquiryId, Model model) {
        InquiryDetailsDto inquiryDetailsDto = inquiryService.getInquiry(inquiryId);
        model.addAttribute("inquiry", inquiryDetailsDto);
        return "user/community/inquiry/inquiry_details";
    }

    /**
     * 상품 문의 목록을 조회하는 요청을 처리하는 메서드
     */
    @GetMapping
    public String inquires(@RequestParam(name = "page", defaultValue = "1") int page, Model model) {
        PageRequest pageRequest = PageRequest.of(page - 1, 10);
        Page<InquiryListDto> inquiryDtos = inquiryService.getInquires(pageRequest);
        model.addAttribute("inquiries", inquiryDtos);
        return "user/community/inquiry/inquiry_list";
    }

    /**
     * 상품 문의 수정 폼을 반환하는 메서드
     */
    @GetMapping("/{inquiryId}/edit")
    @PreAuthorize("hasRole('ROLE_USER') and #loginMember.loginId == principal.username")
    public String editInquiryForm(@PathVariable("inquiryId") Long inquiryId, @CurrentUser Member loginMember, Model model) {
        EditInquiryForm form = inquiryService.getInquiryEditForm(inquiryId);
        model.addAttribute("form", form);
        return "user/community/inquiry/edit_inquiry";
    }

    /**
     * 상품 문의 수정 요청을 처리하는 메서드
     */
    @PostMapping("/{inquiryId}/edit")
    @PreAuthorize("hasRole('ROLE_USER') and #loginMember.loginId == principal.username")
    public String editInquiry(@Valid @ModelAttribute("form") EditInquiryForm form, BindingResult bindingResult,
                              @PathVariable("inquiryId") Long inquiryId, @CurrentUser Member loginMember) {
        if(bindingResult.hasErrors()) {
            return "user/community/inquiry/edit_inquiry";
        }

        inquiryService.editInquiry(form, inquiryId);
        return "redirect:/community/inquiry/{inquiryId}";
    }
}
