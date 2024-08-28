package archivegarden.shop.controller.admin;

import archivegarden.shop.dto.ResultResponse;
import archivegarden.shop.dto.admin.product.answer.AnswerResponseDto;
import archivegarden.shop.dto.admin.product.answer.EditAnswerRequestDto;
import archivegarden.shop.entity.Admin;
import archivegarden.shop.service.admin.product.AdminDiscountService;
import archivegarden.shop.service.admin.product.AdminProductInquiryAnswerService;
import archivegarden.shop.service.admin.product.AdminProductInquiryService;
import archivegarden.shop.web.annotation.CurrentAdmin;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ajax/admin")
@RequiredArgsConstructor
public class AjaxAdminController {

    private final AdminDiscountService discountService;
    private final AdminProductInquiryService inquiryService;
    private final AdminProductInquiryAnswerService answerService;

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
