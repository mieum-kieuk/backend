package archivegarden.shop.controller.admin.product.inquiry;

import archivegarden.shop.dto.ResultResponse;
import archivegarden.shop.dto.admin.product.answer.AdminAnswerDto;
import archivegarden.shop.entity.Admin;
import archivegarden.shop.service.admin.product.AdminInquiryService;
import archivegarden.shop.web.annotation.CurrentAdmin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@Tag(name = "상품 문의", description = "관리자 페이지에서 상품 문의 관련 AJAX API")
@RestController
@RequestMapping("/ajax/admin/inquiries")
@RequiredArgsConstructor
public class AdminInquiryAjaxController {

    private final AdminInquiryService inquiryService;

    @Operation(
            summary = "상품 문의 답변 등록",
            description = "상품 문의 답변을 작성합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "답변 등록"),
                    @ApiResponse(responseCode = "400", description = "유효성 검증 오류"),
            }
    )
    @PostMapping("/{inquiryId}/answer/add")
    public ResultResponse addAnswer(
            @PathVariable("inquiryId") Long inquiryId,
            @RequestBody String answerContent,
            @CurrentAdmin Admin loginAdmin
    ) {
        if (!StringUtils.hasText(answerContent)) {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), "답변 내용이 비어 있을 수 없습니다.");
        }

        inquiryService.addAnswer(inquiryId, answerContent, loginAdmin);
        return new ResultResponse(HttpStatus.OK.value(), "답변이 등록되었습니다.");
    }

    @Operation(
            summary = "상품 문의 답변 조회",
            description = "상품 문의에 등록된 답변을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "답변 조회"),
                    @ApiResponse(responseCode = "404", description = "답변이 존재하지 않음"),
            }
    )
    @GetMapping("/{inquiryId}/answer")
    public AdminAnswerDto answer(@PathVariable("inquiryId") Long inquiryId) {
        return inquiryService.getAnswer(inquiryId);
    }

    @Operation(
            summary = "상품 문의 답변 수정",
            description = "상품 문의에 등록된 답변을 수정합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "답변 수정"),
                    @ApiResponse(responseCode = "400", description = "유효성 검증 오류"),
                    @ApiResponse(responseCode = "404", description = "답변이 존재하지 않음"),
            }
    )
    @PostMapping("/{inquiryId}/answer/edit")
    public ResultResponse updateAnswer(@PathVariable("inquiryId") Long inquiryId, @RequestBody String answerContent) {
        if (!StringUtils.hasText(answerContent)) {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), "답변 내용이 비어 있을 수 없습니다.");
        }

        inquiryService.updateAnswer(inquiryId, answerContent);
        return new ResultResponse(HttpStatus.OK.value(), "답변이 수정되었습니다.");
    }

    @Operation(
            summary = "상품 문의 답변 삭제",
            description = "상품 문의에 등록된 답변을 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "답변 삭제"),
                    @ApiResponse(responseCode = "404", description = "답변이 존재하지 않음"),
            }
    )
    @DeleteMapping("/{inquiryId}/answer")
    public ResultResponse deleteAnswer(@PathVariable("inquiryId") Long inquiryId) {
        inquiryService.deleteAnswer(inquiryId);
        return new ResultResponse(HttpStatus.OK.value(), "답변이 삭제되었습니다.");

    }
}
