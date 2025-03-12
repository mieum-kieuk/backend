package archivegarden.shop.controller.admin.product.inquiry;

import archivegarden.shop.dto.ResultResponse;
import archivegarden.shop.dto.admin.product.answer.AdminAnswerDto;
import archivegarden.shop.entity.Admin;
import archivegarden.shop.service.admin.product.AdminInquiryService;
import archivegarden.shop.web.annotation.CurrentAdmin;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ajax/admin/inquiries")
@RequiredArgsConstructor
public class AdminInquiryAjaxController {

    private final AdminInquiryService inquiryService;

    /**
     * 상품 문의 답변 등록 요청을 처리하는 메서드
     */
    @PostMapping("/{inquiryId}/answer/add")
    public ResultResponse addAnswer(@PathVariable("inquiryId") Long inquiryId, @RequestBody String answerContent, @CurrentAdmin Admin loginAdmin) {
        if (!StringUtils.hasText(answerContent)) {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), "답변 내용이 비어 있을 수 없습니다.");
        }

        inquiryService.addAnswer(inquiryId, answerContent, loginAdmin);
        return new ResultResponse(HttpStatus.OK.value(), "답변이 등록되었습니다.");
    }

    /**
     * 상품 문의 답변 조회 요청을 처리하는 메서드
     */
    @GetMapping("/{inquiryId}/answer")
    public AdminAnswerDto answer(@PathVariable("inquiryId") Long inquiryId) {
        return inquiryService.getAnswer(inquiryId);
    }

    /**
     * 상품 문의 답변 수정 요청을 처리하는 메서드
     */
    @PostMapping("/{inquiryId}/answer/edit")
    public ResultResponse updateAnswer(@PathVariable("inquiryId") Long inquiryId, @RequestBody String answerContent, @CurrentAdmin Admin loginAdmin) {
        if (!StringUtils.hasText(answerContent)) {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), "답변 내용이 비어 있을 수 없습니다.");
        }

        inquiryService.updateAnswer(inquiryId, answerContent);
        return new ResultResponse(HttpStatus.OK.value(), "답변이 수정되었습니다.");
    }

    /**
     * 상품 문의 답변 삭제 요청을 처리하는 메서드
     */
    @DeleteMapping("/{inquiryId}/answer")
    public ResultResponse deleteAnswer(@PathVariable("inquiryId") Long inquiryId) {
        inquiryService.deleteAnswer(inquiryId);
        return new ResultResponse(HttpStatus.OK.value(), "답변이 삭제되었습니다.");

    }
}
