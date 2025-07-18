package archivegarden.shop.repository.wish;

import archivegarden.shop.dto.user.wish.MyWishDto;
import archivegarden.shop.dto.user.wish.QMyWishDto;
import archivegarden.shop.entity.ImageType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;

import static archivegarden.shop.entity.QDiscount.discount;
import static archivegarden.shop.entity.QProduct.product;
import static archivegarden.shop.entity.QProductImage.productImage;
import static archivegarden.shop.entity.QWish.wish;

public class WishRepositoryCustomImpl implements WishRepositoryCustom {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public WishRepositoryCustomImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    /**
     * 마이페이지에서 내 위시리스트 목록 조회
     *
     * @param memberId 회원 ID
     * @return MyWishDto List 객체
     */
    @Override
    public List<MyWishDto> findMyWishList(Long memberId) {
        return queryFactory.select(new QMyWishDto(
                        product.id,
                        product.name,
                        product.price,
                        product.stockQuantity,
                        discount.discountPercent,
                        productImage.imageUrl
                ))
                .from(wish)
                .leftJoin(wish.product, product)
                .leftJoin(product.discount, discount)
                .leftJoin(product.productImages, productImage)
                .where(
                        memberIdEq(memberId),
                        imageTypeEqDisplay()
                )
                .fetch();
    }

    /**
     * 이미지 타입이 DISPLAY인 이미지 필터링 조건
     */
    private BooleanExpression imageTypeEqDisplay() {
        return productImage.imageType.eq(ImageType.DISPLAY);
    }

    private BooleanExpression memberIdEq(Long memberId) {
        return wish.member.id.eq(memberId);
    }
}
