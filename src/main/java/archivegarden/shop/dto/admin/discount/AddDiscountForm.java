package archivegarden.shop.dto.admin.discount;

import archivegarden.shop.entity.DiscountType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddDiscountForm {

    @NotNull(message = "할인 방식을 선택해 주세요.")
    DiscountType type;

    @NotNull(message = "할인 설정을 입력해 주세요.")
    Integer value;
}
