package archivegarden.shop.entity;

import archivegarden.shop.exception.NotEnoughStockException;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(nullable = false)
    private int price;

    @Column(name = "stock_quantity", nullable = false)
    private int stockQuantity;

    @Lob
    private String details;

    @Lob
    @Column(name = "size_guide")
    private String sizeGuide;

    private String thumbNail;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @Enumerated(EnumType.STRING)
    private Category category;

    //==비즈니스 로직==//
    /**
     * stock 증가
     */
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    /**
     * stock 감소
     */
    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            throw new NotEnoughStockException("재고가 부족합니다.");
        }
    }
}
