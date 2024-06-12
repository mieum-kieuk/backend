package archivegarden.shop.repository.community;

import archivegarden.shop.dto.community.inquiry.ProductInquiryDetailsDto;
import archivegarden.shop.dto.community.inquiry.ProductInquiryListDto;
import archivegarden.shop.dto.community.inquiry.QProductInquiryDetailsDto;
import archivegarden.shop.dto.community.inquiry.QProductInquiryListDto;
import archivegarden.shop.entity.ImageType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static archivegarden.shop.entity.QMember.member;
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

    private BooleanExpression inquiryIdEq(Long inquiryId) {
        return productInquiry.id.eq(inquiryId);
    }

    private BooleanExpression imageTypeEqDisplay() {
        return productImage.imageType.eq(ImageType.DISPLAY);
    }
}
