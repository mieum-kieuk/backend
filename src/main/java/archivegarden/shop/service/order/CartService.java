package archivegarden.shop.service.order;

import archivegarden.shop.dto.ResultResponse;
import archivegarden.shop.dto.order.CartCheckoutListDto;
import archivegarden.shop.dto.order.CartListDto;
import archivegarden.shop.entity.Cart;
import archivegarden.shop.entity.Member;
import archivegarden.shop.entity.Product;
import archivegarden.shop.exception.NoSuchProductException;
import archivegarden.shop.exception.ajax.AjaxNotFoundException;
import archivegarden.shop.exception.ajax.NotEnoughStockAjaxException;
import archivegarden.shop.repository.member.MemberRepository;
import archivegarden.shop.repository.order.CartRepository;
import archivegarden.shop.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
     * 장바구니 조회
     */
    @Transactional(readOnly = true)
    public List<CartListDto> getCart(Member member) {
        return cartRepository.findAll(member)
                .stream()
                .map(c -> new CartListDto(c.getProduct(), c.getCount()))
                .collect(Collectors.toList());
    }

    /**
     * 장바구니에 상품 추가
     *
     * @throws AjaxNotFoundException
     */
    public ResultResponse addCart(int count, Long memberId, Long productId) {
        //Member, Product 엔티티 조회
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new AjaxNotFoundException("존재하지 않는 회원입니다."));
        Product product = productRepository.findById(productId).orElseThrow(() -> new AjaxNotFoundException("존재하지 않는 상품입니다.."));

        //카트에 담겨있는 상품인지 확인
        Cart cart = cartRepository.findByMemberAndProduct(member, product);

        //재고 확인
        if(product.getStockQuantity() < count) {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), "재고가 부족합니다.");
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
            return new ResultResponse(HttpStatus.OK.value(), "장바구니에 상품을 담았습니다.");
        } else {
           //수량 증가
            cart.updateCount(count);
            return new ResultResponse(HttpStatus.OK.value(), "장바구니에 상품을 담았습니다.\n이미 담은 상품의 수량을 추가했습니다.");
        }
    }

    /**
     * 장바구니에 담긴 상품 수량 1개 증가
     *
     * @throws AjaxNotFoundException
     */
    public ResultResponse increaseCount(Long productId, Member loginMember) {
        //Product 조회
        Product product = productRepository.findById(productId).orElseThrow(() -> new AjaxNotFoundException("존재하지 않는 상품입니다."));

        //Cart 조회
        Cart cart = cartRepository.findByMemberAndProduct(loginMember, product);
        if(cart == null) {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), "장바구니에 존재하지 않는 상품입니다.\n다시 시도해 주세요.");
        }

        if(cart.getCount() + 1 > product.getStockQuantity()) {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), "재고가 부족합니다.");
        }

        //수량 1개 증가
        cart.updateCount(1);

        return new ResultResponse(HttpStatus.OK.value(), "상품 수량이 성공적으로 증가되었습니다.");
    }

    /**
     * 장바구니에 담긴 상품 수량 1개 감소
     *
     * @throws AjaxNotFoundException
     */
    public ResultResponse decreaseCount(Long productId, Member loginMember) {
        //Product 조회
        Product product = productRepository.findById(productId).orElseThrow(() -> new AjaxNotFoundException("존재하지 않는 상품입니다."));

        //Cart 조회
        Cart cart = cartRepository.findByMemberAndProduct(loginMember, product);
        if(cart == null) {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), "장바구니에 존재하지 않는 상품입니다.\n다시 시도해 주세요.");
        }

        //수량 1개 감소
        cart.updateCount(-1);

        return new ResultResponse(HttpStatus.OK.value(), "상품 수량이 성공적으로 감소되었습니다.");
    }

    /**
     * 장바구니에 담긴 상품 단건 삭제
     *
     * @throws AjaxNotFoundException
     */
    public void deleteCart(Long productId, Member loginMember) {
        //Product 조회
        Product product = productRepository.findById(productId).orElseThrow(() -> new AjaxNotFoundException("존재하지 않는 상품입니다."));

        //Cart 삭제
        cartRepository.deleteByMemberAndProduct(loginMember, product);
    }

    /**
     * 장바구니에 담긴 상품 여러건 삭제
     *
     * @throws AjaxNotFoundException
     */
    public void deleteCarts(List<Long> productIds, Member loginMember) {
        productIds.stream().forEach(productId -> {
            //Product 조회
            Product product = productRepository.findById(productId).orElseThrow(() -> new AjaxNotFoundException("존재하지 않는 상품입니다."));

            //Cart 삭제
            cartRepository.deleteByMemberAndProduct(loginMember, product);
        });
    }

    /**
     * 주문서로 넘어가기 전 재고 검사
     */
    public void validateStockQuantity(List<Long> productIds, Member loginMember) {
        for (Long productId : productIds) {
            Product product = productRepository.findById(productId).orElseThrow(() -> new AjaxNotFoundException("존재하지 않는 상품입니다."));
            Cart cart = cartRepository.findByMemberAndProduct(loginMember, product);
            if(cart.getCount() > product.getStockQuantity()) {
                throw new NotEnoughStockAjaxException("[" + product.getName() + "] 재고가 부족합니다.");
            }
        }
    }

    /**
     * 주문하려는 상품 목록 조회
     */
    public List<CartCheckoutListDto> getCheckoutProducts(Member loginMember, List<Long> productIds) {
        //Product 조회
        productIds.forEach(id -> productRepository.findById(id).orElseThrow(() -> new NoSuchProductException("존재하지 않는 상품입니다.")));

        return cartRepository.findCheckoutProducts(loginMember, productIds)
                .stream()
                .map(CartCheckoutListDto::new)
                .collect(Collectors.toList());
    }
}
