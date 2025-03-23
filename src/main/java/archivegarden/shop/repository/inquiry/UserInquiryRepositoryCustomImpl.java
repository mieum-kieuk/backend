package archivegarden.shop.repository.inquiry;

import archivegarden.shop.dto.user.community.inquiry.*;
import archivegarden.shop.entity.ImageType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static archivegarden.shop.entity.QAnswer.answer;
import static archivegarden.shop.entity.QInquiry.inquiry;
import static archivegarden.shop.entity.QMember.member;
import static archivegarden.shop.entity.QProduct.product;
import static archivegarden.shop.entity.QProductImage.productImage;

public class UserInquiryRepositoryCustomImpl implements UserInquiryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public UserInquiryRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    /**
     * DISPLAY 이미지 필터링
     */
    private BooleanExpression imageTypeEqDisplay() {
        return productImage.imageType.eq(ImageType.DISPLAY);
    }

    private BooleanExpression inquiryIdEq(Long inquiryId) {
        return inquiryId != null ? inquiry.id.eq(inquiryId) : null;
    }

    private BooleanExpression productIdEq(Long productId) {
        return productId != null ? inquiry.product.id.eq(productId) : null;
    }

    private BooleanExpression memberIdEq(Long memberId) {
        return memberId != null ? inquiry.member.id.eq(memberId) : null;
    }

    @Override
    public InquiryDetailsDto findInquiry(Long inquiryId) {
        return queryFactory.select(new QInquiryDetailsDto(
                        inquiry,
                        member.loginId,
                        product.id,
                        product.name,
                        product.price,
                        productImage.imageUrl,
                        answer.content,
                        answer.createdAt
                ))
                .from(inquiry)
                .leftJoin(inquiry.member, member)
                .leftJoin(inquiry.product, product)
                .leftJoin(product.productImages, productImage).on(productImage.product.eq(product).and(imageTypeEqDisplay()))
                .leftJoin(inquiry.answer, answer)
                .where(inquiryIdEq(inquiryId))
                .fetchOne();
    }

    @Override
    public Page<InquiryListDto> findInquiries(Pageable pageable) {
        List<InquiryListDto> content = queryFactory
                .select(new QInquiryListDto(
                        inquiry.id,
                        inquiry.title,
                        inquiry.isSecret,
                        inquiry.isAnswered,
                        inquiry.createdAt,
                        member.loginId,
                        product.id,
                        productImage.imageUrl
                ))
                .from(inquiry)
                .leftJoin(inquiry.member, member)
                .leftJoin(inquiry.product, product)
                .leftJoin(product.productImages, productImage).on(productImage.product.eq(product).and(imageTypeEqDisplay()))
                .orderBy(inquiry.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(inquiry.count())
                .from(inquiry);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<ProductPageInquiryListDto> findInquiriesByProductId(Long productId, Pageable pageable) {
        List<ProductPageInquiryListDto> content = queryFactory
                .select(new QProductPageInquiryListDto(
                        inquiry.id,
                        inquiry.title,
                        inquiry.content,
                        inquiry.isSecret,
                        inquiry.isAnswered,
                        inquiry.createdAt,
                        member.loginId,
                        answer.content
                ))
                .from(inquiry)
                .leftJoin(inquiry.member, member)
                .leftJoin(inquiry.product, product)
                .leftJoin(inquiry.answer, answer)
                .where(productIdEq(productId))
                .orderBy(inquiry.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(inquiry.count())
                .from(inquiry)
                .where(productIdEq(productId));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<MyPageInquiryListDto> findMyInquiries(Long memberId, Pageable pageable) {
        List<MyPageInquiryListDto> content = queryFactory
                .select(new QMyPageInquiryListDto(
                        inquiry.id,
                        inquiry.title,
                        inquiry.content,
                        inquiry.isSecret,
                        inquiry.createdAt,
                        product.id,
                        productImage.imageUrl,
                        answer.content,
                        answer.createdAt
                ))
                .from(inquiry)
                .leftJoin(inquiry.member, member)
                .leftJoin(inquiry.product, product)
                .leftJoin(product.productImages, productImage).on(productImage.product.eq(product).and(imageTypeEqDisplay()))
                .leftJoin(inquiry.answer, answer)
                .where(memberIdEq(memberId))
                .orderBy(inquiry.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(inquiry.count())
                .from(inquiry)
                .where(memberIdEq(memberId));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public EditInquiryForm findInquiryForEdit(Long inquiryId) {
        return queryFactory
                .select(new QEditInquiryForm(
                        inquiry.title,
                        inquiry.content,
                        inquiry.isSecret,
                        product.id,
                        product.name,
                        product.price,
                        productImage.imageUrl
                ))
                .from(inquiry)
                .leftJoin(inquiry.member, member)
                .leftJoin(inquiry.product, product)
                .leftJoin(productImage).on(productImage.product.eq(product).and(imageTypeEqDisplay()))
                .where(inquiryIdEq(inquiryId))
                .fetchOne();
    }
}
