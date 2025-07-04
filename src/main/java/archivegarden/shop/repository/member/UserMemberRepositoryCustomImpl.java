package archivegarden.shop.repository.member;

import archivegarden.shop.dto.user.member.EditMemberInfoForm;
import archivegarden.shop.dto.user.member.QEditMemberInfoForm;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.Optional;

import static archivegarden.shop.entity.QDelivery.delivery;
import static archivegarden.shop.entity.QMember.member;

public class UserMemberRepositoryCustomImpl implements UserMemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public UserMemberRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    /**
     * 회원 정보 수정 화면에 필요한 회원 정보와 기본 배송지 정보를 조회
     *
     * @param memberId 회원 ID
     * @return 회원이 존재하면 Optional<EditMemberInfoForm>, 존재하지 않으면 Optional.empty()
     */
    @Override
    public Optional<EditMemberInfoForm> fetchEditMemberInfoForm(Long memberId) {
        return Optional.ofNullable(queryFactory
                .select(new QEditMemberInfoForm(
                        member,
                        delivery.address
                ))
                .from(member)
                .join(delivery)
                .on(delivery.member.eq(member).and(delivery.isDefaultDelivery.isTrue()))
                .where(member.id.eq(memberId))
                .fetchOne());
    }
}
