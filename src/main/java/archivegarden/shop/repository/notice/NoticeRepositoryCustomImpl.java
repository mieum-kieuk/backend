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

public class NoticeRepositoryCustomImpl implements NoticeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public NoticeRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    /**
     * 사용자 화면용 공지사항을 검색 조건과 페이징 정보로 조회합니다.
     *
     * @param form     검색 폼(키워드, 검색 범위 등)
     * @param pageable 페이징 정보
     * @return 공지사항 엔티티의 페이지
     */
    @Override
    public Page<Notice> findNotices(NoticeSearchForm form, Pageable pageable) {
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

    /**
     * 관리자 페이지에서 공지사항을 페이징 조회합니다.
     *
     * @param cond     관리자 검색 조건
     * @param pageable 페이징 정보
     * @return 검색 조건에 맞는 관리자 목록을 담은 Page 객체
     */
    @Override
    public Page<Notice> findNoticesInAdmin(AdminSearchCondition cond, Pageable pageable) {
        List<Notice> content = queryFactory
                .selectFrom(notice)
                .where(keywordLike(cond.getSearchKey(), cond.getKeyword()))
                .orderBy(notice.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(notice.count())
                .where(keywordLike(cond.getSearchKey(), cond.getKeyword()))
                .from(notice);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    /**
     * 날짜 기준(주, 월, 3개월)으로 공지사항 생성일 범위를 지정합니다.
     *
     * @param searchDate "week", "month", "3month" 중 하나
     * @return 해당 기간의 BooleanExpression, 조건이 없으면 null
     */
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

    /**
     * 키워드에 따라 제목 또는 내용 필드를 LIKE 조건으로 검색합니다.
     *
     * @param searchKey "title" 또는 "content"
     * @param keyword   검색어
     * @return BooleanExpression 또는 null
     */
    private BooleanExpression keywordLike(String searchKey, String keyword) {
        if (keyword != null) {
            if (searchKey.equals("title")) {
                return Expressions.stringTemplate("function('replace',{0},{1},{2})", notice.title, " ", "")
                        .containsIgnoreCase(StringUtils.replace(keyword, " ", ""));
            } else if (searchKey.equals("content")) {
                return notice.content.contains(keyword);
            }
        }

        return null;
    }
}
