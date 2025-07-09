package archivegarden.shop.repository.product;

import archivegarden.shop.dto.user.product.ProductPopupSearchCondition;
import archivegarden.shop.dto.user.product.ProductSearchCondition;
import archivegarden.shop.dto.user.product.ProductSummaryDto;
import archivegarden.shop.dto.user.product.QProductSummaryDto;
import archivegarden.shop.entity.Category;
import archivegarden.shop.entity.ImageType;
import archivegarden.shop.entity.Product;
import archivegarden.shop.entity.SortedType;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static archivegarden.shop.entity.QDiscount.discount;
import static archivegarden.shop.entity.QProduct.product;
import static archivegarden.shop.entity.QProductImage.productImage;
import static org.springframework.util.StringUtils.hasText;

public class UserProductRepositoryCustomImpl implements UserProductRepositoryCustom {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public UserProductRepositoryCustomImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(JPQLTemplates.DEFAULT, em);
    }

    /**
     * 상품 단건 조회
     *
     * - 할인, 이미지 fetchJoin
     *
     * @param productId 상품 ID
     * @return 조회된 상품 Optional
     */
    @Override
    public Optional<Product> findProduct(Long productId) {
        Product result = queryFactory
                .selectFrom(product)
                .leftJoin(product.discount, discount).fetchJoin()
                .leftJoin(product.productImages, productImage).fetchJoin()
                .where(product.id.eq(productId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    /**
     * 카테고리 기반 상품 조회
     *
     * - 할인, 이미지 fetchJoin
     *
     * @param condition 검색 조건
     * @param pageable  페이징 정보
     * @return 상품 Page 객체
     */
    @Override
    public Page<Product> findProductsByCategory(ProductSearchCondition condition, Pageable pageable) {
        List<Product> content = queryFactory
                .selectFrom(product).distinct()
                .leftJoin(product.discount, discount).fetchJoin()
                .join(product.productImages, productImage).fetchJoin()
                .where(
                        categoryEq(condition.getCategory()),
                        productImage.imageType.ne(ImageType.DETAILS)
                )
                .orderBy(getOrderSpecifier(condition.getSorted_type()).stream().toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(product.count())
                .from(product)
                .where(categoryEq(condition.getCategory()));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    /**
     * 키워드로 상품 검색
     *
     * - 정렬 기준: 품절 상품은 맨 뒤
     * - 할인, 상품 이미지 fetchJoin
     *
     * @param keyword  검색어
     * @param pageable 페이징 정보
     * @return 검색된 상품 목록 Page 객체
     */
    @Override
    public Page<Product> searchProducts(String keyword, Pageable pageable) {
        List<Product> content = queryFactory
                .selectFrom(product).distinct()
                .leftJoin(product.discount, discount).fetchJoin()
                .join(product.productImages, productImage).fetchJoin()
                .where(
                        nameLike(keyword),
                        productImage.imageType.ne(ImageType.DETAILS)
                )
                .orderBy(orderSoldOutLast())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(product.count())
                .from(product)
                .where(nameLike(keyword));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    /**
     * 최신 상품 9개 조회
     *
     * - 정렬 기준: 상품 등록일 기준 내림차순
     * - 할인, 상품 이미지 fetchJoin
     *
     * @return 상품 리스트
     */
    @Override
    public List<Product> findLatestProducts() {
        return queryFactory
                .selectFrom(product).distinct()
                .leftJoin(product.discount, discount).fetchJoin()
                .join(product.productImages, productImage).fetchJoin()
                .where(productImage.imageType.ne(ImageType.DETAILS))
                .orderBy(product.createdAt.desc())
                .offset(0)
                .limit(9)
                .fetch();
    }

    /**
     * 팝업창에서 상품 검색
     *
     * @param condition 검색 조건
     * @param pageable  페이징 정보
     * @return 상품 요약 정보 Page 객체
     */
    @Override
    public Page<ProductSummaryDto> searchProductsInInquiryPopup(ProductPopupSearchCondition condition, Pageable pageable) {
        List<ProductSummaryDto> content = queryFactory.select(new QProductSummaryDto(
                        product.id,
                        product.name,
                        product.price,
                        productImage.imageUrl
                ))
                .from(product)
                .leftJoin(product.productImages, productImage)
                .on(
                        product.id.eq(productImage.product.id),
                        imageTypeDisplay()
                )
                .where(
                        nameLike(condition.getKeyword()),
                        categoryEq(condition.getCategory())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(product.count())
                .from(product)
                .where(
                        nameLike(condition.getKeyword()),
                        categoryEq(condition.getCategory())
                );

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
                    orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, product.createdAt));
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
        return new CaseBuilder()
                .when(product.stockQuantity.eq(0)).then(0)
                .otherwise(1).desc();
    }

    /**
     * 낮은 가격순, 높은 가격순 - 할인가로 정렬
     */
    private NumberExpression<Integer> calcDiscountPrice() {
        return new CaseBuilder()
                .when(product.discount.isNull()).then(product.price)
                .otherwise(product.price.subtract((product.price.multiply(discount.discountPercent).divide(100))));
    }

    /**
     * 상품명 검색 조건 (공백 제거, 대소문자 무시)
     */
    private BooleanExpression nameLike(String keyword) {
        if (hasText(keyword)) {
            String replaceKeyword = StringUtils.replace(keyword, " ", "");
            StringTemplate replaceProductName = Expressions.stringTemplate("function('replace',{0},{1},{2})", product.name, " ", "");
            return replaceProductName.containsIgnoreCase(replaceKeyword);
        }

        return null;
    }

    /**
     * 카테고리 조건
     */
    private BooleanExpression categoryEq(Category category) {
        return category != null ? product.category.eq(category) : null;
    }

    /**
     * 이미지 타입이 DISPLAY인 이미지 필터링 조건
     */
    private BooleanExpression imageTypeDisplay() {
        return productImage != null ? productImage.imageType.eq(ImageType.DISPLAY) : null;
    }
}
