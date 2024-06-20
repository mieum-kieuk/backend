package archivegarden.shop.repository.shop;

import archivegarden.shop.dto.admin.shop.product.ProductListDto;
import archivegarden.shop.dto.admin.shop.product.QProductListDto;
import archivegarden.shop.dto.shop.product.ProductSearchCondition;
import archivegarden.shop.entity.Category;
import archivegarden.shop.entity.ImageType;
import archivegarden.shop.entity.Product;
import archivegarden.shop.entity.SortedType;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static archivegarden.shop.entity.QDiscount.discount;
import static archivegarden.shop.entity.QProduct.product;
import static archivegarden.shop.entity.QProductImage.productImage;
import static org.springframework.util.StringUtils.hasText;

public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public ProductRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Product findProductFetch(Long productId) {
        return queryFactory
                .selectFrom(product)
                .leftJoin(product.discount, discount).fetchJoin()
                .leftJoin(product.images, productImage).fetchJoin()
                .where(product.id.eq(productId))
                .fetchOne();
    }

    @Override
    public Page<ProductListDto> findDtoAll(Pageable pageable) {
        List<ProductListDto> content = queryFactory
                .select(new QProductListDto(
                        product.id,
                        product.name,
                        product.category,
                        product.price,
                        product.stockQuantity,
                        discount.discountPercent,
                        productImage.storeImageName
                ))
                .from(product)
                .leftJoin(product.discount, discount)
                .leftJoin(product.images, productImage)
                .where(productImage.imageType.eq(ImageType.DISPLAY))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(product.count())
                .from(product);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public List<Product> findLatestProducts() {
        return queryFactory
                .selectFrom(product)
                .leftJoin(product.discount, discount).fetchJoin()
                .leftJoin(product.images, productImage).fetchJoin()
                .where(imageTypeNe())
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
                .leftJoin(product.images, productImage).fetchJoin()
                .where(
                        categoryEq(condition.getCategory()),
                        imageTypeNe()
                )
                .orderBy(getOrderSpecifier(condition.getSorted_type()).stream().toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(product.count())
                .from(product)
                .leftJoin(product.discount, discount)
                .where(categoryEq(condition.getCategory()));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<Product> search(String keyword, Pageable pageable) {
        List<Product> content = queryFactory
                .selectFrom(product)
                .leftJoin(product.discount, discount).fetchJoin()
                .leftJoin(product.images, productImage).fetchJoin()
                .where(keywordLike(keyword))
                .orderBy(orderSoldOutLast())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(product.count())
                .from(product)
                .where(keywordLike(keyword));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<Product> findAllPopup(Pageable pageable, String keyword) {
        List<Product> content = queryFactory
                .selectFrom(product)
                .leftJoin(product.images, productImage)
                .on(product.id.eq(productImage.product.id), productImage.imageType.eq(ImageType.DISPLAY))
                .where(keywordLike(keyword))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(product.count())
                .from(product)
                .where(keywordLike(keyword));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    /**
     * OrderSpecifier 리스트 객체 생성
     */
    private List<OrderSpecifier> getOrderSpecifier(SortedType sortedType) {

        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

        orderSpecifiers.add(orderSoldOutLast());

        if (sortedType != null) {
            switch (sortedType) {
                case NEW:
                    orderSpecifiers.add(new OrderSpecifier<>(Order.ASC, product.createdAt));
                    break;
                case NAME:
                    orderSpecifiers.add(new OrderSpecifier<>(Order.ASC, product.name));
                    break;
                case LOW_PRICE:
                    orderSpecifiers.add(new OrderSpecifier<>(Order.ASC, calcDiscountPrice()));
                    break;
                case HIGH_PRICE:
                    orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, calcDiscountPrice()));
                    break;
            }
        }

        orderSpecifiers.add(new OrderSpecifier<>(Order.ASC, product.id));
        return orderSpecifiers;
    }

    /**
     * sold out 상품 정렬에서 맨 뒤로
     */
    private OrderSpecifier<?> orderSoldOutLast() {
        return Expressions.stringTemplate("decode({0}, {1}, {2})", product.stockQuantity, 0, 1)
                .asc();
    }

    /**
     * 낮은 가격순, 높은 가격순 - 할인가로 정렬
     */
    private NumberExpression<Integer> calcDiscountPrice() {
        return new CaseBuilder()
                .when(product.discount.isNull()).then(product.price)
                .otherwise(product.price.subtract((product.price.multiply(discount.discountPercent).divide(100))));
    }

    private BooleanExpression keywordLike(String keyword) {
        //공백 제거
        String replaceKeyword = StringUtils.replace(keyword, " ", "");
        StringTemplate replaceProductName = Expressions.stringTemplate("function('replace',{0},{1},{2})", product.name, " ", "");
        return hasText(keyword) ? replaceProductName.containsIgnoreCase(replaceKeyword) : null;
    }

    private BooleanExpression categoryEq(Category category) {
        return category != null ? product.category.eq(category) : null;
    }

    private BooleanExpression imageTypeNe() {
        return productImage != null ? productImage.imageType.ne(ImageType.DETAILS) : null;
    }
}
