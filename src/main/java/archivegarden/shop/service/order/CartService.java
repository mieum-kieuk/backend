package archivegarden.shop.service.order;

import archivegarden.shop.dto.ResultResponse;
import archivegarden.shop.dto.user.order.CartListDto;
import archivegarden.shop.dto.user.cart.CartResultResponse;
import archivegarden.shop.entity.Cart;
import archivegarden.shop.entity.Member;
import archivegarden.shop.entity.Product;
import archivegarden.shop.exception.ajax.EntityNotFoundAjaxException;
import archivegarden.shop.exception.ajax.NotEnoughStockAjaxException;
import archivegarden.shop.exception.global.EntityNotFoundException;
import archivegarden.shop.repository.cart.CartRepository;
import archivegarden.shop.repository.member.MemberRepository;
import archivegarden.shop.repository.product.ProductRepository;
import archivegarden.shop.service.user.product.product.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {

    private final ProductImageService productImageService;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final Executor executor;

    /**
     * 장바구니 조회
     *
     * @param memberId 조회할 회원 ID
     * @return 장바구니 상품 목록 DTO
     */
    @Transactional(readOnly = true)
    public List<CartListDto> getCarts(Long memberId) {
        List<CartListDto> cartListDtos = cartRepository.findCartItems(memberId);

        List<CompletableFuture<Void>> futures = cartListDtos.stream()
                .map(item -> CompletableFuture.runAsync(() -> {
                    String encodedImageData = productImageService.downloadAndEncodeImage(item.getDisplayImage());
                    item.setDisplayImage(encodedImageData);
                }, executor))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        return cartListDtos;
    }

    /**
     * 장바구니에 상품 추가
     *
     * 장바구니에 상품을 담거나 이미 담긴 상품일 경우 수량을 증가시킵니다.
     *
     * @param memberId  회원 ID
     * @param productId 장바구니에 담을 상품 ID
     * @param count     장바구니에 담을 수량
     * @return 장바구니 결과 응답 DTO
     * @throws EntityNotFoundAjaxException 존재하지 않는 회원 또는 상품일 경우
     */
    public CartResultResponse addCart(Long memberId, Long productId, int count) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundAjaxException("존재하지 않는 회원입니다."));
        Product product = productRepository.findById(productId).orElseThrow(() -> new EntityNotFoundAjaxException("존재하지 않는 상품입니다.."));

        if (count > product.getStockQuantity()) {
            throw new NotEnoughStockAjaxException("재고가 부족합니다.");
        }

        Cart cart = cartRepository.findByMemberAndProduct(member, product);

        if (cart == null) {
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
     * 장바구니에서 상품 수량을 1개 증가
     *
     * @param productId   증가할 상품 ID
     * @param loginMember 현재 로그인한 회원
     * @return 결과 응답 DTO
     * @throws EntityNotFoundAjaxException 존재하지 않는 상품일 경우
     */
    public ResultResponse increaseCount(Long productId, Member loginMember) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new EntityNotFoundAjaxException("존재하지 않는 상품입니다."));
        Cart cart = cartRepository.findByMemberAndProduct(loginMember, product);

        if (cart == null) {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), "장바구니에 존재하지 않는 상품입니다.\n다시 시도해 주세요.");
        }

        if (cart.getCount() + 1 > product.getStockQuantity()) {
            throw new NotEnoughStockAjaxException("재고가 부족합니다.");
        }

        cart.updateCount(1);
        return new ResultResponse(HttpStatus.OK.value(), "상품 수량이 성공적으로 증가되었습니다.");
    }

    /**
     * 장바구니에 담긴 상품 수량을 1개 감소
     *
     * @param productId   감소할 상품 ID
     * @param loginMember 현재 로그인한 회원
     * @return 수량 감소 처리 결과 응답 DTO
     * @throws EntityNotFoundAjaxException 존재하지 않는 상품일 경우
     */
    public ResultResponse decreaseCount(Long productId, Member loginMember) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new EntityNotFoundAjaxException("존재하지 않는 상품입니다."));
        Cart cart = cartRepository.findByMemberAndProduct(loginMember, product);
        if (cart == null) {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), "장바구니에 존재하지 않는 상품입니다.\n다시 시도해 주세요.");
        }

        cart.updateCount(-1);
        return new ResultResponse(HttpStatus.OK.value(), "상품 수량이 성공적으로 감소되었습니다.");
    }

    /**
     * 장바구니에서 상품 삭제
     *
     * @param productIds  삭제할 상품 ID 리스트
     * @param loginMember 현재 로그인한 회원
     * @throws EntityNotFoundAjaxException 존재하지 않는 상품일 경우
     */
    public void deleteCarts(List<Long> productIds, Member loginMember) {
        productIds.stream().forEach(productId -> {
            Product product = productRepository.findById(productId).orElseThrow(() -> new EntityNotFoundAjaxException("존재하지 않는 상품입니다."));
            cartRepository.deleteByMemberAndProduct(loginMember, product);
        });
    }

    /**
     * 장바구니에 담긴 상품의 재고를 확인
     *
     * @param productIds  확인할 상품 ID 리스트
     * @param loginMember 현재 로그인한 회원
     * @throws EntityNotFoundAjaxException 존재하지 않는 상품일 경우
     * @throws NotEnoughStockAjaxException 재고가 부족할 경우
     */
    public void validateStockQuantity(List<Long> productIds, Member loginMember) {
        for (Long productId : productIds) {
            Product product = productRepository.findById(productId).orElseThrow(() -> new EntityNotFoundAjaxException("존재하지 않는 상품입니다."));
            Cart cart = cartRepository.findByMemberAndProduct(loginMember, product);
            if (product.getStockQuantity() == 0 || cart.getCount() > product.getStockQuantity()) {
                throw new NotEnoughStockAjaxException("[" + product.getName() + "] 재고가 부족합니다.");
            }
        }
    }

    /**
     * 장바구니에 담긴 상품 개수 조회
     *
     * @param loginId 회원의 로그인 ID
     * @return 장바구니에 담긴 상품 개수
     * @throws EntityNotFoundException 존재하지 않는 회원일 경우
     */
    @Transactional(readOnly = true)
   public int getCartItemCount(String loginId) {
        Member member = memberRepository.findByLoginId(loginId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));
        return cartRepository.countCartsByMember(member);
    }
}
