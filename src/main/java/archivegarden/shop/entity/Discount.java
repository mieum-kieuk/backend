package archivegarden.shop.entity;

import archivegarden.shop.dto.admin.discount.AddDiscountForm;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "discount_id")
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private DiscountType type;

    private int value;

    //==생성자 메서드==//
    public static Discount createDiscount(AddDiscountForm form) {
        Discount discount = new Discount();
        discount.type = form.getType();
        discount.value = form.getValue();
        return discount;
    }
}
