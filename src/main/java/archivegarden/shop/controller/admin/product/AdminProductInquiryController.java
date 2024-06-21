package archivegarden.shop.controller.admin.product;

import archivegarden.shop.dto.admin.AdminSearchForm;
import archivegarden.shop.dto.admin.product.inquiry.ProductInquiryAdminDetailsDto;
import archivegarden.shop.dto.admin.product.inquiry.ProductInquiryAdminListDto;
import archivegarden.shop.service.admin.product.AdminProductInquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/product/inquiry")
@RequiredArgsConstructor
public class AdminProductInquiryController {

    private final AdminProductInquiryService inquiryService;

    //상품 문의 단건 조회
    @GetMapping("/{inquiryId}")
    public String inquiry(@PathVariable("inquiryId") Long inquiryId, Model model) {
        ProductInquiryAdminDetailsDto inquiryDetailsDto = inquiryService.getInquiry(inquiryId);
        model.addAttribute("inquiry", inquiryDetailsDto);
        return "admin/product/inquiry/inquiry_details";
    }

    //상품 문의 여러건 조회
    @GetMapping
    public String inquiries(@ModelAttribute("form") AdminSearchForm form, @RequestParam(name="page", defaultValue = "1") int page, Model model) {
        PageRequest pageRequest = PageRequest.of(page - 1, 10);
        Page<ProductInquiryAdminListDto> inquiryListDtos = inquiryService.getInquiries(form, pageRequest);
        model.addAttribute("inquiries", inquiryListDtos);
        return "admin/product/inquiry/inquiry_list";
    }
}
