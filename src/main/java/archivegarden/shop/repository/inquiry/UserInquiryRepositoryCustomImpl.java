package archivegarden.shop.repository.inquiry;

import archivegarden.shop.dto.user.community.inquiry.*;
import archivegarden.shop.entity.ImageType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

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

    @Override
    public InquiryDetailsDto findInquiry(Long inquiryId) {
        return queryFactory.select(new QInquiryDetailsDto(
                        inquiry,
                        member.name,
                        member.loginId,
                        product.id,
                        product.name,
                        product.price,
                        productImage.imageUrl
                ))
                .from(inquiry)
                .leftJoin(inquiry.member, member)
                .leftJoin(inquiry.product, product)
                .leftJoin(productImage).on(productImage.product.eq(inquiry.product))
                .where(
                        inquiryIdEq(inquiryId),
                        imageTypeEqDisplay()
                )
                .fetchOne();
    }

    @Override
    public Page<InquiryListDto> findInquiries(Pageable pageable) {
        List<InquiryListDto> content = queryFactory.select(new QInquiryListDto(
                        inquiry.id,
                        inquiry.title,
                        inquiry.isSecret,
                        inquiry.isAnswered,
                        inquiry.createdAt,
                        member.name,
                        member.loginId,
                        product.id,
                        productImage.imageUrl
                ))
                .from(inquiry)
                .leftJoin(inquiry.member, member)
                .leftJoin(inquiry.product, product)
                .leftJoin(product.productImages, productImage)
                .where(imageTypeEqDisplay())
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
    public Page<InquiryListInProductDto> findInquiriesByProductId(Long productId, Pageable pageable) {
        List<InquiryListInProductDto> content = queryFactory.select(new QInquiryListInProductDto(
                        inquiry.id,
                        inquiry.title,
                        inquiry.isSecret,
                        inquiry.isAnswered,
                        inquiry.createdAt,
                        member.name,
                        member.loginId,
                        product.id,
                        productImage.imageUrl,
                        answer.content
                ))
                .from(inquiry)
                .leftJoin(inquiry.member, member)
                .leftJoin(inquiry.product, product)
                .leftJoin(product.productImages, productImage)
                .join(inquiry.answer, answer)
                .where(
                        inquiry.product.id.eq(productId),
                        imageTypeEqDisplay()
                )
                .orderBy(inquiry.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(inquiry.count())
                .from(inquiry)
                .where(inquiry.product.id.eq(productId));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public EditInquiryForm findInquiryForEdit(Long inquiryId) {
        return queryFactory.select(new QEditInquiryForm(
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
                .leftJoin(productImage).on(productImage.product.eq(inquiry.product))
                .where(
                        inquiryIdEq(inquiryId),
                        imageTypeEqDisplay()
                )
                .fetchOne();
    }


    private BooleanExpression keywordLike(String searchKey, String keyword) {
        if (StringUtils.hasText(keyword)) {
            if (searchKey.equals("title")) {
                return Expressions.stringTemplate("function('replace', {0},{1},{2})", inquiry.title, " ", "")
                        .containsIgnoreCase(StringUtils.replace(keyword, " ", ""));
            } else if (searchKey.equals("writer")) {
                return inquiry.member.name.containsIgnoreCase(keyword);
            }
        }

        return null;
    }

    private BooleanExpression inquiryIdEq(Long inquiryId) {
        return inquiry.id.eq(inquiryId);
    }

    private BooleanExpression imageTypeEqDisplay() {
        return productImage.imageType.eq(ImageType.DISPLAY);
    }
}
