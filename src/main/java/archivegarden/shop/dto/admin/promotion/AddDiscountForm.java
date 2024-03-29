package archivegarden.shop.dto.admin.promotion;

import archivegarden.shop.entity.DiscountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AddDiscountForm {

    @NotBlank(message = "할인명을 입력해 주세요.")
    @Size(max = 30, message = "30자까지 입력 가능합니다.")
    String name;

    @NotNull(message = "할인 방식을 선택해 주세요.")
    DiscountType type;

    @NotNull(message = "할인율 또는 할인 금액을 입력해 주세요.")
    Integer value;

    public AddDiscountForm() {
        type = DiscountType.RATE;
    }
}
