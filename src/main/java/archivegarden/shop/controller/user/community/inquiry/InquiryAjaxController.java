package archivegarden.shop.controller.user.community.inquiry;

import archivegarden.shop.dto.ResultResponse;
import archivegarden.shop.dto.user.community.inquiry.AddInquiryForm;
import archivegarden.shop.dto.user.community.inquiry.EditInquiryForm;
import archivegarden.shop.dto.user.community.inquiry.ProductPageInquiryListDto;
import archivegarden.shop.entity.Member;
import archivegarden.shop.service.user.community.InquiryService;
import archivegarden.shop.web.annotation.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.AccessDeniedException;

@Tag(name = "상품 문의", description = "사용자 페이지에서 상품 문의 관련 AJAX API")
@RestController
@RequestMapping("/ajax/inquiries")
@RequiredArgsConstructor
public class InquiryAjaxController {

    private final InquiryService inquiryService;

    @Operation(
            summary = "상품 상세 페이지에서 상품 문의 등록",
            description = "상품 상세 페이지에서 상품 문의를 등록합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "상품 문의 등록"),
                    @ApiResponse(responseCode = "400", description = "유효성 검증 오류"),
            }
    )
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/add")
    public ResultResponse addInquiry(
            @Valid @ModelAttribute("addForm") AddInquiryForm form,
            BindingResult bindingResult,
            @CurrentUser Member loginMember,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            new ResultResponse(HttpStatus.BAD_REQUEST.value(), "상품 문의 등록 중 오류가 발생했습니다.");
        }

        Long inquiryId = inquiryService.addInquiry(form, loginMember);
        redirectAttributes.addAttribute("inquiryId", inquiryId);
        return new ResultResponse(HttpStatus.OK.value(), "상품 문의가 등록되었습니다.");
    }

    @Operation(
            summary = "상품 상세페이지에서 상품 문의 목록 조회",
            description = "상품 상세페이지에서 상품 문의 목록을 조회합니다.",
            responses = {@ApiResponse(responseCode = "200", description = "상품 문의 목록 조회 성공")}
    )
    @GetMapping("/{productId}")
    public Page<ProductPageInquiryListDto> inquiries(
            @PathVariable("productId") Long productId,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @CurrentUser Member loginMember
    ) {
        PageRequest pageRequest = PageRequest.of(page - 1, 10);
        return inquiryService.getInquiriesInProduct(productId, pageRequest, loginMember);
    }

    @Operation(
            summary = "상품 상세 페이지에서 상품 문의 수정",
            description = "상품 상세 페이지에서 상품 문의를 수정합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "상품 문의 수정 완료"),
                    @ApiResponse(responseCode = "400", description = "유효성 검증 오류"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 상품 또는 상품 문의")
            }
    )
    @PostMapping("/{inquiryId}/edit")
    public ResultResponse editInquiry(
            @Valid @ModelAttribute("editForm") EditInquiryForm form,
            BindingResult bindingResult,
            @PathVariable("inquiryId") Long inquiryId,
            @CurrentUser Member loginMember
    ) throws AccessDeniedException {
        if (bindingResult.hasErrors()) {
            new ResultResponse(HttpStatus.BAD_REQUEST.value(), "상품 문의 수정 중 오류가 발생했습니다.");
        }

        inquiryService.editInquiry(inquiryId, form, loginMember);
        return new ResultResponse(HttpStatus.OK.value(), "상품 문의가 수정되었습니다.");
    }

    @Operation(
            summary = "상품 문의 삭제",
            description = "상품 문의를 삭제합니다. 상품 문의 ID가 존재하지 않을 경우 오류를 반환합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "상품 문의 삭제 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 상품 문의"),
            }
    )
    @DeleteMapping
    public ResultResponse deleteInquiry(@RequestParam("inquiryId") Long inquiryId) {
        inquiryService.deleteInquiry(inquiryId);
        return new ResultResponse(HttpStatus.OK.value(), "상품 문의가 삭제되었습니다.");
    }
}
