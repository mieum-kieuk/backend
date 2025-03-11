package archivegarden.shop.controller.user.community.inquiry;

import archivegarden.shop.dto.ResultResponse;
import archivegarden.shop.service.user.community.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ajax/inquiry")
@RequiredArgsConstructor
public class InquiryAjaxController {

    private final InquiryService inquiryService;

    /**
     * 상품 문의 삭제 요청을 처리하는 메서드
     */
    @DeleteMapping
    public ResultResponse deleteInquiry(@RequestParam("inquiryId") Long inquiryId) {
        inquiryService.deleteInquiry(inquiryId);
        return new ResultResponse(HttpStatus.OK.value(), "삭제가 완료되었습니다.");
    }
}
