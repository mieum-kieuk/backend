package archivegarden.shop.entity;

import archivegarden.shop.dto.delivery.AddDeliveryForm;
import archivegarden.shop.dto.delivery.EditDeliveryForm;
import archivegarden.shop.dto.delivery.EditPopupDeliveryForm;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Delivery extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_id")
    private Long id;

    @Column(name = "delivery_name", length = 50, nullable = false)
    private String deliveryName;

    @Column(name = "recipient_name", length = 12, nullable = false)
    private String recipientName;

    @Embedded
    private Address address;

    @Column(name = "phone_number", length = 13, nullable = false)
    private String phonenumber;

    @Column(name = "is_default_delivery")
    private String isDefaultDelivery;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;  //다대일 단방향

    //==비즈니스 로직==//
    /**
     * 배송지 주소 수정
     */
    public void update(EditDeliveryForm form) {
        this.deliveryName = form.getDeliveryName();
        this.recipientName = form.getRecipientName();
        this.address = new Address(form.getZipCode(), form.getBasicAddress(), form.getDetailAddress());
        this.phonenumber = form.getPhonenumber1() + "-" + form.getPhonenumber2() + "-" + form.getPhonenumber3();
        this.isDefaultDelivery = form.getIsDefaultDelivery().toString().toUpperCase();
    }

    /**
     * 주문서 페이지 팝업창
     * 배송지 주소 수정
     */
    public void updatePopup(EditPopupDeliveryForm form) {
        this.address.updateDetailAddress(form.getDetailAddress());
        this.recipientName = form.getRecipientName();
        this.phonenumber = form.getPhonenumber1() + "-" + form.getPhonenumber2() + "-" + form.getPhonenumber3();
    }

    /**
     * 기본 배송지 제거
     */
    public void removeDefault() {
        this.isDefaultDelivery = Boolean.toString(false).toUpperCase();
    }

    //==생성 메서드==//
    /**
     * 회원가입할 때 호출
     */
    public static Delivery createDeliveryWhenJoin(Member member, String zipCode, String basicAddress, String detailAddress) {
        Delivery delivery = new Delivery();
        delivery.deliveryName = "미지정";
        delivery.recipientName = member.getName();
        delivery.address = new Address(zipCode, basicAddress, detailAddress);
        delivery.phonenumber = member.getPhonenumber();
        delivery.isDefaultDelivery = Boolean.toString(true).toUpperCase();
        delivery.member = member;
        return delivery;
    }

    /**
     * 배송지 관리페이지에서 배송지 등록할 때 호출
     */
    public static Delivery createDelivery(AddDeliveryForm form, Member member) {
        Delivery delivery = new Delivery();
        delivery.deliveryName = form.getDeliveryName();
        delivery.recipientName = form.getRecipientName();
        delivery.address = new Address(form.getZipCode(), form.getBasicAddress(), form.getDetailAddress());
        delivery.phonenumber = form.getPhonenumber1() + "-" + form.getPhonenumber2() + "-" + form.getPhonenumber3();
        delivery.isDefaultDelivery = Boolean.toString(form.getIsDefaultDelivery()).toUpperCase();
        delivery.member = member;
        return delivery;
    }
}
