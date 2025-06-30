package archivegarden.shop.controller.user.cart;

import archivegarden.shop.dto.ResultResponse;
import archivegarden.shop.dto.user.cart.CartResultResponse;
import archivegarden.shop.entity.Member;
import archivegarden.shop.service.order.CartService;
import archivegarden.shop.web.annotation.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "장바구니", description = "사용자 페이지에서 장바구니 관련 AJAX API")
@RestController
@RequestMapping("/ajax/cart")
@RequiredArgsConstructor
public class CartAjaxController {

    private final CartService cartService;

    @Operation(
            summary = "상품 장바구니 담기",
            description = "상품 상세페이지에서 상품을 장바구니에 추가합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "장바구니에 상품이 정상적으로 담거나 이미 담긴 상품 수량 증가"),
                    @ApiResponse(responseCode = "400", description = "재고 부족"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 또는 상품")
            }
    )
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/add")
    public CartResultResponse addCart(
            @RequestParam(name = "productId") Long productId,
            @RequestParam(name = "count") int count,
            @CurrentUser Member loginMember,
            HttpSession session
    ) {
        CartResultResponse resultResponse = cartService.addCart(loginMember.getId(), productId, count);

        int cartItemCount = updateCartItemCount(loginMember.getLoginId(), session);
        resultResponse.setCartItemCount(cartItemCount);

        return resultResponse;
    }

    @Operation(
            summary = "장바구니 상품 수량 증가",
            description = "장바구니에 담긴 특정 상품의 수량을 1개 증가시킵니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "상품 수량이 1개 증가됨"),
                    @ApiResponse(responseCode = "400", description = "장바구니에 상품이 존재하지 않거나 재고 부족"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 상품")
            }
    )
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/increase")
    public ResultResponse increaseCount(@RequestParam("productId") Long productId, @CurrentUser Member loginMember) {
        return cartService.increaseCount(productId, loginMember);
    }

    @Operation(
            summary = "장바구니 상품 수량 감소",
            description = "장바구니에 담긴 특정 상품의 수량을 1개 감소시킵니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "상품 수량이 1개 감소됨"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 상품")
            }
    )
    @PostMapping("/decrease")
    public ResultResponse decreaseCount(@RequestParam("productId") Long productId, @CurrentUser Member loginMember) {
        return cartService.decreaseCount(productId, loginMember);
    }

    @Operation(
            summary = "장바구니 상품 삭제",
            description = "장바구니에 담긴 상품을 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공적으로 삭제"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 상품")
            }
    )
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping
    public ResultResponse deleteCarts(@RequestBody List<Long> productIds, @CurrentUser Member loginMember, HttpSession session) {
        cartService.deleteCarts(productIds, loginMember);
        updateCartItemCount(loginMember.getLoginId(), session);
        return new ResultResponse(HttpStatus.OK.value(), "상품들이 삭제되었습니다.");
    }

    /**
     * 카트에 상품을 추가하거나 삭제할 때 메뉴에 표시되는 상품 개수를 업데이트
     *
     * @param loginId 로그인 ID
     * @param session Http 세션
     * @return 장바구니에 담긴 상품 개수
     */
    private int updateCartItemCount(String loginId, HttpSession session) {
        int cartItemCount = cartService.getCartItemCount(loginId);
        session.setAttribute("cartItemCount", cartItemCount);
        return cartItemCount;
    }
}
