package archivegarden.shop.controller;

import archivegarden.shop.dto.ResultResponse;
import archivegarden.shop.entity.Member;
import archivegarden.shop.service.mypage.DeliveryService;
import archivegarden.shop.service.order.CartService;
import archivegarden.shop.web.annotation.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ajax")
@RequiredArgsConstructor
public class AjaxController {

    private final DeliveryService deliveryService;
    private final CartService cartService;

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/delivery/delete")
    public ResultResponse deleteDelivery(@RequestParam("deliveryId") Long deliveryId) {
        deliveryService.deleteDelivery(deliveryId);
        return new ResultResponse(HttpStatus.OK.value(), "삭제가 완료되었습니다.");
    }

    //상품 상세페이지에서 상품 장바구니에 담기
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/cart/add")
    public ResultResponse addCart(@CurrentUser Member loginMember, @RequestParam(name = "productId") Long productId,
                          @RequestParam(name = "count") int count) {
        return cartService.addCart(count, loginMember.getId(), productId);
    }

}
