package archivegarden.shop.service.product;

import archivegarden.shop.entity.Member;
import archivegarden.shop.entity.Product;
import archivegarden.shop.entity.Wish;
import archivegarden.shop.exception.ajax.NoSuchMemberAjaxException;
import archivegarden.shop.exception.ajax.NoSuchProductAjaxException;
import archivegarden.shop.repository.MemberRepository;
import archivegarden.shop.repository.shop.ProductRepository;
import archivegarden.shop.repository.shop.WishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class WishService {

    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final WishRepository wishRepository;

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

        //찜 엔티티 생성
        Wish wish = Wish.builder()
                .member(member)
                .product(product)
                .build();

        //찜 엔티티 저장
        wishRepository.save(wish);

        return wish.getId();
    }

    /**
     * 위시 삭제
     * @param productId
     * @param memberId
     */
    public void remove(Long productId, Long memberId) {
        //찜 조회
        Wish wish = wishRepository.findWish(productId, memberId);

        //찜 엔티티 삭제
        wishRepository.delete(wish);
    }
}
