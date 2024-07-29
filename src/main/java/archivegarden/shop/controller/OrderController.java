package archivegarden.shop.controller;

import archivegarden.shop.dto.delivery.DeliveryDto;
import archivegarden.shop.dto.order.MemberDto;
import archivegarden.shop.dto.order.OrderProductListDto;
import archivegarden.shop.entity.Member;
import archivegarden.shop.service.mypage.DeliveryService;
import archivegarden.shop.service.order.OrderService;
import archivegarden.shop.service.point.SavedPointService;
import archivegarden.shop.service.product.ProductService;
import archivegarden.shop.web.annotation.CurrentUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final ProductService productService;
    private final OrderService orderService;
    private final DeliveryService deliveryService;
    private final SavedPointService savedPointService;

    //주문페이지 요청
    @GetMapping("/checkout")
    public String checkout(HttpServletRequest request, @CurrentUser Member loginMember, Model model) {

        //로그인 된 회원 정보
        MemberDto member = new MemberDto(loginMember);

        //주문 상품 목록
        HttpSession session = request.getSession(false);
        if(session == null || session.getAttribute("checkoutProducts") == null) {
            return "redirect:/cart";
        }
        List<Long> productIds = (List<Long>) session.getAttribute("checkoutProducts");
        session.removeAttribute("checkoutProducts");
        List<OrderProductListDto> products = productService.getOrderProducts(loginMember, productIds);

        //기본 배송지 주소
        DeliveryDto defaultDelivery = deliveryService.getDefaultDelivery(loginMember.getId());

        //주문번호 매번 유니크하게 생성
        long nano = System.currentTimeMillis();
        String paymentId = "pid-" + nano;

        //상품 주문 정보 생성
        Long orderId = orderService.createOrder(paymentId, productIds, loginMember.getId());

        //적립금 조회
        int point = savedPointService.getPoint(loginMember.getId());

        //스토어 아이디
        model.addAttribute("storeId", "store-3a9093ef-3510-4e55-b3ac-6fb1d76550ba");

        //PG사 채널키
        model.addAttribute("channelKey", "channel-key-def17012-580c-4cf0-a4e6-9a083924d4d9");

        //상품주문번호
        model.addAttribute("paymentId", paymentId);
        model.addAttribute("orderId", orderId);

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
