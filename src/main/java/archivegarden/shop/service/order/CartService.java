package archivegarden.shop.service.order;

import archivegarden.shop.dto.ResultResponse;
import archivegarden.shop.dto.order.CartListDto;
import archivegarden.shop.entity.*;
import archivegarden.shop.exception.ajax.AjaxNotFoundException;
import archivegarden.shop.exception.ajax.NotEnoughStockAjaxException;
import archivegarden.shop.repository.member.MemberRepository;
import archivegarden.shop.repository.order.CartRepository;
import archivegarden.shop.repository.product.ProductRepository;
import archivegarden.shop.service.upload.ProductImageService;
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
    public List<CartListDto> getCart(Member member) {
        return cartRepository.findAll(member)
                .stream()
                .map(c -> {
                    String displayImageUrl = downloadProductDisplayImage(c.getProduct());
                    return new CartListDto(c.getProduct(), c.getCount(), displayImageUrl);
                })
                .collect(Collectors.toList());
    }

    /**
     * 장바구니에 상품 추가
     *
     * @throws AjaxNotFoundException
     */
    public ResultResponse addCart(int count, Long memberId, Long productId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new AjaxNotFoundException("존재하지 않는 회원입니다."));
        Product product = productRepository.findById(productId).orElseThrow(() -> new AjaxNotFoundException("존재하지 않는 상품입니다.."));
        Cart cart = cartRepository.findByMemberAndProduct(member, product);
        if(product.getStockQuantity() < count) {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), "재고가 부족합니다.");
        }

        if(cart == null) {
            cart = Cart.builder()
                    .count(count)
                    .member(member)
                    .product(product)
                    .build();
            cartRepository.save(cart);
            return new ResultResponse(HttpStatus.OK.value(), "장바구니에 상품을 담았습니다.");
        } else {
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
        Product product = productRepository.findById(productId).orElseThrow(() -> new AjaxNotFoundException("존재하지 않는 상품입니다."));
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
     * @throws AjaxNotFoundException
     */
    public ResultResponse decreaseCount(Long productId, Member loginMember) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new AjaxNotFoundException("존재하지 않는 상품입니다."));
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
     * @throws AjaxNotFoundException
     */
    public void deleteCarts(List<Long> productIds, Member loginMember) {
        productIds.stream().forEach(productId -> {
            Product product = productRepository.findById(productId).orElseThrow(() -> new AjaxNotFoundException("존재하지 않는 상품입니다."));
            cartRepository.deleteByMemberAndProduct(loginMember, product);
        });
    }

    /**
     *  재고 확인
     *
     *  @throws AjaxNotFoundException
     */
    public void validateStockQuantity(List<Long> productIds, Member loginMember) {
        for (Long productId : productIds) {
            //Product 조회
            Product product = productRepository.findById(productId).orElseThrow(() -> new AjaxNotFoundException("존재하지 않는 상품입니다."));

            //Cart 조회
            Cart cart = cartRepository.findByMemberAndProduct(loginMember, product);
            if(cart.getCount() > product.getStockQuantity()) {
                throw new NotEnoughStockAjaxException("[" + product.getName() + "] 상품의 재고가 부족합니다.");
            }
        }
    }

    /**
     * 파이어베이스 서버에서 파일 조회
     *
     * @throws
     */
    private String downloadProductDisplayImage(Product product) {
        ProductImage displayImage = product.getProductImages().stream()
                .filter(productImage -> productImage.getImageType().equals(ImageType.DISPLAY))
                .findFirst().get();

        return productImageService.downloadImage(displayImage.getImageUrl());
    }
}
