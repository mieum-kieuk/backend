package archivegarden.shop.repository.admin.promotion;

import archivegarden.shop.dto.admin.AdminSearchForm;
import archivegarden.shop.entity.Discount;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

import static archivegarden.shop.entity.QDiscount.discount;

public class AdminDiscountRepositoryImpl implements AdminDiscountRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public AdminDiscountRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Discount> findAll(AdminSearchForm form, Pageable pageable) {
        List<Discount> content = queryFactory
                .selectFrom(discount)
                .where(keywordLike(form.getSearchKey(), form.getKeyword()))
                .orderBy(discount.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(discount.count())
                .from(discount)
                .where(keywordLike(form.getSearchKey(), form.getKeyword()));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression keywordLike(String searchKey, String keyword) {
        if (StringUtils.hasText(keyword)) {
            if (searchKey.equals("name")) {
                return discount.name.containsIgnoreCase(keyword);
            } else if (searchKey.equals("percent"))
                return discount.discountPercent.eq(Integer.parseInt(keyword));
        }

        return null;
    }
}
