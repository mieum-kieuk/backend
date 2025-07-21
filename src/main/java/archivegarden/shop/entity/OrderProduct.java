package archivegarden.shop.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private int quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;    //양방향

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;    //다대일 양방향

    public static OrderProduct createOrderProduct(int quantity, Product product) {
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.quantity = quantity;
        orderProduct.product = product;
        return orderProduct;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
