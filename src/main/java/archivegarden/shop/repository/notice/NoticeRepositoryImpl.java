package archivegarden.shop.repository.notice;

import archivegarden.shop.dto.admin.AdminSearchCondition;
import archivegarden.shop.dto.user.community.notice.NoticeSearchForm;
import archivegarden.shop.entity.Notice;
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
import java.util.List;

import static archivegarden.shop.entity.QNotice.notice;

public class NoticeRepositoryImpl implements NoticeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public NoticeRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Notice> findAllNotice(NoticeSearchForm form, Pageable pageable) {
        List<Notice> content = queryFactory
                .selectFrom(notice)
                .where(
                        keywordLike(form.getSearchKey(), form.getKeyword()),
                        searchDateBetween(form.getSearchDate())
                )
                .orderBy(notice.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(notice.count())
                .where(
                        keywordLike(form.getSearchKey(), form.getKeyword()),
                        searchDateBetween(form.getSearchDate())
                )
                .from(notice);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);

    }

    @Override
    public Page<Notice> findAllAdminNotice(AdminSearchCondition form, Pageable pageable) {
        List<Notice> content = queryFactory
                .selectFrom(notice)
                .where(keywordLike(form.getSearchKey(), form.getKeyword()))
                .orderBy(notice.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(notice.count())
                .where(keywordLike(form.getSearchKey(), form.getKeyword()))
                .from(notice);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression searchDateBetween(String searchDate) {
        if(searchDate != null) {
            LocalDate today = LocalDate.now();
            LocalDateTime now = LocalDateTime.now();
            if(searchDate.equals("week")) {
                LocalDateTime aWeekAgo = today.minusWeeks(1).atStartOfDay();
                return notice.createdAt.between(aWeekAgo, now);
            } else if(searchDate.equals("month")) {
                LocalDateTime aMonthAgo = today.minusMonths(1).atStartOfDay();
                return notice.createdAt.between(aMonthAgo, now);
            } else if(searchDate.equals("3month")) {
                LocalDateTime threeMonthAgo = today.minusMonths(3).atStartOfDay();
                return notice.createdAt.between(threeMonthAgo, now);
            }
        }

        return null;
    }

    private BooleanExpression keywordLike(String searchKey, String keyword) {
        if (keyword != null) {
            if (searchKey.equals("title")) {
                //공백제거
                return Expressions.stringTemplate("function('replace',{0},{1},{2})", notice.title, " ", "")
                        .containsIgnoreCase(StringUtils.replace(keyword, " ", ""));
            } else if (searchKey.equals("content")) {
                return notice.content.contains(keyword);
            }
        }

        return null;
    }
}
