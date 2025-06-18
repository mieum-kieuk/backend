package archivegarden.shop.dto.admin.member.membership;

import archivegarden.shop.entity.Membership;
import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;

@Getter
@Setter
public class AdminMembershipDto {

    private Long id;
    private String name;
    private String pointRate;
    private Integer level;
    private boolean isDefault;
    private String maxBenefitPoint;
    private String minAmountSpent;

    public AdminMembershipDto(Membership membership) {
        this.id = membership.getId();
        this.name = membership.getName();
        this.pointRate = membership.getPointRate() + "%";
        this.level = membership.getLevel();
        this.isDefault = membership.isDefault();
        if(membership.getMaxBenefitPoint() != null) {
            this.maxBenefitPoint = new DecimalFormat("###,###원").format(membership.getMaxBenefitPoint());
        }
        this.minAmountSpent = new DecimalFormat("###,###,###원").format(membership.getMinAmountSpent());
    }
}
