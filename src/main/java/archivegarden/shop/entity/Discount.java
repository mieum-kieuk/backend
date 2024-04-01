package archivegarden.shop.entity;

import archivegarden.shop.dto.admin.promotion.AddDiscountForm;
import archivegarden.shop.dto.admin.promotion.EditDiscountForm;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "discount_id")
    private Long id;

    @Column(length = 30, nullable = false)
    private String name;

    @Column(name = "discount_percent", nullable = false)
    private int discountPercent;

    @Column(name = "start_datetime", nullable = false)
    private LocalDateTime startDatetime;

    @Column(name = "end_datetime", nullable = false)
    private LocalDateTime endDatetime;

    @Column(name = "is_double_discount", nullable = false)
    private String isDoubleDiscount;

    //==비즈니스 로직==//
    /**
     * 할인 혜택 수정
     */
    public void update(EditDiscountForm form) {
        this.name = form.getName();
        this.discountPercent = form.getDiscountPercent();
        this.startDatetime = form.getStartDatetime();
        this.endDatetime = form.getEndDatetime();
        this.isDoubleDiscount = form.getIsDoubleDiscount();
    }

    //==생성자 메서드==//
    public static Discount createDiscount(AddDiscountForm form) {
        Discount discount = new Discount();
        discount.name = form.getName();
        discount.discountPercent = form.getDiscountPercent();
        discount.startDatetime = form.getStartDatetime();
        discount.endDatetime = form.getEndDatetime();
        discount.isDoubleDiscount = form.getIsDoubleDiscount();
        return discount;
    }
}
