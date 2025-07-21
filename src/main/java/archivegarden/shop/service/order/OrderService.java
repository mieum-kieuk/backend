package archivegarden.shop.service.order;

import archivegarden.shop.dto.order.OrderProductListDto;
import archivegarden.shop.entity.*;
import archivegarden.shop.exception.global.EntityNotFoundException;
import archivegarden.shop.repository.cart.CartRepository;
import archivegarden.shop.repository.member.MemberRepository;
import archivegarden.shop.repository.order.OrderRepository;
import archivegarden.shop.repository.product.ProductRepository;
import archivegarden.shop.service.user.product.product.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final ProductImageService productImageService;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;

    /**
     * 주문 생성
     *
     * @throws EntityNotFoundException
     */
    public Long createOrder(String merchantUid, List<Long> productIds, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));
        List<Product> products = productIds.stream()
                .map(productId -> productRepository.findById(productId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 상품입니다.")))
                .collect(Collectors.toList());

        //OrderProduct 생성
        List<OrderProduct> orderProducts = new ArrayList<>();
        for (Product product : products) {
            Cart cart = cartRepository.findByMemberAndProduct(member, product);
            OrderProduct orderProduct = OrderProduct.createOrderProduct(cart.getCount(), product);
            orderProducts.add(orderProduct);
        }

        //주문 결제 금액 계산
        int amount = 0;
        for (OrderProduct orderProduct : orderProducts) {
            Product product = orderProduct.getProduct();
            int price = product.getPrice();
            Discount discount = product.getDiscount();
            if(discount != null) {
                double salePrice = price - (double) price * discount.getDiscountPercent() / 100;
                amount += Math.round(salePrice * orderProduct.getQuantity());
            } else {
                amount += price;
            }
        }

        if(amount < 50000) {
            amount += 3000;
        }

        //주문 생성
        Order order = Order.builder()
                .member(member)
                .merchantUid(merchantUid)
                .amount(amount)
                .orderStatus(OrderStatus.TRY)   //주문시도
                .orderProducts(orderProducts)
                .build();

        //주문 상품, 주문 저장
        orderRepository.save(order);

        return order.getId();
    }

    /**
     * 주문 상품 목록 조회
     *
     * @throws EntityNotFoundException
     */
    public List<OrderProductListDto> getOrderProducts(Long orderId) {
        List<OrderProductListDto> orderProductListDtos = orderRepository.findOrderProducts(orderId);
        orderProductListDtos.forEach(o -> {
//            String encodedImageData = productImageService.getEncodedImageData(o.getDisplayImageData());
//            o.setDisplayImageData(encodedImageData);
        });
        return orderProductListDtos;
    }
}
