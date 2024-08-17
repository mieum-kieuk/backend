package archivegarden.shop.controller;

import archivegarden.shop.dto.ResultResponse;
import archivegarden.shop.entity.Member;
import archivegarden.shop.service.member.MemberService;
import archivegarden.shop.service.mypage.DeliveryService;
import archivegarden.shop.service.order.CartService;
import archivegarden.shop.web.annotation.CurrentUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@RestController
@RequestMapping("/ajax")
@RequiredArgsConstructor
public class AjaxController {

    private final MemberService memberService;
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

    //카트에서 상품 수량 증가
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/cart/increase")
    @PreAuthorize("#loginMember.loginId == principal.username")
    public ResultResponse increaseCount(@RequestParam("productId") Long productId, @CurrentUser Member loginMember) {
        return cartService.increaseCount(productId, loginMember);
    }

    //카트에서 상품 수량 감소
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/cart/decrease")
    @PreAuthorize("#loginMember.loginId == principal.username")
    public ResultResponse decreaseCount(@RequestParam("productId") Long productId, @CurrentUser Member loginMember) {
        return cartService.decreaseCount(productId, loginMember);
    }

    //카트에 담긴 상품 단건 삭제
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("#loginMember.loginId == principal.username")
    @DeleteMapping("/cart/{productId}/delete")
    public ResultResponse deleteCart(@PathVariable("productId") Long productId, @CurrentUser Member loginMember) {
        cartService.deleteCart(productId, loginMember);
        return new ResultResponse(HttpStatus.OK.value(), "상품이 삭제되었습니다.");
    }

    //카트에 담긴 상품 여러건 삭제
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("#loginMember.loginId == principal.username")
    @DeleteMapping("/cart/delete")
    public ResultResponse deleteCarts(@RequestBody List<Long> productIds, @CurrentUser Member loginMember) {
        cartService.deleteCarts(productIds, loginMember);
        return new ResultResponse(HttpStatus.OK.value(), "상품들이 삭제되었습니다.");
    }

    //카트 -> 주문서
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/checkout")
    @PreAuthorize("#loginMember.loginId == principal.username")
    public ResultResponse checkout(@RequestBody List<Long> productIds, @CurrentUser Member loginMember, HttpServletRequest request) {
        cartService.validateStockQuantity(productIds, loginMember);

        //구매할 상품 목록 세션에 저장
        HttpSession session = request.getSession();
        session.setAttribute("checkoutProducts", productIds);
        return new ResultResponse(HttpStatus.OK.value(), "주문서로 이동합니다.");
    }

    //마이페이지: 개인 정보 수정 전 본인 확인
    @PostMapping("/mypage/validate/member")
    @PreAuthorize("#loginMember.loginId == principal.username")
    public ResultResponse validateMember(@RequestParam("password") String password,  @CurrentUser Member loginMember) {
        boolean isCurrentMember = memberService.mypageInfoLogin(loginMember, password);
        if(isCurrentMember) {
            return new ResultResponse(HttpStatus.OK.value(), "본인 확인이 완료되었습니다.");
        } else {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), "비밀번호가 일치하지 않습니다.");
        }
    }
}
