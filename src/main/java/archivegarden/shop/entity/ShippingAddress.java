package archivegarden.shop.entity;

import archivegarden.shop.dto.mypage.address.AddAddressForm;
import archivegarden.shop.dto.mypage.address.EditAddressForm;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "SHIPPING_ADDRESS")
public class ShippingAddress extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shipping_address_id")
    private Long id;

    @Column(name = "shipping_address_name")
    private String shippingAddressName;

    @Column(name = "recipient_name")
    private String recipientName;

    @Embedded
    private Address address;

    @Column(name = "phone_number")
    private String phonenumber;

    @Column(name = "is_default_address")
    private String isDefaultAddress;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    //==비즈니스 로직==//
    /**
     * 배송지 주소 수정
     */
    public void update(EditAddressForm form) {
        this.shippingAddressName = form.getAddressName();
        this.recipientName = form.getRecipientName();
        this.address = new Address(form.getZipCode(), form.getBasicAddress(), form.getDetailAddress());
        this.phonenumber = form.getPhonenumber1() + "-" + form.getPhonenumber2() + "-" + form.getPhonenumber3();
        this.isDefaultAddress = form.getIsDefaultAddress().toString().toUpperCase();
    }

    //==연관관계 메서드==//
    private void setMember(Member member) {
        this.member = member;
        member.getShippingAddressList().add(this);
    }

    /**
     * 기본 배송지 제거
     */
    public void removeDefault() {
        this.isDefaultAddress = "FALSE";
    }

    //==생성 메서드==//
    /**
     * 회원가입할 때 주소 입력하는 경우 호출
     */
    public static ShippingAddress createShippingAddressWhenJoin(Member member, String zipCode, String basicAddress, String detailAddress) {
        ShippingAddress shippingAddress = new ShippingAddress();
        shippingAddress.setMember(member);
        shippingAddress.shippingAddressName = "미지정";
        shippingAddress.recipientName = member.getName();
        shippingAddress.address = new Address(zipCode, basicAddress, detailAddress);
        shippingAddress.phonenumber = member.getPhonenumber();
        shippingAddress.isDefaultAddress = Boolean.toString(true).toUpperCase();
        return shippingAddress;
    }

    /**
     * 배송지 관리페이지에서 배송지 등록할 때 호출
     */
    public static ShippingAddress createShippingAddress(AddAddressForm form, Member member) {
        ShippingAddress shippingAddress = new ShippingAddress();
        shippingAddress.setMember(member);
        shippingAddress.shippingAddressName = form.getAddressName();
        shippingAddress.recipientName = form.getRecipientName();
        shippingAddress.address = new Address(form.getZipCode(), form.getBasicAddress(), form.getDetailAddress());
        shippingAddress.phonenumber = form.getPhonenumber1() + "-" + form.getPhonenumber2() + "-" + form.getPhonenumber3();
        shippingAddress.isDefaultAddress = Boolean.toString(form.getIsDefaultAddress()).toUpperCase();
        return shippingAddress;
    }
}
