package archivegarden.shop.controller;

import archivegarden.shop.entity.Member;
import archivegarden.shop.service.product.WishService;
import archivegarden.shop.web.annotation.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wish")
@RequiredArgsConstructor
public class WishController {

    private final WishService wishService;

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/add")
    public void addWish(@RequestParam("productId") Long productId, @CurrentUser Member loginMember) {
        wishService.add(productId, loginMember.getId());
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/remove")
    public void removeWish(@RequestParam("productId") Long productId, @CurrentUser Member loginMember) {
        wishService.remove(productId, loginMember.getId());
    }
}
