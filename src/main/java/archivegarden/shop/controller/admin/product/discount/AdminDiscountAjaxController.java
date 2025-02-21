package archivegarden.shop.controller.admin.product.discount;

import archivegarden.shop.dto.ResultResponse;
import archivegarden.shop.service.admin.product.AdminDiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ajax/admin")
@RequiredArgsConstructor
public class AdminDiscountAjaxController {

    private final AdminDiscountService discountService;

    /**
     * 할인 한개 삭제 요청을 처리하는 메서드
     */
    @DeleteMapping("/discount")
    public ResultResponse deleteDiscount(@RequestParam("discountId") Long discountId) {
        discountService.deleteDiscount(discountId);
        return new ResultResponse(HttpStatus.OK.value(), "삭제가 완료되었습니다.");
    }

    /**
     * 할인 여러개 삭제 요청을 처리하는 메서드
     */
    @DeleteMapping("/discounts")
    public ResultResponse deleteDiscounts(@RequestBody List<Long> discountIds) {
        discountService.deleteDiscounts(discountIds);
        return new ResultResponse(HttpStatus.OK.value(), "삭제가 완료되었습니다.");
    }
}
