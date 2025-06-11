package archivegarden.shop.repository.admin;

import archivegarden.shop.dto.admin.AdminSearchCondition;
import archivegarden.shop.dto.admin.admin.AdminListDto;
import archivegarden.shop.dto.admin.admin.QAdminListDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
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

public class AdminAdminRepositoryCustomImpl implements AdminAdminRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public AdminAdminRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    /**
     * 관리자 목록을 조건에 따라 검색하고 페이징 처리하여 반환합니다.
     *
     * 검색 조건:
     * - 키워드(로그인 아이디, 이름, 이메일)
     * - 생성일 범위
     *
     * 정렬 기준:
     * - 미승인 → 승인 순서
     * - 미승인 관리자: createdAt 오름차순
     * - 승인된 관리자: createdAt 내림차순
     *
     * @param cond 검색 조건
     * @param pageable 페이징 정보
     * @return 검색 조건에 맞는 관리자 목록을 담은 Page 객체
     */
    @Override
    public Page<AdminListDto> findAdmins(AdminSearchCondition cond, Pageable pageable) {
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
                        keywordLike(cond.getSearchKey(), cond.getKeyword()),
                        searchDateBetween(cond.getStartDate(), cond.getEndDate())
                )
                .orderBy(
                        new CaseBuilder()
                                .when(admin.isAuthorized.isFalse()).then(0)
                                .when(admin.isAuthorized.isTrue()).then(1)
                                .otherwise(2).asc(),
                        new CaseBuilder()
                                .when(admin.isAuthorized.isFalse()).then(admin.createdAt)
                                .otherwise(LocalDateTime.now()).asc(),
                        new CaseBuilder()
                                .when(admin.isAuthorized.isTrue()).then(admin.createdAt)
                                .otherwise(LocalDateTime.now()).desc()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(admin.count())
                .from(admin)
                .where(
                        keywordLike(cond.getSearchKey(), cond.getKeyword()),
                        searchDateBetween(cond.getStartDate(), cond.getEndDate())
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression keywordLike(String searchKey, String keyword) {
        if (StringUtils.hasText(keyword)) {
            if (searchKey.equals("loginId")) {
                return admin.loginId.containsIgnoreCase(keyword);
            } else if (searchKey.equals("name")) {
                return admin.name.containsIgnoreCase(keyword);
            } else if (searchKey.equals("email")) {
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
}
