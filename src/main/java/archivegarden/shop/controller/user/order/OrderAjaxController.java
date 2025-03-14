package archivegarden.shop.controller.user.order;

import archivegarden.shop.dto.ResultResponse;
import archivegarden.shop.entity.Member;
import archivegarden.shop.service.order.CartService;
import archivegarden.shop.web.annotation.CurrentUser;
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

@RestController
@RequestMapping("/ajax")
@RequiredArgsConstructor
public class OrderAjaxController {

    private final CartService cartService;

    /**
     * 주문서 들어가기 전에 재고 확인하는 메서드
     */
    @PostMapping("/checkout")
    @PreAuthorize("#loginMember.loginId == principal.username")
    public ResultResponse checkout(@RequestBody List<Long> productIds, @CurrentUser Member loginMember, HttpServletRequest request) {
        cartService.validateStockQuantity(productIds, loginMember);

        //구매할 상품 목록 세션에 저장
        HttpSession session = request.getSession();
        session.setAttribute("checkout:products", productIds);
        return new ResultResponse(HttpStatus.OK.value(), "주문서로 이동합니다.");
    }
}
