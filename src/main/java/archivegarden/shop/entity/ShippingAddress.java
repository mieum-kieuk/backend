package archivegarden.shop.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "SHIPPING_ADDRESS")
public class ShippingAddress extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shipping_address_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "shipping_address_name")
    private String shippingAddressName;

    @Column(name = "recipient_name")
    private String recipientName;

    private String address;

    @Column(name = "phone_number")
    private String phonenumber;

    @Column(name = "is_default_address")
    private String isDefaultAddress;

    @Enumerated(value = EnumType.STRING)
    private ShippingAddressStatus status;

    //==연관관계 메서드==//
    private void setMember(Member member) {
        this.member = member;
        member.getShippingAddressList().add(this);
    }

    //==생성 메서드==//
    public static ShippingAddress createShippingAddressWhenJoin(Member member, String address) {
        ShippingAddress shippingAddress = new ShippingAddress();
        shippingAddress.setMember(member);
        shippingAddress.shippingAddressName = "미지정";
        shippingAddress.recipientName = member.getName();
        shippingAddress.address = address;
        shippingAddress.phonenumber = member.getPhonenumber();
        shippingAddress.isDefaultAddress = Boolean.toString(true).toUpperCase();
        shippingAddress.status = ShippingAddressStatus.INACTIVE;
        return shippingAddress;
    }

}
