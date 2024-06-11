package archivegarden.shop.repository.admin.admin;

import archivegarden.shop.dto.admin.admin.AdminListDto;
import archivegarden.shop.dto.admin.admin.AdminSearchForm;
import archivegarden.shop.dto.admin.admin.QAdminListDto;
import com.querydsl.core.types.dsl.BooleanExpression;
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
import java.time.format.DateTimeFormatter;
import java.util.List;

import static archivegarden.shop.entity.QAdmin.admin;

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
                .orderBy(admin.createdAt.desc())
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

    private BooleanExpression searchDateBetween(String startDate, String endDate) {

        if (StringUtils.hasText(startDate) && StringUtils.hasText(endDate)) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDateTime startDateTime = LocalDate.parse(startDate, dateTimeFormatter).atStartOfDay();
            LocalDateTime endDateTime = LocalDate.parse(endDate, dateTimeFormatter).atTime(LocalTime.MAX);
            return admin.createdAt.between(startDateTime, endDateTime);
        }

        return null;
    }
}
