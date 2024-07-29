package archivegarden.shop.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
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

    public static OrderProduct createOrderProduct(int count, Product product) {
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.count = count;
        orderProduct.product = product;
        return orderProduct;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
