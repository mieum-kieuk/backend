package archivegarden.shop.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Table(name = "ORDER_PRODUCT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_product_id")
    private Long id;

    @Column(nullable = false)
    private int count;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "product_id")
    private Product product;    //다대일 단방향

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_id")
    private Order order;    //다대일 양방향

    @Builder
    public OrderProduct(int count, Product product) {
        this.count = count;
        this.product = product;

        product.removeStock(count);
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
