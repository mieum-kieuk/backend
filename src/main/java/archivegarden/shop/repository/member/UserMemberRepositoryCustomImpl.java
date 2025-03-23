package archivegarden.shop.repository.member;

import archivegarden.shop.dto.user.member.EditMemberInfoForm;
import archivegarden.shop.dto.user.member.QEditMemberInfoForm;
import archivegarden.shop.entity.Member;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import static archivegarden.shop.entity.QDelivery.delivery;
import static archivegarden.shop.entity.QMember.member;

public class UserMemberRepositoryCustomImpl implements UserMemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public UserMemberRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public EditMemberInfoForm findByIdWithDefaultDelivery(Long memberId) {
        return queryFactory
                .select(new QEditMemberInfoForm(
                    member,
                    delivery.address
                ))
        .from(member)
                .join(delivery)
                .on(delivery.member.eq(member).and(delivery.isDefaultDelivery.isTrue()))
                .where(member.id.eq(memberId))
                .fetchOne();
    }
}
