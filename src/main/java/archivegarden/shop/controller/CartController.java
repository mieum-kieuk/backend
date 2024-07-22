package archivegarden.shop.controller;

import archivegarden.shop.dto.order.CartListDto;
import archivegarden.shop.entity.Member;
import archivegarden.shop.service.order.CartService;
import archivegarden.shop.web.annotation.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/cart")
    @PreAuthorize("hasRole('ROLE_USER') and #loginMember.loginId == principal.username")
    public String cart(@CurrentUser Member loginMember, Model model) {
        List<CartListDto> products = cartService.getCart(loginMember);
        model.addAttribute("products", products);
        return "order/cart";
    }

    @GetMapping("/cart/checkout")
    @PreAuthorize("hasRole('ROLE_USER') and #loginMember.loginId == principal.username")
    public String checkout(@RequestParam List<Long> productIds, @CurrentUser Member loginMember, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("productIds", productIds);
        return "redirect:/order/checkout";
    }


    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("#loginMember.loginId == principal.username")
    @GetMapping("/api/cart/validate")
    public void validateCheckout(@RequestParam("productIds") List<Long> productIds, @CurrentUser Member loginMember) {
        cartService.validateStockQuantity(productIds, loginMember);
    }
}
