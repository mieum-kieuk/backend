package archivegarden.shop.controller.admin.product.inquiry;

import archivegarden.shop.dto.admin.product.inquiry.AdminInquiryDetailsDto;
import archivegarden.shop.dto.admin.product.inquiry.AdminInquiryListDto;
import archivegarden.shop.dto.admin.product.product.AdminProductSearchCondition;
import archivegarden.shop.service.admin.product.AdminInquiryService;
import archivegarden.shop.util.PageRequestUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Tag(name = "상품 문의", description = "관리자 페이지에서 상품 문의 관련 API")
@Controller
@RequestMapping("/admin/inquiries")
@RequiredArgsConstructor
public class AdminInquiryController {

    private final AdminInquiryService inquiryService;

    @Operation(
            summary = "상품 문의 상세 조회",
            description = "상품 문의 상세 정보를 조회합니다."
    )
    @GetMapping("/{inquiryId}")
    public String inquiry(@PathVariable("inquiryId") Long inquiryId, Model model) {
        AdminInquiryDetailsDto inquiryDetailsDto = inquiryService.getInquiry(inquiryId);
        model.addAttribute("inquiry", inquiryDetailsDto);
        return "admin/product/inquiry/inquiry_details";
    }

    @Operation(
            summary = "상품 문의 목록 조회",
            description = "검색 조건에 따라 상품 문의 목록을 페이징하여 조회합니다"
    )
    @GetMapping
    public String inquires(
            @ModelAttribute("cond") AdminProductSearchCondition condition,
            @RequestParam(name = "page", defaultValue = "1") int page,
            Model model
    ) {
        PageRequest pageRequest = PageRequestUtil.of(page);
        Page<AdminInquiryListDto> inquiryListDtos = inquiryService.getInquiries(condition, pageRequest);
        model.addAttribute("inquiries", inquiryListDtos);
        return "admin/product/inquiry/inquiry_list";
    }
}
