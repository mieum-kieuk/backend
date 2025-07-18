package archivegarden.shop.controller.user.mypage;

import archivegarden.shop.dto.user.wish.MyWishDto;
import archivegarden.shop.entity.Member;
import archivegarden.shop.service.product.WishService;
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

@Tag(name = "위시리스트", description = "사용자 페이지에서 마이페이지 위시리스트 관련 API")
@Controller
@RequestMapping("/mypage/wish")
@RequiredArgsConstructor
public class MyWishController {

    private final WishService wishService;

    /**
     * 위시 상품 목록을 조회하는 요청을 처리하는 메서드
     */
    @Operation(
            summary = "내 위시리스트 목록 조회",
            description = "사용자가 찜한 위시리스트 목록을 조회합니다."
    )
    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public String wishlist(@CurrentUser Member loginMember, Model model
    ) {
        List<MyWishDto> wishlist = wishService.getWishList(loginMember.getId());
        model.addAttribute("products", wishlist);
        return "user/mypage/wish/wish_list";
    }
}
