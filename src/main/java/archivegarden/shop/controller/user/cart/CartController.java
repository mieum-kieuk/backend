package archivegarden.shop.controller.user.cart;

import archivegarden.shop.dto.order.CartListDto;
import archivegarden.shop.entity.Member;
import archivegarden.shop.service.order.CartService;
import archivegarden.shop.web.annotation.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /**
     * 카트 목록을 조회하는 요청을 처리하는 메서드
     */
    @GetMapping
    @PreAuthorize("#loginMember.loginId == principal.username")
    public String cart(@CurrentUser Member loginMember, Model model) {
        List<CartListDto> products = cartService.getCarts(loginMember.getId());
        model.addAttribute("products", products);
        return "user/order/cart";
    }
}
