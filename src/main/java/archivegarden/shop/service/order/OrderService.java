package archivegarden.shop.service.order;

import archivegarden.shop.entity.*;
import archivegarden.shop.exception.NoSuchMemberException;
import archivegarden.shop.exception.NoSuchProductException;
import archivegarden.shop.repository.member.MemberRepository;
import archivegarden.shop.repository.DeliveryRepository;
import archivegarden.shop.repository.order.CartRepository;
import archivegarden.shop.repository.order.OrderRepository;
import archivegarden.shop.repository.shop.ProductRepository;
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

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final DeliveryRepository deliveryRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;


    /**
     * 주문 생성
     */
    public void createOrder(List<Long> productIds, Long memberId) {
        
        //회원, 상품 조회
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchMemberException("존재하지 않는 회원입니다."));
        Delivery delivery = deliveryRepository.findDefaultDelivery(memberId);
        List<Product> products = productIds.stream()
                .map(productId -> productRepository.findById(productId).orElseThrow(() -> new NoSuchProductException("존재하지 않는 상품입니다.")))
                .collect(Collectors.toList());

        //주문번호 매번 유니크하게 생성
        long nano = System.currentTimeMillis();
        String merchantUid = "pid-" + nano;

        //주문 상품 생성
        List<OrderProduct> orderProducts = new ArrayList<>();
        for (Product product : products) {
            Cart cart = cartRepository.findByMemberAndProduct(member, product);
            OrderProduct orderProduct = OrderProduct.builder()
                    .product(product)
                    .count(cart.getCount())
                    .build();
            orderProducts.add(orderProduct);
        }

        //주문 생성
        Order order = Order.builder()
                .member(member)
                .delivery(delivery)
                .merchantUid(merchantUid)
                .orderProducts(orderProducts)
                .build();

        //주문 상품, 주문 저장
        orderRepository.save(order);
    }
}
