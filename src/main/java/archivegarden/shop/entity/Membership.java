package archivegarden.shop.entity;

import archivegarden.shop.dto.admin.member.membership.AdminAddMembershipForm;
import archivegarden.shop.dto.admin.member.membership.AdminEditMembershipForm;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Membership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "membership_id")
    private Short id;

    @Column(length = 30, nullable = false)
    private String name;

    @Column(name = "point_rate", nullable = false)
    private int pointRate;

    @Column(length = 30, nullable = false)
    private int level;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault;

    @Column(name = "max_benefit_point")
    private Integer maxBenefitPoint;

    @Column(name = "min_amount_spent", nullable = false)
    private int minAmountSpent;

    public static Membership createMembership(AdminAddMembershipForm form, int level) {
        Membership membership = new Membership();
        membership.name = form.getName();
        membership.pointRate = form.getPointRate();
        membership.level = level;
        membership.maxBenefitPoint = form.getMaxBenefitPoint();
        membership.minAmountSpent = form.getMinAmountSpent();
        return membership;
    }

    /**
     * 회원 등급 수정
     */
    public void update(AdminEditMembershipForm form) {
        this.name = form.getName();
        this.pointRate = form.getPointRate();
        this.maxBenefitPoint = form.getMaxBenefitPoint();
        this.minAmountSpent = form.getMinAmountSpent();
    }

    public boolean isDefault() {
        return isDefault;
    }
}
