package archivegarden.shop.entity;

import archivegarden.shop.dto.admin.product.discount.AdminAddDiscountForm;
import archivegarden.shop.dto.admin.product.discount.AdminEditDiscountForm;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Discount extends BaseTimeEntity {

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

    @OneToMany(mappedBy = "discount")
    private List<Product> products = new LinkedList<>();  //양방향

    public static Discount createDiscount(AdminAddDiscountForm form, List<Product> products) {
        Discount discount = new Discount();
        discount.name = form.getName();
        discount.discountPercent = form.getDiscountPercent();
        discount.startedAt = form.getStartDateTime();
        discount.expiredAt = form.getExpireDateTime();
        for (Product product : products) {
            discount.products.add(product);
            product.setDiscount(discount);
        }
        return discount;
    }

    /**
     * 할인 수정
     */
    public void update(AdminEditDiscountForm form) {
        this.name = form.getName();
        this.discountPercent = form.getDiscountPercent();
        this.startedAt = form.getStartDateTime();
        this.expiredAt = form.getExpireDateTime();
    }
}
