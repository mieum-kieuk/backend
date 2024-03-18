package archivegarden.shop.dto.admin.promotion;

import archivegarden.shop.entity.Discount;
import archivegarden.shop.entity.DiscountType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DiscountDto {

    private Long id;
    private String name;
    private DiscountType type;
    private int value;

    public DiscountDto(Discount discount) {
        id = discount.getId();
        name = discount.getName();
        type = discount.getType();
        value = discount.getValue();
    }
}
