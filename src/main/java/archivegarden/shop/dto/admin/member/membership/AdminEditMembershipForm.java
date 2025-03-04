package archivegarden.shop.dto.admin.member.membership;

import archivegarden.shop.entity.Membership;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdminEditMembershipForm {

    private Short id;

    @NotBlank(message = "회원 등급명을 입력해 주세요.")
    private String name;

    @NotNull(message = "적립률을 입력해 주세요.")
    private Integer pointRate;

    private Integer maxBenefitPoint;

    @NotNull(message = "해당 등급을 유지하기 위한 최소 소비 금액을 입력해 주세요.")
    private Integer minAmountSpent;

    public AdminEditMembershipForm(Membership membership) {
        this.id = membership.getId();
        this.name = membership.getName();
        this.pointRate = membership.getPointRate();
        this.maxBenefitPoint = membership.getMaxBenefitPoint();
        this.minAmountSpent = membership.getMinAmountSpent();
    }
}
