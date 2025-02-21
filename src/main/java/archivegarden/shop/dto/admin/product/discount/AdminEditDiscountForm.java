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
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class AdminEditDiscountForm {

    private Long id;

    @NotBlank(message = "할인명을 입력해 주세요.")
    @Size(max = 50, message = "50자까지 입력 가능합니다.")
    private String name;

    @NotNull(message = "할인율을 입력해 주세요.")
    @Range(min = 1, max = 100, message = "할인율은 1부터 100사이의 값이여야 합니다.")
    private Integer discountPercent;

    @NotNull(message = "시작 날짜를 지정해 주세요.")
    @FutureOrPresent(message = "시작날짜가 현재 또는 미래의 날짜여야 합니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotBlank(message = "시작 시간을 지정해 주세요.")
    private String startHour;

    @NotBlank(message = "시작 시간을 지정해 주세요.")
    private String startMin;

    @NotNull(message = "종료 날짜를 지정해 주세요.")
    @FutureOrPresent(message = "종료일시가 현재 또는 미래의 날짜여야 합니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expireDate;

    @NotBlank(message = "시작 시간을 지정해 주세요.")
    private String expireHour;

    @NotBlank(message = "시작 시간을 지정해 주세요.")
    private String expireMin;

    public AdminEditDiscountForm(Discount discount) {
        this.id = discount.getId();
        this.name = discount.getName();
        this.discountPercent = discount.getDiscountPercent();

        LocalDateTime startedAt = discount.getStartedAt();
        this.startDate = startedAt.toLocalDate();
        this.startHour = String.format("%02d", startedAt.getHour());
        this.startMin = String.format("%02d", startedAt.getMinute());

        LocalDateTime expiredAt = discount.getExpiredAt();
        this.expireDate = expiredAt.toLocalDate();
        this.expireHour = String.format("%02d", expiredAt.getHour());
        this.expireMin = String.format("%02d", expiredAt.getMinute());
    }

    public LocalDateTime getStartDateTime() {
        int hour = Integer.parseInt(this.startHour);
        int minute = Integer.parseInt(this.startMin);
        return this.startDate.atTime(hour, minute);
    }

    public LocalDateTime getExpireDateTime() {
        int hour = Integer.parseInt(this.expireHour);
        int minute = Integer.parseInt(this.expireMin);
        return this.expireDate.atTime(hour, minute);
    }
}
