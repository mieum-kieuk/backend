package archivegarden.shop.controller.admin;

import archivegarden.shop.dto.ResultResponse;
import archivegarden.shop.dto.admin.product.answer.AnswerResponseDto;
import archivegarden.shop.dto.admin.product.answer.EditAnswerRequestDto;
import archivegarden.shop.dto.admin.product.product.ProductImageDto;
import archivegarden.shop.entity.Admin;
import archivegarden.shop.service.admin.admins.AdminAdminService;
import archivegarden.shop.service.admin.help.AdminNoticeService;
import archivegarden.shop.service.admin.product.*;
import archivegarden.shop.web.annotation.CurrentAdmin;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ajax/admin")
@RequiredArgsConstructor
public class AjaxAdminController {

    private final AdminAdminService adminService;
    private final AdminNoticeService noticeService;
    private final AdminDiscountService discountService;
//    private final AdminProductService productService;
    private final AdminProductImageService productImageService;
    private final AdminProductInquiryService inquiryService;
    private final AdminProductInquiryAnswerService answerService;
//    private final AdminFileStore fileStore;

    //전체 관리자 관리페이지에서 관리자 단건 삭제
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/admins/delete")
    public ResultResponse deleteAdmin(@RequestParam("adminId") Long adminId) {
        adminService.deleteAdmin(adminId);
        return new ResultResponse(HttpStatus.OK.value(), "삭제되었습니다.");
    }

    //전체 관리자 관리페이지에서 권한 부여
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/admins/auth")
    public ResultResponse authorizeAdmin(@RequestParam("adminId") Long adminId) {
        adminService.authorizeAdmin(adminId);
        return new ResultResponse(HttpStatus.OK.value(), "승인되었습니다.");
    }

    //공지사항 삭제
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/notice/delete")
    public ResultResponse deleteNotice(@RequestParam("noticeId") Long noticeId) {
        noticeService.deleteNotice(noticeId);
        return new ResultResponse(HttpStatus.OK.value(), "삭제가 완료되었습니다.");
    }

    //상품할인 단건 삭제
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/discount/delete")
    public ResultResponse deleteDiscount(@RequestParam("discountId") Long discountId) {
        discountService.deleteDiscount(discountId);
        return new ResultResponse(HttpStatus.OK.value(), "삭제가 완료되었습니다.");
    }

    //상품할인 여러건 삭제
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/discounts/delete")
    public ResultResponse deleteDiscounts(@RequestBody List<Long> discountIds) {
        discountService.deleteDiscounts(discountIds);
        return new ResultResponse(HttpStatus.OK.value(), "삭제가 완료되었습니다.");
    }

//    @ResponseBody
//    @GetMapping("/images/{filename}")
//    public Resource downloadImage(@PathVariable String filename) throws MalformedURLException {
//        return new UrlResource("file:" + fileStore.getFullPath(filename));
//    }

    //상품 수정 폼에서 페이지 로딩시 첨부된 이미지 조회
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/productImages/{productId}")
    public List<ProductImageDto> getProductImages(@PathVariable("productId") Long productId) {
        return productImageService.findProductImages(productId);
    }

    //상품 수정 폼에서 이미지 단건 삭제
    @PostMapping("/productImages/{productImageId}/delete")
    public void deleteImage(@PathVariable("productImageId") Long productImageId) {
        productImageService.deleteImage(productImageId);
    }

    //상품 단건 삭제
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/product/delete")
    public ResultResponse deleteProduct(@RequestParam("productId") Long productId) {
//        productService.deleteProduct(productId);
        return new ResultResponse(HttpStatus.OK.value(), "삭제가 완료되었습니다.");
    }

    //상품 여러건 삭제
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/products/delete")
    public ResultResponse deleteProducts(@RequestBody List<Long> discountIds) {
//        productService.deleteProducts(discountIds);
        return new ResultResponse(HttpStatus.OK.value(), "삭제가 완료되었습니다.");
    }

    //상품 문의 답변 저장
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/product/inquiry/{inquiryId}/add")
    public ResultResponse addAnswer(@RequestBody String content, @PathVariable("inquiryId") Long inquiryId, @CurrentAdmin Admin loginAdmin) {
        answerService.writeAnswer(content, inquiryId, loginAdmin);
        return new ResultResponse(HttpStatus.OK.value(), "답변 작성이 완료되었습니다.");
    }

    //상품 문의 답변 조회
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/product/inquiry/{inquiryId}")
    public AnswerResponseDto readAnswer(@PathVariable("inquiryId") Long inquiryId) {
        AnswerResponseDto answer = answerService.getAnswer(inquiryId);
        return answer;
    }

    //상품 문의 답변 수정
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/product/inquiry/edit")
    public ResultResponse editAnswer(@RequestBody EditAnswerRequestDto editAnswerDto) {
        answerService.editAnswer(editAnswerDto);
        return new ResultResponse(HttpStatus.OK.value(), "답변 수정이 완료되었습니다.");
    }

    //상품 문의 답변 삭제
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/product/inquiry/delete")
    public ResultResponse deleteAnswer(@RequestParam("answerId") Long answerId) {
        answerService.deleteAnswer(answerId);
        return new ResultResponse(HttpStatus.OK.value(), "답변이 삭제되었습니다.");
    }
}
