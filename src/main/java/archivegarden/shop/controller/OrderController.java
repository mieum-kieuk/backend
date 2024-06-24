package archivegarden.shop.controller;

import archivegarden.shop.dto.delivery.DeliveryDto;
import archivegarden.shop.dto.order.CartCheckoutListDto;
import archivegarden.shop.dto.order.MemberDto;
import archivegarden.shop.entity.Member;
import archivegarden.shop.service.mypage.DeliveryService;
import archivegarden.shop.service.order.CartService;
import archivegarden.shop.service.order.OrderService;
import archivegarden.shop.service.point.SavedPointService;
import archivegarden.shop.web.annotation.CurrentUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final CartService cartService;
    private final OrderService orderService;
    private final DeliveryService deliveryService;
    private final SavedPointService savedPointService;

    //주문페이지 요청
    @GetMapping("/checkout")
    public String checkout(@ModelAttribute("productIds") List<Long> productIds, @CurrentUser Member loginMember, Model model) {

        //로그인 된 회원 정보
        MemberDto member = new MemberDto(loginMember);

        //주문하려는 상품들
        List<CartCheckoutListDto> products = cartService.getCheckoutProducts(loginMember, productIds);

        //기본 배송지 주소
        DeliveryDto defaultDelivery = deliveryService.getDefaultDelivery(loginMember.getId());

        //상품 주문 정보 생성
        String merchantUid = orderService.createOrder(productIds, loginMember.getId());

        //적립금 조회
        int point = savedPointService.getPoint(loginMember.getId());

        //스토어 아이디
        model.addAttribute("storeId", "store-3a9093ef-3510-4e55-b3ac-6fb1d76550ba");

        //PG사 채널키
        model.addAttribute("channelKey", "channel-key-def17012-580c-4cf0-a4e6-9a083924d4d9");

        //상품주문번호
        model.addAttribute("paymentId", merchantUid);

        //회원, 배송지, 적립금, 주문상품 정보
        model.addAttribute("member", member);
        model.addAttribute("delivery", defaultDelivery);
        model.addAttribute("point", point);
        model.addAttribute("products", products);

        return "order/checkout";
    }

    @GetMapping("/complete")
    public String orderComplete() {
        return "order/checkout_complete";
    }
}
