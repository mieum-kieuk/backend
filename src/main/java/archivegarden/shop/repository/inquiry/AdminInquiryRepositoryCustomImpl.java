package archivegarden.shop.repository.inquiry;

import archivegarden.shop.dto.admin.product.inquiry.AdminInquiryDetailsDto;
import archivegarden.shop.dto.admin.product.inquiry.AdminInquiryListDto;
import archivegarden.shop.dto.admin.product.inquiry.QAdminInquiryDetailsDto;
import archivegarden.shop.dto.admin.product.inquiry.QAdminInquiryListDto;
import archivegarden.shop.dto.admin.product.product.AdminProductSearchCondition;
import archivegarden.shop.entity.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

import static archivegarden.shop.entity.QInquiry.inquiry;
import static archivegarden.shop.entity.QMember.member;
import static archivegarden.shop.entity.QProduct.product;
import static archivegarden.shop.entity.QProductImage.productImage;
import static org.springframework.util.StringUtils.hasText;

public class AdminInquiryRepositoryCustomImpl implements AdminInquiryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public AdminInquiryRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    /**
     * 관리자 상품 문의 상세 조회
     *
     * @param inquiryId 조회할 상품 문의 ID
     * @return AdminInquiryDetailsDto, 존재하지 않으면 Optional.empty()
     */
    @Override
    public Optional<AdminInquiryDetailsDto> findInquiryInAdmin(Long inquiryId) {
        AdminInquiryDetailsDto result =
                queryFactory.select(new QAdminInquiryDetailsDto(
                        inquiry.id,
                        inquiry.title,
                        inquiry.content,
                        inquiry.createdAt,
                        member.loginId,
                        product.id,
                        product.name,
                        product.price,
                        productImage.imageUrl
                ))
                .from(inquiry)
                .leftJoin(inquiry.product, product)
                .leftJoin(product.productImages, productImage).on(imageTypeDisplay())
                .leftJoin(inquiry.member, member)
                .where(inquiry.id.eq(inquiryId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    /**
     * 관리자 상품 문의 목록 조회
     *
     * - 검색 조건: 상품명(keyword), 카테고리(category)
     *
     * @param cond     검색 조건
     * @param pageable 페이징 정보
     * @return AdminInquiryListDto Page 객체
     */
    @Override
    public Page<AdminInquiryListDto> findInquiriesInAdmin(AdminProductSearchCondition cond, Pageable pageable) {
        List<AdminInquiryListDto> content = queryFactory
                .select(new QAdminInquiryListDto(
                        inquiry.id,
                        inquiry.title,
                        inquiry.createdAt,
                        inquiry.isAnswered,
                        member.loginId,
                        product.id,
                        productImage.imageUrl
                )).from(inquiry)
                .leftJoin(inquiry.product, product)
                .leftJoin(product.productImages, productImage).on(imageTypeDisplay())
                .leftJoin(inquiry.member, member)
                .where(
                        nameLike(cond.getKeyword()),
                        categoryEq(Category.of(cond.getCategory()))
                )
                .orderBy(inquiry.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(inquiry.count())
                .where(
                        nameLike(cond.getKeyword()),
                        categoryEq(Category.of(cond.getCategory()))
                )
                .from(inquiry);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
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
