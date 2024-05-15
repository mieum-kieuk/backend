package archivegarden.shop.service.order;

import archivegarden.shop.dto.order.CartListDto;
import archivegarden.shop.entity.Cart;
import archivegarden.shop.entity.Member;
import archivegarden.shop.entity.Product;
import archivegarden.shop.exception.ajax.NoSuchMemberAjaxException;
import archivegarden.shop.exception.ajax.NoSuchProductAjaxException;
import archivegarden.shop.repository.MemberRepository;
import archivegarden.shop.repository.order.CartRepository;
import archivegarden.shop.repository.shop.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;

    /**
     * 카트 목록 조회
     */
    @Transactional(readOnly = true)
    public List<CartListDto> getCart(Member member) {
        return cartRepository.findAll(member)
                .stream()
                .map(c -> new CartListDto(c.getProduct(), c.getCount()))
                .collect(Collectors.toList());
    }

    /**
     * 카트에 상품 추가
     *
     * @throws NoSuchMemberAjaxException
     * @throws NoSuchProductAjaxException
     */
    public String addCart(int count, Long memberId, Long productId) {
        //Member, Product 엔티티 조회
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchMemberAjaxException("존재하지 않는 회원입니다."));
        Product product = productRepository.findById(productId).orElseThrow(() -> new NoSuchProductAjaxException("존재하지 않는 상품입니다.."));

        //카트에 담겨있는 상품인지 확인
        Cart cart = cartRepository.findByMemberAndProduct(member, product);

        //재고 확인
        if(product.getStockQuantity() < count) {
            return "재고가 부족합니다.";
        }

        if(cart == null) {
            //Cart 엔티티 생성
            cart = Cart.builder()
                    .count(count)
                    .member(member)
                    .product(product)
                    .build();

            //Cart 저장
            cartRepository.save(cart);

            return "장바구니에 상품을 담았습니다.";
        } else {
           //수량 증가
            cart.addCount(count);
            return "장바구니에 상품을 담았습니다.\n이미 담은 상품의 수량을 추가했습니다.";
        }
    }

    /**
     * 카트에 담긴 상품 단 삭제
     *
     * @throws NoSuchProductAjaxException
     */
    public void deleteCart(Long productId, Member loginMember) {
        //Product 엔티티 조회
        Product product = productRepository.findById(productId).orElseThrow(() -> new NoSuchProductAjaxException("존재하지 않는 상품입니다.."));

        //Cart 엔티티 삭제
        cartRepository.deleteByMemberAndProduct(loginMember, product);
    }

    /**
     * 카트에 담긴 상품 여러개 삭제
     *
     * @throws NoSuchProductAjaxException
     */
    public void deleteCarts(List<Long> productIds, Member loginMember) {
        productIds.stream().forEach(productId -> {
            //Product 엔티티 조회
            Product product = productRepository.findById(productId).orElseThrow(() -> new NoSuchProductAjaxException("존재하지 않는 상품입니다.."));

            //Cart 엔티티 삭제
            cartRepository.deleteByMemberAndProduct(loginMember, product);
        });
    }
}
