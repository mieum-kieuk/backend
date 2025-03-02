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
     *  할인명 중복 여부를 검사하는 메서드
     */
    @PostMapping("/discount/check/name")
    public ResultResponse checkLoginIdDuplicate(@RequestParam("name") String name) {
        boolean isNameAvailable = discountService.isNameAvailable(name);
        if(isNameAvailable) {
            return new ResultResponse(HttpStatus.OK.value(), "사용 가능한 할인명입니다.");
        } else {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), "이미 존재하는 할인명입니다.");
        }
    }

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
