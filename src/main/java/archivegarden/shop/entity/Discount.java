package archivegarden.shop.entity;

import archivegarden.shop.dto.admin.promotion.AddDiscountForm;
import archivegarden.shop.dto.admin.promotion.EditDiscountForm;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "discount_id")
    private Long id;

    @Column(length = 30, nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private DiscountType type;

    @Column(nullable = false)
    private int value;

    //==비즈니스 로직==//
    /**
     * 할인 혜택 수정
     */
    public void update(EditDiscountForm form) {
        this.name = form.getName();
        this.type = form.getType();
        this.value = form.getValue();
    }

    //==생성자 메서드==//
    public static Discount createDiscount(AddDiscountForm form) {
        Discount discount = new Discount();
        discount.name = form.getName();
        discount.type = form.getType();
        discount.value = form.getValue();
        return discount;
    }
}
