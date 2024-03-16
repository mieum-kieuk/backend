package archivegarden.shop.dto.admin.discount;

import archivegarden.shop.entity.DiscountType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddDiscountForm {

    @NotNull(message = "할인 방식을 선택해 주세요.")
    DiscountType type;

    @NotNull(message = "할인 설정을 입력해 주세요.")
    Integer value;
}
