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
import java.util.Optional;

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
     * 상품 문의 상세 정보를 조회
     *
     * 조회 대상:
     * - 상품 문의(inquiry)
     * - 작성자 정보(이름, 로그인 아이디)
     * - 상품 정보(ID, 이름, 가격)
     * - 상품 대표 이미지 URL - 한 장만 조회(ImageType = DISPLAY)
     * - 답변 정보(내용, 생성일)
     *
     * @param inquiryId 조회할 상품 문의 ID
     * @return 조회된 Optional<InquiryDetailsDto> 반환, 존재하지 않을 경우 Optional.empty() 반환
     */
    @Override
    public Optional<InquiryDetailsDto> findInquiry(Long inquiryId) {
        InquiryDetailsDto inquiryDetailsDto = queryFactory.select(new QInquiryDetailsDto(
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

        return Optional.ofNullable(inquiryDetailsDto);
    }

    /**
     * 상품 문의 목록 조회
     *
     * 조회 대상:
     * - 상품 문의(inquiry)
     * - 작성자 정보(로그인 아이디)
     * - 상품 정보(ID, 이름, 가격)
     * - 상품 대표 이미지 URL - 한 장만 조회(ImageType = DISPLAY)
     *
     * @param pageable 페이징 정보
     * @return InquiryListDto Page 객체
     */
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

    /**
     * 상품 문의 수정 폼 조회
     *
     * @param inquiryId 수정할 상품 문의 ID
     * @return 조회된 Optional<EditInquiryForm> 반환, 존재하지 않을 경우 Optional.empty() 반환
     */
    @Override
    public Optional<EditInquiryForm> findInquiryForEdit(Long inquiryId) {
        EditInquiryForm editInquiryForm = queryFactory
                .select(new QEditInquiryForm(
                        inquiry.title,
                        inquiry.content,
                        inquiry.isSecret,
                        member.loginId,
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

        return Optional.ofNullable(editInquiryForm);
    }

    /**
     * 상품 상세 페이지에서 상품 문의 목록 조회
     *
     * 조회 대상:
     * - 상품 문의(inquiry)
     * - 작성자 정보(로그인 아이디)
     * - 답변 정보(내용)
     *
     * @param productId 조회할 상품 ID
     * @param pageable 페이징 정보
     * @return ProductPageInquiryListDto Page 객체
     */
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

    /**
     * 이미지 타입이 DISPLAY인 이미지 필터링 조건
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
}
