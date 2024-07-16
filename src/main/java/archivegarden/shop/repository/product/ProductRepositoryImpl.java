package archivegarden.shop.repository.product;

import archivegarden.shop.dto.admin.product.product.AdminProductSearchForm;
import archivegarden.shop.dto.community.inquiry.ProductPopupDto;
import archivegarden.shop.dto.community.inquiry.QProductPopupDto;
import archivegarden.shop.dto.product.ProductSearchCondition;
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

import static archivegarden.shop.entity.QDiscount.discount;
import static archivegarden.shop.entity.QProduct.product;
import static archivegarden.shop.entity.QProductImage.productImage;
import static org.springframework.util.StringUtils.hasText;

public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public ProductRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(JPQLTemplates.DEFAULT, em);
    }

    @Override
    public Product findProduct(Long productId) {
        return queryFactory
                .selectFrom(product)
                .leftJoin(product.discount, discount).fetchJoin()
                .leftJoin(product.productImages, productImage).fetchJoin()
                .where(product.id.eq(productId))
                .fetchOne();
    }

    @Override
    public List<Product> findMainProducts() {
        return queryFactory
                .selectFrom(product)
                .leftJoin(product.discount, discount).fetchJoin()
                .leftJoin(product.productImages, productImage).fetchJoin()
                .where(imageTypeDisplay())
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
                .leftJoin(product.productImages, productImage).fetchJoin()
                .where(
                        categoryEq(condition.getCategory()),
                        imageTypeDisplay()
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

    @Override
    public Page<Product> search(String keyword, Pageable pageable) {
        List<Product> content = queryFactory
                .selectFrom(product)
                .leftJoin(product.discount, discount).fetchJoin()
                .leftJoin(product.productImages, productImage).fetchJoin()
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
    public Page<ProductPopupDto> findDtoAllPopup(String keyword, Pageable pageable) {
        List<ProductPopupDto> content = queryFactory.select(new QProductPopupDto(
                        product.id,
                        product.name,
                        product.price,
                        productImage.storeImageName
                ))
                .from(product)
                .leftJoin(product.productImages, productImage)
                .on(
                        product.id.eq(productImage.product.id),
                        productImage.imageType.eq(ImageType.DISPLAY))
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

    @Override
    public Page<Product> findAdminDtoAll(AdminProductSearchForm form, Pageable pageable) {
        List<Product> content = queryFactory
                .selectFrom(product)
                .leftJoin(product.discount, discount).fetchJoin()
                .leftJoin(product.productImages, productImage).fetchJoin()
                .where(
                        adminKeywordLike(form.getSearchKey(), form.getKeyword()),
                        adminCategoryEq(form.getCategory()),
                        imageTypeDisplay()
                )
                .orderBy(product.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(product.count())
                .where(
                        adminKeywordLike(form.getSearchKey(), form.getKeyword()),
                        adminCategoryEq(form.getCategory())
                )
                .from(product);

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

    /**
     * 검색어
     * 공백 제거, 대소문자 구분 안함
     */
    private BooleanExpression keywordLike(String keyword) {
        String replaceKeyword = StringUtils.replace(keyword, " ", "");
        StringTemplate replaceProductName = Expressions.stringTemplate("function('replace',{0},{1},{2})", product.name, " ", "");
        return hasText(keyword) ? replaceProductName.containsIgnoreCase(replaceKeyword) : null;
    }

    /**
     * 카테고리
     */
    private BooleanExpression categoryEq(Category category) {
        return category != null ? product.category.eq(category) : null;
    }

    /**
     * IMAGE TYPE = DISPLAY
     */
    private BooleanExpression imageTypeDisplay() {
        return productImage != null ? productImage.imageType.eq(ImageType.DISPLAY) : null;
    }

    private BooleanExpression adminKeywordLike(String searchKey, String keyword) {
        if (keyword != null) {
            if (searchKey.equals("name")) {
                return keywordLike(keyword);
            }
        }

        return null;
    }

    private BooleanExpression adminCategoryEq(String category) {
        if(StringUtils.hasText(category)) {
            return product.category.eq(Category.of(category));
        }

        return null;
    }
}
