package archivegarden.shop.controller.user.cart;

import archivegarden.shop.dto.user.order.CartListDto;
import archivegarden.shop.entity.Member;
import archivegarden.shop.service.order.CartService;
import archivegarden.shop.web.annotation.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Tag(name = "장바구니", description = "사용자 페이지에서 장바구니 관련 API")
@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @Operation(
            summary = "장바구니 조회",
            description = "장바구니를 조회합니다."
    )
    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public String cart(@CurrentUser Member loginMember, Model model) {
        List<CartListDto> products = cartService.getCarts(loginMember.getId());
        model.addAttribute("products", products);
        return "user/order/cart";
    }
}
