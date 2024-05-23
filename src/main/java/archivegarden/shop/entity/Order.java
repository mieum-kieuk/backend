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

    @Column(name = "recipient_name")
    private String recipientName;

    @Column(name = "recipient_phonenumber")
    private String recipientPhonenumber;

    @Column(name = "shipping_address")
    private String shippingAddress;

    @Column(name = "merchant_uid")
    private String merchantUid;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    private OrderStatus orderStatus;

    @Column(name = "order_at", nullable = false, updatable = false)
    private LocalDateTime orderAt;

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

    private void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    @Builder
    public Order(Member member, String merchantUid, List<OrderProduct> orderProducts) {
        setMember(member);
        this.merchantUid = merchantUid;
        this.orderStatus = OrderStatus.TRY;
        this.orderAt = LocalDateTime.now();
        for (OrderProduct orderProduct : orderProducts) {
            addOrderProduct(orderProduct);
        }
    }
}
