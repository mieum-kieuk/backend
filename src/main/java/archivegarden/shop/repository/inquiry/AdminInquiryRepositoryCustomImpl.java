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

    @Override
    public Optional<AdminInquiryDetailsDto> findInquiryInAdmin(Long inquiryId) {
        AdminInquiryDetailsDto result = queryFactory.select(new QAdminInquiryDetailsDto(
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
                .leftJoin(product.productImages, productImage)
                .leftJoin(inquiry.member, member)
                .where(inquiry.id.eq(inquiryId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Page<AdminInquiryListDto> findInquiriesInAdmin(AdminProductSearchCondition condition, Pageable pageable) {
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
                .leftJoin(product.productImages, productImage)
                .leftJoin(inquiry.member, member)
                .where(
                        nameLike(condition.getKeyword()),
                        categoryEq(Category.of(condition.getCategory())),
                        imageTypeDisplay()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(product.count())
                .where(
                        nameLike(condition.getKeyword()),
                        categoryEq(Category.of(condition.getCategory()))
                )
                .from(product);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    /**
     * 카테고리
     */
    private BooleanExpression categoryEq(Category category) {
        return category != null ? product.category.eq(category) : null;
    }

    /**
     * 상품명 검색
     * 공백 제거, 대소문자 구분 안함
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
     * IMAGE TYPE = DISPLAY
     */
    private BooleanExpression imageTypeDisplay() {
        return productImage != null ? productImage.imageType.eq(ImageType.DISPLAY) : null;
    }
}
