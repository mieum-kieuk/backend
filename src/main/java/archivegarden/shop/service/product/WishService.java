package archivegarden.shop.service.product;

import archivegarden.shop.dto.user.wish.MyWishDto;
import archivegarden.shop.entity.Member;
import archivegarden.shop.entity.Product;
import archivegarden.shop.entity.Wish;
import archivegarden.shop.exception.NoSuchWishException;
import archivegarden.shop.exception.ajax.AjaxEntityNotFoundException;
import archivegarden.shop.repository.member.MemberRepository;
import archivegarden.shop.repository.product.ProductRepository;
import archivegarden.shop.repository.wish.WishRepository;
import archivegarden.shop.service.user.product.product.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

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
     * @throws AjaxEntityNotFoundException
     */
    public Long add(Long productId, Long memberId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new AjaxEntityNotFoundException("존재하지 않는 상품입니다."));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new AjaxEntityNotFoundException("존재하지 않는 회원입니다."));

        Wish wish = Wish.createWish(product, member);
        wishRepository.save(wish);

        return wish.getId();
    }

    /**
     * 위시리스트에서 상품 삭제
     *
     * @throws AjaxEntityNotFoundException
     */
    public void remove(Long productId, Long memberId) {
        Wish wish = wishRepository.findWish(productId, memberId).orElseThrow(() -> new AjaxEntityNotFoundException("존재하지 않는 위시입니다."));
        wishRepository.delete(wish);
    }

    /**
     * 마이페이지에서 위시 상품 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<MyWishDto> getWishList(Long memberId, Pageable pageable) {
        Page<MyWishDto> myWishDtos = wishRepository.findDtoAll(memberId, pageable);
        myWishDtos.forEach(w -> {
            String encodedImageData = productImageService.getEncodedImageData(w.getDisplayImageData());
            w.setDisplayImageData(encodedImageData);
        });

        return myWishDtos;
    }

    /**
     * 좋아요한 상품인지 아닌지
     */
    public boolean isWish(Long productId, Member member) {
        //로그인 하지 않은 경우
        if (member == null) {
            return false;
        }

        return wishRepository.findWish(productId, member.getId()).isPresent();
    }
}
