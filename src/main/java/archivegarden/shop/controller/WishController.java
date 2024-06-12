package archivegarden.shop.controller;

import archivegarden.shop.dto.mypage.MyWishDto;
import archivegarden.shop.entity.Member;
import archivegarden.shop.service.product.WishService;
import archivegarden.shop.web.annotation.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class WishController {

    private final WishService wishService;

    @GetMapping("/mypage/wish")
    @PreAuthorize("hasRole('ROLE_USER') and #loginMember.loginId == principal.username")
    public String wishlist(@RequestParam(name = "page", defaultValue = "1") int page, @CurrentUser Member loginMember, Model model) {
        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.Direction.ASC, "id");
        Page<MyWishDto> wishlist = wishService.getWishList(loginMember.getId(), pageRequest);
        model.addAttribute("products", wishlist);
        return "mypage/wish_list";
    }

    @GetMapping("/mypage/wish/{wishId}/delete")
    public String deleteWish(@PathVariable("wishId") Long wishId) {
        wishService.delete(wishId);
        return "redirect:/mypage/wish";
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("#loginMember.loginId == principal.username")
    @ResponseBody
    @PostMapping("/api/wish/add")
    public void addWish(@RequestParam("productId") Long productId, @CurrentUser Member loginMember) {
        wishService.add(productId, loginMember.getId());
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("#loginMember.loginId == principal.username")
    @ResponseBody
    @PostMapping("/api/wish/remove")
    public void removeWish(@RequestParam("productId") Long productId, @CurrentUser Member loginMember) {
        wishService.remove(productId, loginMember.getId());
    }
}
