package archivegarden.shop.controller.user.cart;

import archivegarden.shop.dto.ResultResponse;
import archivegarden.shop.entity.Member;
import archivegarden.shop.service.order.CartService;
import archivegarden.shop.web.annotation.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ajax/cart")
@RequiredArgsConstructor
public class CartAjaxController {

    private final CartService cartService;

    /**
     * 상품 상세페이지에서 상품을 장바구니에 담는 요청을 처리하는 메서드
     */
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/add")
    public ResultResponse addCart(@RequestParam(name = "productId") Long productId, @RequestParam(name = "count") int count, @CurrentUser Member loginMember) {
        return cartService.addCart(count, loginMember.getId(), productId);
    }

    /**
     * 카트에서 상품 1개 수량 증가 요청을 처리하는 메서드
     */
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/increase")
    @PreAuthorize("#loginMember.loginId == principal.username")
    public ResultResponse increaseCount(@RequestParam("productId") Long productId, @CurrentUser Member loginMember) {
        return cartService.increaseCount(productId, loginMember);
    }

    /**
     * 카트에서 상품 1개 수량 감소 요청을 처리하는 메서드
     */
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/decrease")
    @PreAuthorize("#loginMember.loginId == principal.username")
    public ResultResponse decreaseCount(@RequestParam("productId") Long productId, @CurrentUser Member loginMember) {
        return cartService.decreaseCount(productId, loginMember);
    }

    /**
     * 카트에 담긴 상품 삭제 요청을 처리하는 메서드
     */
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("#loginMember.loginId == principal.username")
    @DeleteMapping
    public ResultResponse deleteCarts(@RequestBody List<Long> productIds, @CurrentUser Member loginMember) {
        cartService.deleteCarts(productIds, loginMember);
        return new ResultResponse(HttpStatus.OK.value(), "상품들이 삭제되었습니다.");
    }
}
