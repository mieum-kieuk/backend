package archivegarden.shop.repository.productInquiry;

import archivegarden.shop.dto.admin.AdminSearchForm;
import archivegarden.shop.dto.admin.product.inquiry.ProductInquiryAdminDetailsDto;
import archivegarden.shop.dto.admin.product.inquiry.ProductInquiryAdminListDto;
import archivegarden.shop.dto.admin.product.inquiry.QProductInquiryAdminDetailsDto;
import archivegarden.shop.dto.admin.product.inquiry.QProductInquiryAdminListDto;
import archivegarden.shop.dto.community.inquiry.ProductInquiryDetailsDto;
import archivegarden.shop.dto.community.inquiry.ProductInquiryListDto;
import archivegarden.shop.dto.community.inquiry.QProductInquiryDetailsDto;
import archivegarden.shop.dto.community.inquiry.QProductInquiryListDto;
import archivegarden.shop.entity.ImageType;
import com.querydsl.core.types.Predicate;
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

import static archivegarden.shop.entity.QMember.member;
import static archivegarden.shop.entity.QNotice.notice;
import static archivegarden.shop.entity.QProduct.product;
import static archivegarden.shop.entity.QProductImage.productImage;
import static archivegarden.shop.entity.QProductInquiry.productInquiry;

public class ProductInquiryRepositoryImpl implements ProductInquiryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ProductInquiryRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public ProductInquiryDetailsDto findDto(Long inquiryId) {
        return queryFactory.select(new QProductInquiryDetailsDto(
                        productInquiry,
                        member.name,
                        member.loginId,
                        product.id,
                        product.name,
                        product.price,
                        productImage.storeImageName
                ))
                .from(productInquiry)
                .leftJoin(productInquiry.member, member)
                .leftJoin(productInquiry.product, product)
                .leftJoin(productImage).on(productImage.product.eq(productInquiry.product))
                .where(
                        inquiryIdEq(inquiryId),
                        imageTypeEqDisplay()
                )
                .fetchOne();
    }

    @Override
    public ProductInquiryAdminDetailsDto findAdminDto(Long inquiryId) {

        return queryFactory.select(new QProductInquiryAdminDetailsDto(
                        productInquiry,
                        member.name,
                        product.id,
                        product.name,
                        product.price,
                        productImage.storeImageName
                ))
                .from(productInquiry)
                .leftJoin(productInquiry.member, member)
                .leftJoin(productInquiry.product, product)
                .leftJoin(productImage).on(productImage.product.eq(productInquiry.product))
                .where(
                        inquiryIdEq(inquiryId),
                        imageTypeEqDisplay()
                )
                .fetchOne();
    }

    @Override
    public Page<ProductInquiryListDto> findDtoAll(Pageable pageable) {
        List<ProductInquiryListDto> content = queryFactory.select(new QProductInquiryListDto(
                        productInquiry.id,
                        productInquiry.title,
                        productInquiry.isSecret,
                        productInquiry.isAnswered,
                        productInquiry.createdAt,
                        member.name,
                        member.loginId,
                        productImage.product.id,
                        productImage.storeImageName
                ))
                .from(productInquiry)
                .leftJoin(productInquiry.member, member)
                .leftJoin(productImage).on(productImage.product.eq(productInquiry.product))
                .where(imageTypeEqDisplay())
                .orderBy(productInquiry.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(productInquiry.count())
                .from(productInquiry);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<ProductInquiryAdminListDto> findAdminDtoAll(AdminSearchForm form, Pageable pageable) {
        List<ProductInquiryAdminListDto> content = queryFactory
                .select(new QProductInquiryAdminListDto(
                        productInquiry.id,
                        productInquiry.title,
                        member.name,
                        productInquiry.createdAt,
                        productInquiry.isAnswered
                ))
                .from(productInquiry)
                .leftJoin(productInquiry.member, member)
                .where(keywordLike(form.getSearchKey(), form.getKeyword()))
                .orderBy(productInquiry.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(productInquiry.count())
                .from(productInquiry)
                .where(keywordLike(form.getSearchKey(), form.getKeyword()));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression keywordLike(String searchKey, String keyword) {
        if (StringUtils.hasText(keyword)) {
            if (searchKey.equals("title")) {
                return Expressions.stringTemplate("function('replace', {0},{1},{2})", productInquiry.title, " ", "")
                        .containsIgnoreCase(StringUtils.replace(keyword, " ", ""));
            } else if(searchKey.equals("writer")) {
                return productInquiry.member.name.containsIgnoreCase(keyword);
            }
        }

        return null;
    }

    private BooleanExpression inquiryIdEq(Long inquiryId) {
        return productInquiry.id.eq(inquiryId);
    }

    private BooleanExpression imageTypeEqDisplay() {
        return productImage.imageType.eq(ImageType.DISPLAY);
    }
}
