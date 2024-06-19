package archivegarden.shop.entity;

import archivegarden.shop.dto.admin.product.discount.AddDiscountForm;
import archivegarden.shop.dto.admin.product.discount.EditDiscountForm;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "discount_id")
    private Long id;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(name = "discount_percent", nullable = false)
    private int discountPercent;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    //==비즈니스 로직==//
    /**
     * 할인 수정
     */
    public void update(EditDiscountForm form) {
        this.name = form.getName();
        this.discountPercent = form.getDiscountPercent();
        this.startedAt = form.getStartedAt();
        this.expiredAt = form.getExpiredAt();
    }

    //==생성자 메서드==//
    public static Discount createDiscount(AddDiscountForm form) {
        Discount discount = new Discount();
        discount.name = form.getName();
        discount.discountPercent = form.getDiscountPercent();
        discount.startedAt = form.getStartedAt();
        discount.expiredAt = form.getExpiredAt();
        return discount;
    }
}
