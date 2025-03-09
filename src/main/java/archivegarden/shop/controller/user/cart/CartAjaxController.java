package archivegarden.shop.controller.user.cart;

import archivegarden.shop.dto.ResultResponse;
import archivegarden.shop.dto.user.cart.CartResultResponse;
import archivegarden.shop.entity.Member;
import archivegarden.shop.service.order.CartService;
import archivegarden.shop.web.annotation.CurrentUser;
import jakarta.servlet.http.HttpSession;
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
    @PostMapping("/add")
    public CartResultResponse addCart(@RequestParam(name = "productId") Long productId, @RequestParam(name = "count") int count,
                                  @CurrentUser Member loginMember, HttpSession session) {
        CartResultResponse resultResponse = cartService.addCart(count, loginMember.getId(), productId);

        int cartItemCount = updateCartItemCount(loginMember.getLoginId(), session);
        resultResponse.setCartItemCount(cartItemCount);

        return resultResponse;
    }

    /**
     * 카트에서 상품 1개 수량 증가 요청을 처리하는 메서드
     */
    @PostMapping("/increase")
    @PreAuthorize("#loginMember.loginId == principal.username")
    public ResultResponse increaseCount(@RequestParam("productId") Long productId, @CurrentUser Member loginMember) {
        return cartService.increaseCount(productId, loginMember);
    }

    /**
     * 카트에서 상품 1개 수량 감소 요청을 처리하는 메서드
     */
    @PostMapping("/decrease")
    @PreAuthorize("#loginMember.loginId == principal.username")
    public ResultResponse decreaseCount(@RequestParam("productId") Long productId, @CurrentUser Member loginMember) {
        return cartService.decreaseCount(productId, loginMember);
    }

    /**
     * 카트에 담긴 상품 삭제 요청을 처리하는 메서드
     */
    @PreAuthorize("#loginMember.loginId == principal.username")
    @DeleteMapping
    public ResultResponse deleteCarts(@RequestBody List<Long> productIds, @CurrentUser Member loginMember, HttpSession session) {
        cartService.deleteCarts(productIds, loginMember);
        updateCartItemCount(loginMember.getLoginId(), session);
        return new ResultResponse(HttpStatus.OK.value(), "상품들이 삭제되었습니다.");
    }

    /**
     * 카트에 상품을 추가하고 삭제할 때 메뉴에 보이는 상품 개수 업데이트하는 메서드
     */
    private int updateCartItemCount(String loginId, HttpSession session) {
        int cartItemCount = cartService.getCartItemCount(loginId);
        session.setAttribute("cartItemCount", cartItemCount);
        return cartItemCount;
    }
}
