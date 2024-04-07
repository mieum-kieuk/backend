package archivegarden.shop.repository.shop;

import archivegarden.shop.dto.shop.product.ProductSearchCondition;
import archivegarden.shop.entity.Category;
import archivegarden.shop.entity.Product;
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

public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ProductRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Product> findLatestProducts() {
        return queryFactory
                .selectFrom(product)
                .leftJoin(product.discount,discount).fetchJoin()
                .orderBy(product.createdAt.desc())
                .offset(1)
                .limit(9)
                .fetch();
    }

    @Override
    public Page<Product> findAllByCategory(ProductSearchCondition condition, Pageable pageable) {
        List<Product> content = queryFactory
                .selectFrom(product)
                .leftJoin(product.discount,discount).fetchJoin()
                .where(categoryEq(condition.getCategory()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(product.count())
                .from(product)
                .leftJoin(product.discount,discount)
                .where(categoryEq(condition.getCategory()));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression categoryEq(Category category) {
        return category != null ? product.category.eq(category) : null;
    }
}
