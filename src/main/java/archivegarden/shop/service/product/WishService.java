package archivegarden.shop.service.product;

import archivegarden.shop.dto.user.wish.MyWishDto;
import archivegarden.shop.entity.Member;
import archivegarden.shop.entity.Product;
import archivegarden.shop.entity.Wish;
import archivegarden.shop.exception.ajax.EntityNotFoundAjaxException;
import archivegarden.shop.repository.member.MemberRepository;
import archivegarden.shop.repository.product.ProductRepository;
import archivegarden.shop.repository.wish.WishRepository;
import archivegarden.shop.service.user.product.product.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class WishService {

    private final ProductImageService productImageService;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final WishRepository wishRepository;

    /**
     * 위시리스트에 상품 추가
     *
     * @param productId 추가할 상품 ID
     * @param memberId  현재 로그인한 회원 ID
     * @return 생성된 위시 ID
     * @throws EntityNotFoundAjaxException 상품 혹은 회원이 존재하지 않을 경우
     */
    public Long add(Long productId, Long memberId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new EntityNotFoundAjaxException("존재하지 않는 상품입니다."));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundAjaxException("존재하지 않는 회원입니다."));

        Wish wish = Wish.createWish(product, member);
        wishRepository.save(wish);

        return wish.getId();
    }

    /**
     * 위시리스트에서 상품 삭제
     *
     * @param productId 삭제할 상품 ID
     * @param memberId  현재 로그인한 회원 ID
     * @throws EntityNotFoundAjaxException 존재하지 않는 위시일 경우
     */
    public void remove(Long productId, Long memberId) {
        Wish wish = wishRepository.findWish(productId, memberId).orElseThrow(() -> new EntityNotFoundAjaxException("존재하지 않는 위시입니다."));
        wishRepository.delete(wish);
    }

    /**
     * 마이페이지에서 회원의 위시 상품 목록을 조회
     *
     * @param memberId 현재 로그인한 회원 ID
     * @param pageable 페이징 정보
     * @return 회원의 위시 상품 목록 Page 객체
     */
    @Transactional(readOnly = true)
    public Page<MyWishDto> getWishList(Long memberId, Pageable pageable) {
        Page<MyWishDto> myWishDtos = wishRepository.findDtoAll(memberId, pageable);
        myWishDtos.forEach(w -> {
//            String encodedImageData = productImageService.getEncodedImageData(w.getDisplayImageData());
//            w.setDisplayImageData(encodedImageData);
        });

        return myWishDtos;
    }

    /**
     * 회원이 특정 상품을 좋아요 했는지 여부 확인
     *
     * @param productId 확인할 상품 ID
     * @param member    로그인한 회원 객체 (null 가능)
     * @return 좋아요한 상품 -> true 반환 , 좋아요하지 않은 상품 또는 비로그인 상태 -> false 반환
     */
    @Transactional(readOnly = true)
    public boolean isWish(Long productId, Member member) {
        //로그인 하지 않은 경우
        if (member == null) {
            return false;
        }

        return wishRepository.findWish(productId, member.getId()).isPresent();
    }
}
