package archivegarden.shop.controller.user.order;

import archivegarden.shop.constant.SessionConstants;
import archivegarden.shop.dto.ResultResponse;
import archivegarden.shop.entity.Member;
import archivegarden.shop.service.order.CartService;
import archivegarden.shop.web.annotation.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "주문", description = "사용자 페이지에서 주문 관련 AJAX API")
@RestController
@RequestMapping("/ajax/order")
@RequiredArgsConstructor
public class OrderAjaxController {

    private final CartService cartService;

    @Operation(
            summary = "주문 전 재고 확인",
            description = "사용자가 주문서 페이지로 이동하기 전에 장바구니 내 상품들의 재고를 확인합니다. 검증이 성공하면 세션에 상품 목록을 저장하고 주문서 페이지로 이동합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "재고 검증 성공 - 주문서로 이동"),
                    @ApiResponse(responseCode = "400", description = "재고 부족 또는 존재하지 않는 상품"),
            }
    )
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/validate")
    public ResultResponse validateOrderProducts(
            @RequestBody List<Long> productIds,
            @CurrentUser Member loginMember,
            HttpServletRequest request
    ) {
        cartService.validateStockQuantity(productIds, loginMember);

        //구매할 상품 목록 세션에 저장
        HttpSession session = request.getSession();
        session.setAttribute(SessionConstants.CHECKOUT_PRODUCT_IDS, productIds);
        return new ResultResponse(HttpStatus.OK.value(), "주문서로 이동합니다.");
    }
}
