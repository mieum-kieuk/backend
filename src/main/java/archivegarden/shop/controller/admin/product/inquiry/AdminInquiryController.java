package archivegarden.shop.controller.admin.product.inquiry;

import archivegarden.shop.dto.admin.product.inquiry.AdminInquiryDetailsDto;
import archivegarden.shop.dto.admin.product.inquiry.AdminInquiryListDto;
import archivegarden.shop.dto.admin.product.product.AdminProductSearchCondition;
import archivegarden.shop.service.admin.product.AdminInquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/inquiry")
@RequiredArgsConstructor
public class AdminInquiryController {

    private final AdminInquiryService inquiryService;

    /**
     * 상품 문의 상세페이지 조회하는 요청을 처리하는 메서드
     */
    @GetMapping("/{inquiryId}")
    public String inquiry(@PathVariable("inquiryId") Long inquiryId, Model model) {
        AdminInquiryDetailsDto inquiryDetailsDto = inquiryService.getInquiry(inquiryId);
        model.addAttribute("inquiry", inquiryDetailsDto);
        return "admin/product/inquiry/inquiry_details";
    }

    /**
     * 상품 문의 목록을 조회하는 요청을 처리하는 메서드
     */
    @GetMapping
    public String inquires(@ModelAttribute("form") AdminProductSearchCondition condition, @RequestParam(name = "page", defaultValue = "1") int page, Model model) {
        PageRequest pageRequest = PageRequest.of(page - 1, 10);
        Page<AdminInquiryListDto> inquiryListDtos = inquiryService.getInquiries(condition, pageRequest);
        model.addAttribute("inquiries", inquiryListDtos);
        return "admin/product/inquiry/inquiry_list";
    }
}
