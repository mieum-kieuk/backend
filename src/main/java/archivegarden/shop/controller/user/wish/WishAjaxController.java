package archivegarden.shop.controller.user.wish;

import archivegarden.shop.dto.ResultResponse;
import archivegarden.shop.entity.Member;
import archivegarden.shop.service.product.WishService;
import archivegarden.shop.web.annotation.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "위시", description = "사용자 페이지에서 위시 관련 AJAX API")
@RestController
@RequestMapping("/ajax/wish")
@RequiredArgsConstructor
public class WishAjaxController {

    private final WishService wishService;

    @Operation(
            summary = "위시리스트에 상품 추가",
            description = " 위시리스트에 상품을 추가합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "위시리스트 추가"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 상품 또는 회원"),
            }
    )
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResultResponse addWish(@RequestParam("productId") Long productId, @CurrentUser Member loginMember) {
        wishService.add(productId, loginMember.getId());
        return new ResultResponse(HttpStatus.OK.value(), "위시리스트에 추가되었습니다.");
    }

    @Operation(
            summary = " 위시리스트에서 상품 삭제",
            description = " 위시리스트에서 상품을 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "위시리스트에서 삭제"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 상품 또는 회원"),
            }
    )
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping
    public ResultResponse removeWish(@RequestParam("productId") Long productId, @CurrentUser Member loginMember) {
        wishService.remove(productId, loginMember.getId());
        return new ResultResponse(HttpStatus.OK.value(), "위시리스트에서 삭제되었습니다.");
    }
}
