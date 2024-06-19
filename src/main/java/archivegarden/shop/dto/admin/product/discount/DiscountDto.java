package archivegarden.shop.dto.admin.promotion;

import archivegarden.shop.entity.Discount;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class DiscountDto {

    private Long id;
    private String name;
    private int discountPercent;
    private String createdAt;
    private String expiredAt;

    public DiscountDto(Discount discount) {
        this.id = discount.getId();
        this.name = discount.getName();
        this.discountPercent = discount.getDiscountPercent();
        this.createdAt = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초").format(discount.getCreatedAt());
        this.expiredAt = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초").format(discount.getExpiredAt());
    }
}
