package archivegarden.shop.service.product;

import archivegarden.shop.dto.mypage.MyWishDto;
import archivegarden.shop.entity.Member;
import archivegarden.shop.entity.Product;
import archivegarden.shop.entity.Wish;
import archivegarden.shop.exception.NoSuchWishException;
import archivegarden.shop.exception.ajax.NoSuchMemberAjaxException;
import archivegarden.shop.exception.ajax.NoSuchProductAjaxException;
import archivegarden.shop.repository.member.MemberRepository;
import archivegarden.shop.repository.shop.ProductRepository;
import archivegarden.shop.repository.wish.WishRepository;
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

    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final WishRepository wishRepository;

    /**
     * 좋아요한 상품인지 아닌지
     */
    public boolean isWish(Long productId, Member member) {
        //로그인 하지 않은 경우
        if(member == null) {
            return false;
        }

        return wishRepository.findWish(productId, member.getId()).isPresent();
    }

    /**
     * 마이페이지에서 위시리스트 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<MyWishDto> getWishList(Long memberId, Pageable pageable) {
        return wishRepository.findDtoAll(memberId, pageable);
    }

    /**
     * 위시 추가
     *
     * @throws NoSuchProductAjaxException
     * @throws NoSuchMemberAjaxException
     */
    public Long add(Long productId, Long memberId) {
        //회원, 상품 엔티티 조회
        Product product = productRepository.findById(productId).orElseThrow(() -> new NoSuchProductAjaxException("존재하지 않는 상품입니다."));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchMemberAjaxException("존재하지 않는 회원입니다."));

        //위시 엔티티 생성
        Wish wish = Wish.builder()
                .member(member)
                .product(product)
                .build();

        //위시 엔티티 저장
        wishRepository.save(wish);

        return wish.getId();
    }

    /**
     * 위시 삭제
     *
     * @throws NoSuchElementException
     */
    public void remove(Long productId, Long memberId) {
        //위시 조회
        Wish wish = wishRepository.findWish(productId, memberId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 위시입니다."));

        //위시 삭제
        wishRepository.delete(wish);
    }

    /**
     * 위시 삭제
     *
     * @throws NoSuchWishException
     */
    public void delete(Long wishId) {
        //위시 조회
        Wish wish = wishRepository.findById(wishId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 위시입니다."));

        //위시 삭제
        wishRepository.delete(wish);
    }
}
