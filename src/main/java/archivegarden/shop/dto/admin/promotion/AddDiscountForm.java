package archivegarden.shop.dto.admin.promotion;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class AddDiscountForm {

    @NotBlank(message = "할인 혜택명을 입력해 주세요.")
    @Size(max = 30, message = "30자까지 입력 가능합니다.")
    private String name;

    @NotNull(message = "할인율을 입력해 주세요.")
    @Range(min = 1, max = 100, message = "할인율을 1부터 100사이의 값이여야 합니다.")
    private Integer discountPercent;

    @NotNull(message = "시작일시를 입력해 주세요.")
    @FutureOrPresent(message = "시작일시가 현재 또는 미래의 날짜여야 합니다.")
    private LocalDateTime startDatetime;

    @NotNull(message = "종료일시를 입력해 주세요.")
    @FutureOrPresent(message = "종료일시가 현재 또는 미래의 날짜여야 합니다.")
    private LocalDateTime endDatetime;

    @NotNull(message = "중복 할인 가능 여부를 선택해 주세요.")
    private String isDoubleDiscount;
}
