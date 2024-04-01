package archivegarden.shop.dto.admin.promotion;

import archivegarden.shop.entity.Discount;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class DiscountDetailsDto {

    private Long id;
    private String name;
    private int discountPercent;
    private String startDatetime;
    private String endDatetime;
    private String isDoubleDiscount;

    public DiscountDetailsDto(Discount discount) {
        this.id = discount.getId();
        this.name = discount.getName();
        this.discountPercent = discount.getDiscountPercent();
        this.startDatetime = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분").format(discount.getStartDatetime());
        this.endDatetime = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분").format(discount.getEndDatetime());
        this.isDoubleDiscount = discount.getIsDoubleDiscount().equals("TRUE") ? "허용" : "불가";
    }
}
