package archivegarden.shop.controller.user.wish;

import archivegarden.shop.entity.Member;
import archivegarden.shop.service.product.WishService;
import archivegarden.shop.web.annotation.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ajax/wish")
@RequiredArgsConstructor
public class WishAjaxController {

    private final WishService wishService;

    /**
     * 위시리스트에 상품을 추가하는 요청을 처리하는 메서드
     */
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("#loginMember.loginId == principal.username")
    @PostMapping("/add")
    public void addWish(@RequestParam("productId") Long productId, @CurrentUser Member loginMember) {
        wishService.add(productId, loginMember.getId());
    }

    /**
     * 위시리스트에 상품을 제거하는 요청을 처리하는 메서드
     */
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("#loginMember.loginId == principal.username")
    @PostMapping("/remove")
    public void removeWish(@RequestParam("productId") Long productId, @CurrentUser Member loginMember) {
        wishService.remove(productId, loginMember.getId());
    }
}
