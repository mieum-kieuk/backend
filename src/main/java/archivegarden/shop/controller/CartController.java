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
    public String checkout(@RequestParam List<Long> productIds, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("productIds", productIds);
        return "redirect:/order/checkout";
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/api/cart/increase")
    @PreAuthorize("hasRole('ROLE_USER') and #loginMember.loginId == principal.username")
    public void increaseCount(@RequestParam("productId") Long productId, @CurrentUser Member loginMember) {
        cartService.increaseCount(productId, loginMember);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/api/cart/decrease")
    @PreAuthorize("hasRole('ROLE_USER') and #loginMember.loginId == principal.username")
    public void decreaseCount(@RequestParam("productId") Long productId, @CurrentUser Member loginMember) {
        cartService.decreaseCount(productId, loginMember);
    }

    @ResponseBody
    @PostMapping("/api/cart/add")
    public String addCart(@CurrentUser Member loginMember,
                          @RequestParam(name = "productId") Long productId,
                          @RequestParam(name = "count") int count) {
        return cartService.addCart(count, loginMember.getId(), productId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("#loginMember.loginId == principal.username")
    @PostMapping("/api/cart/{productId}/delete")
    public void deleteCart(@PathVariable("productId") Long productId, @CurrentUser Member loginMember) {
        cartService.deleteCart(productId, loginMember);
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("#loginMember.loginId == principal.username")
    @PostMapping("/api/cart/delete")
    public void deleteCarts(@RequestBody List<Long> productIds, @CurrentUser Member loginMember) {
        cartService.deleteCarts(productIds, loginMember);
    }
}
