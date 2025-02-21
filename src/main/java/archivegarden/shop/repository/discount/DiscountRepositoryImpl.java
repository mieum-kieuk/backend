package archivegarden.shop.repository.discount;

import archivegarden.shop.dto.admin.AdminSearchCondition;
import archivegarden.shop.entity.Discount;
import archivegarden.shop.entity.ImageType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

import static archivegarden.shop.entity.QDiscount.discount;
import static archivegarden.shop.entity.QProduct.product;
import static archivegarden.shop.entity.QProductImage.productImage;

public class DiscountRepositoryImpl implements DiscountRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public DiscountRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Discount> findAll(AdminSearchCondition condition, Pageable pageable) {
        List<Discount> content = queryFactory
                .selectFrom(discount)
                .where(keywordLike(condition.getSearchKey(), condition.getKeyword()))
                .orderBy(discount.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(discount.count())
                .from(discount)
                .where(keywordLike(condition.getSearchKey(), condition.getKeyword()));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Optional<Discount> findByIdWithProducts(Long discountId) {
        return queryFactory
                .selectFrom(discount)
                .join(discount.products, product).fetchJoin()
                .join(product.productImages, productImage)
                .on(
                        product.id.eq(productImage.product.id),
                        productImage.imageType.eq(ImageType.DISPLAY)
                )
                .where(discount.id.eq(discountId))
                .stream().findAny();
    }

    private BooleanExpression keywordLike(String searchKey, String keyword) {
        if (StringUtils.hasText(keyword)) {
            if (searchKey.equals("name")) {
                return discount.name.containsIgnoreCase(keyword);
            } else if (searchKey.equals("percent"))
                return discount.discountPercent.eq(Integer.parseInt(keyword));
        }

        return null;
    }
}
