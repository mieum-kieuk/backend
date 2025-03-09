package archivegarden.shop.service.order;

import archivegarden.shop.dto.ResultResponse;
import archivegarden.shop.dto.order.CartListDto;
import archivegarden.shop.dto.user.cart.CartResultResponse;
import archivegarden.shop.entity.Cart;
import archivegarden.shop.entity.Member;
import archivegarden.shop.entity.Product;
import archivegarden.shop.exception.ajax.AjaxEntityNotFoundException;
import archivegarden.shop.exception.ajax.NotEnoughStockAjaxException;
import archivegarden.shop.exception.common.EntityNotFoundException;
import archivegarden.shop.repository.cart.CartRepository;
import archivegarden.shop.repository.member.MemberRepository;
import archivegarden.shop.repository.product.ProductRepository;
import archivegarden.shop.service.user.product.product.ProductImageService;
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

    private final ProductImageService productImageService;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;

    /**
     * 장바구니 조회
     */
    @Transactional(readOnly = true)
    public List<CartListDto> getCarts(Long memberId) {
        return cartRepository.findAllProducts(memberId)
                .stream()
                .map(c -> {
                    String encodedImageData = productImageService.getEncodedImageData(c.getDisplayImageData());
                    c.setDisplayImageData(encodedImageData);
                    return c;
                }).collect(Collectors.toList());
    }

    /**
     * 장바구니에 상품 추가
     *
     * @throws AjaxEntityNotFoundException
     */
    public CartResultResponse addCart(int count, Long memberId, Long productId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new AjaxEntityNotFoundException("존재하지 않는 회원입니다."));
        Product product = productRepository.findById(productId).orElseThrow(() -> new AjaxEntityNotFoundException("존재하지 않는 상품입니다.."));
        Cart cart = cartRepository.findByMemberAndProduct(member, product);
        if(cart == null) {
            cart = Cart.builder()
                    .count(count)
                    .member(member)
                    .product(product)
                    .build();
            cartRepository.save(cart);
            return new CartResultResponse(HttpStatus.OK.value(), "장바구니에 상품이 담겼습니다.");
        } else {
            cart.updateCount(count);
            return new CartResultResponse(HttpStatus.OK.value(), "장바구니에 상품이 담겼습니다.\n이미 담긴 상품의 수량을 추가했습니다.");
        }
    }

    /**
     * 장바구니에 담긴 상품 수량 1개 증가
     *
     * @throws AjaxEntityNotFoundException
     */
    public ResultResponse increaseCount(Long productId, Member loginMember) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new AjaxEntityNotFoundException("존재하지 않는 상품입니다."));
        Cart cart = cartRepository.findByMemberAndProduct(loginMember, product);
        if(cart == null) {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), "장바구니에 존재하지 않는 상품입니다.\n다시 시도해 주세요.");
        }

        if(cart.getCount() + 1 > product.getStockQuantity()) {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), "재고가 부족합니다.");
        }

        cart.updateCount(1);
        return new ResultResponse(HttpStatus.OK.value(), "상품 수량이 성공적으로 증가되었습니다.");
    }

    /**
     * 장바구니에 담긴 상품 수량 1개 감소
     *
     * @throws AjaxEntityNotFoundException
     */
    public ResultResponse decreaseCount(Long productId, Member loginMember) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new AjaxEntityNotFoundException("존재하지 않는 상품입니다."));
        Cart cart = cartRepository.findByMemberAndProduct(loginMember, product);
        if(cart == null) {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), "장바구니에 존재하지 않는 상품입니다.\n다시 시도해 주세요.");
        }

        cart.updateCount(-1);
        return new ResultResponse(HttpStatus.OK.value(), "상품 수량이 성공적으로 감소되었습니다.");
    }

    /**
     * 장바구니에 담긴 상품 삭제
     *
     * @throws AjaxEntityNotFoundException
     */
    public void deleteCarts(List<Long> productIds, Member loginMember) {
        productIds.stream().forEach(productId -> {
            Product product = productRepository.findById(productId).orElseThrow(() -> new AjaxEntityNotFoundException("존재하지 않는 상품입니다."));
            cartRepository.deleteByMemberAndProduct(loginMember, product);
        });
    }

    /**
     * 카트에 담긴 상품 개수<br>
     */
    public int getCartItemCount(String loginId) {
        Member member = memberRepository.findByLoginId(loginId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));
        return cartRepository.countByMember(member);
    }

    /**
     *  재고 확인
     *
     *  @throws AjaxEntityNotFoundException
     */
    public void validateStockQuantity(List<Long> productIds, Member loginMember) {
        for (Long productId : productIds) {
            Product product = productRepository.findById(productId).orElseThrow(() -> new AjaxEntityNotFoundException("존재하지 않는 상품입니다."));
            Cart cart = cartRepository.findByMemberAndProduct(loginMember, product);
            if(cart.getCount() > product.getStockQuantity()) {
                throw new NotEnoughStockAjaxException("[" + product.getName() + "] 상품의 재고가 부족합니다.");
            }
        }
    }
}
