package archivegarden.shop.repository.shop;

import archivegarden.shop.dto.shop.product.ProductSearchCondition;
import archivegarden.shop.entity.Category;
import archivegarden.shop.entity.Product;
import archivegarden.shop.entity.SortedType;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.ArrayList;
import java.util.List;

import static archivegarden.shop.entity.QDiscount.discount;
import static archivegarden.shop.entity.QProduct.product;
import static org.springframework.util.StringUtils.hasText;

public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ProductRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Product> findLatestProducts() {
        return queryFactory
                .selectFrom(product)
                .leftJoin(product.discount, discount).fetchJoin()
                .orderBy(product.createdAt.desc())
                .offset(0)
                .limit(9)
                .fetch();
    }

    @Override
    public Page<Product> findAllByCategory(ProductSearchCondition condition, Pageable pageable) {

        List<Product> content = queryFactory
                .selectFrom(product)
                .leftJoin(product.discount, discount).fetchJoin()
                .where(
                        keywordLike(condition.getKeyword()),
                        categoryEq(condition.getCategory()))
                .orderBy(getOrderSpecifier(condition.getSorted_type()).stream().toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(product.count())
                .from(product)
                .leftJoin(product.discount, discount)
                .where(
                        keywordLike(condition.getKeyword()),
                        categoryEq(condition.getCategory())
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    /**
     * OrderSpecifier 리스트 객체 생성
     */
    private List<OrderSpecifier> getOrderSpecifier(SortedType sortedType) {

        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

        if(sortedType != null) {
            switch (sortedType) {
                case NEW:
                    orderSpecifiers.add(new OrderSpecifier<>(Order.ASC, product.createdAt));
                    break;
                case NAME:
                    orderSpecifiers.add(new OrderSpecifier<>(Order.ASC, product.name));
                    break;
                case LOW_PRICE:
                    orderSpecifiers.add(new OrderSpecifier<>(Order.ASC, product.price));
                    break;
                case HIGH_PRICE:
                    orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, product.price));
                    break;
            }
        }

        orderSpecifiers.add(new OrderSpecifier<>(Order.ASC, product.id));
        return orderSpecifiers;
    }

        private BooleanExpression keywordLike (String keyword){
            return hasText(keyword) ? product.name.contains(keyword) : null;
        }

        private BooleanExpression categoryEq (Category category){
            return category != null ? product.category.eq(category) : null;
        }
    }
