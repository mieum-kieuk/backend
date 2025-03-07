package archivegarden.shop.controller.user.wish;

import archivegarden.shop.dto.user.wish.MyWishDto;
import archivegarden.shop.entity.Member;
import archivegarden.shop.service.product.WishService;
import archivegarden.shop.web.annotation.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class WishController {

    private final WishService wishService;

    /**
     * 위시 상품 목록을 조회하는 요청을 처리하는 메서드
     */
    @GetMapping("/mypage/wish")
    @PreAuthorize("hasRole('ROLE_USER') and #loginMember.loginId == principal.username")
    public String wishlist(@RequestParam(name = "page", defaultValue = "1") int page, @CurrentUser Member loginMember, Model model) {
        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.Direction.DESC, "createdAt");
        Page<MyWishDto> wishlist = wishService.getWishList(loginMember.getId(), pageRequest);
        model.addAttribute("products", wishlist);
        return "user/mypage/wish_list";
    }
}
