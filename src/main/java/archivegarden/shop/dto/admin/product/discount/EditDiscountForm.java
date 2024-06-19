package archivegarden.shop.dto.admin.promotion;

import archivegarden.shop.entity.Discount;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class EditDiscountForm {

    private Long id;

    @NotBlank(message = "할인 혜택명을 입력해 주세요.")
    @Size(max = 30, message = "30자까지 입력 가능합니다.")
    private String name;

    @NotNull(message = "할인율을 입력해 주세요.")
    private Integer discountPercent;

    @NotNull(message = "시작일시를 입력해 주세요.")
    @FutureOrPresent(message = "시작일시가 현재 또는 미래의 날짜여야 합니다.")
    private LocalDateTime createdAt;

    @NotNull(message = "종료일시를 입력해 주세요.")
    @FutureOrPresent(message = "종료일시가 현재 또는 미래의 날짜여야 합니다.")
    private LocalDateTime expiredAt;

    public EditDiscountForm(Discount discount) {
        this.id = discount.getId();
        this.name = discount.getName();
        this.discountPercent = discount.getDiscountPercent();
        this.createdAt = discount.getCreatedAt();
        this.expiredAt = discount.getExpiredAt();
    }

}
