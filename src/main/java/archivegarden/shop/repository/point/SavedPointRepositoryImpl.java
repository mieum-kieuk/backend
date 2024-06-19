package archivegarden.shop.repository.point;

import archivegarden.shop.dto.admin.AdminSearchForm;
import archivegarden.shop.dto.admin.member.QSavedPointListDto;
import archivegarden.shop.dto.admin.member.SavedPointListDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.JPQLQueryFactory;
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

import static archivegarden.shop.entity.QMember.member;
import static archivegarden.shop.entity.QSavedPoint.savedPoint;

public class SavedPointRepositoryImpl implements SavedPointRepositoryCustom {

    private final JPQLQueryFactory queryFactory;

    public SavedPointRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<SavedPointListDto> findDtoAll(AdminSearchForm form, Pageable pageable) {
        List<SavedPointListDto> content = queryFactory.select(new QSavedPointListDto(
                    member.name,
                    member.loginId,
                    member.grade,
                    savedPoint.type,
                    savedPoint.createdAt,
                    savedPoint.expiredAt,
                    savedPoint.amount,
                    savedPoint.balance
                ))
                .from(savedPoint)
                .leftJoin(savedPoint.member, member)
                .where(
                        keywordLike(form.getSearchKey(), form.getKeyword()),
                        searchDateBetween(form.getStartDate(), form.getEndDate())
                )
                .orderBy(savedPoint.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPQLQuery<Long> countquery = queryFactory
                .select(savedPoint.count())
                .from(savedPoint);

        return PageableExecutionUtils.getPage(content, pageable, countquery::fetchOne);
    }

    private BooleanExpression keywordLike(String searchKey, String keyword) {
        if (StringUtils.hasText(keyword)) {
            if (searchKey.equals("loginId")) {
                return member.loginId.containsIgnoreCase(keyword);
            } else if (searchKey.equals("name")) {
                return member.name.containsIgnoreCase(keyword);
            }
        }

        return null;
    }

    private BooleanExpression searchDateBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null) {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
            return savedPoint.createdAt.between(startDateTime, endDateTime);
        }

        return null;
    }
}
