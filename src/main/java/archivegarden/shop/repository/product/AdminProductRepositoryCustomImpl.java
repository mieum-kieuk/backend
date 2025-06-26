package archivegarden.shop.repository.product;

import archivegarden.shop.dto.admin.product.product.AdminProductSummaryDto;
import archivegarden.shop.dto.admin.product.product.AdminProductPopupSearchCondition;
import archivegarden.shop.dto.admin.product.product.AdminProductSearchCondition;
import archivegarden.shop.dto.admin.product.product.QAdminProductSummaryDto;
import archivegarden.shop.entity.Category;
import archivegarden.shop.entity.ImageType;
import archivegarden.shop.entity.Product;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.JPQLTemplates;
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
import static org.springframework.util.StringUtils.hasText;

public class AdminProductRepositoryCustomImpl implements AdminProductRepositoryCustom {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public AdminProductRepositoryCustomImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(JPQLTemplates.DEFAULT, em);
    }

    /**
     * 상품 ID 리스트로 상품 목록 조회
     *
     * @param productIds 조회할 상품 ID 리스트
     * @return 해당 ID에 해당하는 상품 리스트
     */
    @Override
    public List<Product> findProductsInAdmin(List<Long> productIds) {
        return queryFactory
                .selectFrom(product)
                .where(product.id.in(productIds))
                .fetch();
    }

    /**
     * 상품 상세 조회 시 사용되는 단건 조회
     *
     * - 할인, 상품 이미지 fetchJoin
     *
     * @param productId 조회할 상품 ID
     * @return 조회된 상품, 없으면 Optional.empty()
     */
    @Override
    public Optional<Product> findProductInAdmin(Long productId) {
        Product result = queryFactory
                .selectFrom(product)
                .leftJoin(product.discount, discount).fetchJoin()
                .leftJoin(product.productImages, productImage).fetchJoin()
                .where(product.id.eq(productId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    /**
     * 관리자 상품 목록 검색
     *
     * - 검색 조건: 카테고리, 키워드
     * - 정렬 기준: 생성일 기준 내림차순
     * - 할인, 상품 이미지 fetchJoin
     *
     * @param cond     검색 조건
     * @param pageable 페이징 정보
     * @return 검색 조건에 맞는 상품 목록 Page 객체
     */
    @Override
    public Page<Product> searchProductsInAdmin(AdminProductSearchCondition cond, Pageable pageable) {
        List<Product> content = queryFactory
                .selectFrom(product)
                .leftJoin(product.discount, discount).fetchJoin()
                .leftJoin(product.productImages, productImage).fetchJoin()
                .where(
                        keywordLike(cond.getSearchKey(), cond.getKeyword()),
                        categoryEq(Category.of(cond.getCategory())),
                        imageTypeDisplay()
                )
                .orderBy(product.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(product.count())
                .where(
                        keywordLike(cond.getSearchKey(), cond.getKeyword()),
                        categoryEq(Category.of(cond.getCategory()))
                )
                .from(product);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    /**
     * 할인 등록 팝업창 내 상품 검색
     *
     * - 검색 조건: 카테고리, 키워드
     *
     * @param condition 검색 조건
     * @param pageable  페이징 정보
     * @return 검색된 상품 요약 정보 DTO Page 객체
     */
    @Override
    public Page<AdminProductSummaryDto> searchProductsInDiscountPopup(AdminProductPopupSearchCondition condition, Pageable pageable) {
        List<AdminProductSummaryDto> content = queryFactory.select(new QAdminProductSummaryDto(
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
                        categoryEq(condition.getCategory()),
                        product.id.notIn(condition.getSelectedProductIds())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(product.count())
                .from(product)
                .where(
                        nameLike(condition.getKeyword()),
                        categoryEq(condition.getCategory()),
                        product.id.notIn(condition.getSelectedProductIds())
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    /**
     * 검색어 조건
     */
    private BooleanExpression keywordLike(String searchKey, String keyword) {
        if (keyword != null) {
            if (searchKey.equals("name")) {
                return nameLike(keyword);
            }
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
     * 이미지 타입이 DISPLAY인 이미지 필터링 조건
     */
    private BooleanExpression imageTypeDisplay() {
        return productImage != null ? productImage.imageType.eq(ImageType.DISPLAY) : null;
    }
}
