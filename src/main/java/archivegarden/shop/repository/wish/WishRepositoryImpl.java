package archivegarden.shop.repository.wish;

import archivegarden.shop.dto.mypage.MyWishDto;
import archivegarden.shop.dto.mypage.QMyWishDto;
import archivegarden.shop.entity.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static archivegarden.shop.entity.QDiscount.discount;
import static archivegarden.shop.entity.QProduct.product;
import static archivegarden.shop.entity.QProductImage.productImage;
import static archivegarden.shop.entity.QWish.wish;

public class WishRepositoryImpl implements WishRepositoryCustom {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public WishRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<MyWishDto> findDtoAll(Long memberId, Pageable pageable) {

        List<MyWishDto> content = queryFactory.select(new QMyWishDto(
                        wish.id,
                        product.id,
                        product.name,
                        product.price,
                        product.stockQuantity,
                        discount.discountPercent,
                        productImage.storeImageName
                ))
                .from(wish)
                .leftJoin(wish.product, product)
                .leftJoin(product.discount, discount)
                .leftJoin(product.images, productImage)
                .where(
                        memberIdEq(memberId),
                        imageTypeEqDisplay()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(wish.count())
                .from(wish)
                .where(
                        memberIdEq(memberId)
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression imageTypeEqDisplay() {
        return productImage.imageType.eq(ImageType.DISPLAY);
    }

    private BooleanExpression memberIdEq(Long memberId) {
        return wish.member.id.eq(memberId);
    }
}
