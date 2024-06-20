package archivegarden.shop.dto.admin.product.discount;

import archivegarden.shop.entity.Discount;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class DiscountListDto {

    private Long id;
    private String name;
    private String discountPercent;
    private String startedAt;
    private String expiredAt;

    public DiscountListDto(Discount discount) {
        this.id = discount.getId();
        this.name = discount.getName();
        this.discountPercent = String.valueOf(discount.getDiscountPercent()) + "%";
        this.startedAt = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초").format(discount.getStartedAt());
        this.expiredAt = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초").format(discount.getExpiredAt());
    }
}
