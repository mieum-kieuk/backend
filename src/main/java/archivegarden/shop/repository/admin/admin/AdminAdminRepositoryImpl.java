package archivegarden.shop.repository.admin.admin;

import archivegarden.shop.dto.admin.admin.AdminListDto;
import archivegarden.shop.dto.admin.admin.AdminSearchForm;
import archivegarden.shop.dto.admin.admin.QAdminListDto;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static archivegarden.shop.entity.QAdmin.admin;
import static archivegarden.shop.entity.QProduct.product;

public class AdminAdminRepositoryImpl implements AdminAdminRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public AdminAdminRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<AdminListDto> findDtoAll(AdminSearchForm form, Pageable pageable) {
        List<AdminListDto> content = queryFactory.select(new QAdminListDto(
                        admin.id,
                        admin.name,
                        admin.loginId,
                        admin.email,
                        admin.isAuthorized,
                        admin.createdAt
                ))
                .from(admin)
                .where(
                        keywordLike(form.getSearchKey(), form.getKeyword()),
                        searchDateBetween(form.getStartDate(), form.getEndDate())
                )
                .orderBy(
                        anonymousFirst(),
                        admin.createdAt.desc()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(admin.count())
                .from(admin)
                .where(
                        keywordLike(form.getSearchKey(), form.getKeyword()),
                        searchDateBetween(form.getStartDate(), form.getEndDate())
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression keywordLike(String searchKey, String keyword) {
        if (StringUtils.hasText(keyword)) {
            if (searchKey.equals("loginId")) {
                return admin.loginId.containsIgnoreCase(keyword);
            } else if (searchKey.equals("name")) {
                return admin.name.contains(keyword);
            }else if (searchKey.equals("email")) {
                return admin.email.containsIgnoreCase(keyword);
            }
        }

        return null;
    }

    private BooleanExpression searchDateBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null) {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
            return admin.createdAt.between(startDateTime, endDateTime);
        }

        return null;
    }

    /**
     * 승인 안된 관리자 우선 조회
     */
    private OrderSpecifier<?> anonymousFirst() {
        return Expressions.stringTemplate("decode({0}, {1}, {2})", admin.isAuthorized, "TRUE", 1)
                .desc();
    }
}
