package archivegarden.shop.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ORDERS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @Column(name = "merchant_uid")
    private String merchantUid;

    @Column(name = "recipient_name", length = 12, nullable = false)
    private String recipientName;

    @Column(name = "recipient_address", length = 100, nullable = false)
    private String recipientAddress;

    @Column(name = "recipient_phone_number", length = 13, nullable = false)
    private String recipientPhonenumber;

    @Column(name = "total_amount", nullable = false)
    private int totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    private OrderStatus orderStatus;

    @Column(name = "order_at", nullable = false, updatable = false)
    private LocalDateTime orderAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;  //다대일 양방향

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderProduct> orderProducts = new ArrayList<>();

    //==연관관계 메서드==//
    private void addOrderProduct(OrderProduct orderProduct) {
        this.orderProducts.add(orderProduct);
        orderProduct.setOrder(this);
    }

    @Builder
    public Order(Member member, Delivery delivery, String merchantUid, int totalAmount, OrderStatus orderStatus, List<OrderProduct> orderProducts) {
        this.member = member;
        this.recipientName = delivery.getRecipientName();
        this.recipientPhonenumber = delivery.getPhonenumber();
        this.recipientAddress = delivery.getAddress().fullAddress();
        this.merchantUid = merchantUid;
        this.totalAmount = totalAmount;
        this.orderStatus = orderStatus;
        this.orderAt = LocalDateTime.now();
        for (OrderProduct orderProduct : orderProducts) {
            addOrderProduct(orderProduct);
        }
    }
}
