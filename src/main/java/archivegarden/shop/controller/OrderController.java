package archivegarden.shop.controller;

import archivegarden.shop.dto.order.CartCheckoutListDto;
import archivegarden.shop.dto.order.ShippingAddressDto;
import archivegarden.shop.entity.Member;
import archivegarden.shop.entity.Order;
import archivegarden.shop.service.mypage.ShippingAddressService;
import archivegarden.shop.service.order.CartService;
import archivegarden.shop.service.order.OrderService;
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
    private final ShippingAddressService shippingAddressService;

    @GetMapping("/checkout")
    public String checkout(@ModelAttribute("productIds") List<Long> productIds, @CurrentUser Member loginMember, Model model) {

        //주문하려는 상품
        List<CartCheckoutListDto> products = cartService.getCheckoutProducts(loginMember, productIds);
//
//        //재고 정보 확인
//        for (CartCheckoutListDto product : products) {
//            if(product.getCount() > product.getStockQuantity()) {
//                log.info("{}의 재고가 부족합니다.", product.getName());
//                return "redirect:/cart";
//            }
//        }

        //기본 배송지 주소
        ShippingAddressDto defaultShippingAddress = shippingAddressService.getDefaultShippingAddress(loginMember.getId());

        //상품 주문 정보 생성
        orderService.createOrder(productIds, loginMember.getId());

        //스토어 아이디
        model.addAttribute("storeId", "store-3a9093ef-3510-4e55-b3ac-6fb1d76550ba");

        //PG사 채널키
        model.addAttribute("channelKey", "channel-key-def17012-580c-4cf0-a4e6-9a083924d4d9");

        //회원, 상품, 주문 정보
        model.addAttribute("address", defaultShippingAddress);
        model.addAttribute("products", products);

        return "order/checkout";
    }
}
