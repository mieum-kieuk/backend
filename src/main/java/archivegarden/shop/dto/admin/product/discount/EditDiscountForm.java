package archivegarden.shop.dto.admin.product.discount;

import archivegarden.shop.entity.Discount;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class EditDiscountForm {

    private Long id;

    @NotBlank(message = "할인명을 입력해 주세요.")
    @Size(max = 50, message = "50자까지 입력 가능합니다.")
    private String name;

    @NotNull(message = "할인율을 입력해 주세요.")
    @Range(min = 1, max = 100, message = "할인율은 1부터 100사이의 값이여야 합니다.")
    private Integer discountPercent;

    @NotNull(message = "시작일시를 지정해 주세요.")
    private LocalDateTime startedAt;

    @NotNull(message = "종료일시를 지정해 주세요.")
    private LocalDateTime expiredAt;

    public EditDiscountForm(Discount discount) {
        this.id = discount.getId();
        this.name = discount.getName();
        this.discountPercent = discount.getDiscountPercent();
        this.startedAt = discount.getStartedAt();
        this.expiredAt = discount.getExpiredAt();
    }
}
