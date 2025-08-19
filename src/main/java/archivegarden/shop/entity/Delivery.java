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

    @Column(name = "is_default")
    private boolean isDefault;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;

    /**
     * 회원가입할 때 호출
     */
    public static Delivery createDeliveryWhenJoin(String zipCode, String basicAddress, String detailAddress, String recipientName, String phonenumber) {
        Delivery delivery = new Delivery();
        delivery.deliveryName = "미지정";
        delivery.recipientName = recipientName;
        delivery.address = new Address(zipCode, basicAddress, detailAddress);
        delivery.phonenumber = phonenumber;
        delivery.isDefault = true;
        return delivery;
    }

    /**
     * 마이페이지 배송지 관리에서 배송지 등록할 때 호출
     */
    public static Delivery createDelivery(AddDeliveryForm form, Member member) {
        Delivery delivery = new Delivery();
        delivery.deliveryName = form.getDeliveryName();
        delivery.recipientName = form.getRecipientName();
        delivery.address = new Address(form.getZipCode(), form.getBasicAddress(), form.getDetailAddress());
        delivery.phonenumber = form.getPhonenumber1() + "-" + form.getPhonenumber2() + "-" + form.getPhonenumber3();
        delivery.isDefault = form.isDefaultDelivery();
        delivery.member = member;
        return delivery;
    }

    /**
     * 배송지 주소 수정
     */
    public void update(EditDeliveryForm form) {
        this.deliveryName = form.getDeliveryName();
        this.recipientName = form.getRecipientName();
        this.address = new Address(form.getZipCode(), form.getBasicAddress(), form.getDetailAddress());
        this.phonenumber = form.getPhonenumber1() + "-" + form.getPhonenumber2() + "-" + form.getPhonenumber3();
        this.isDefault = form.isDefault();
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
        this.isDefault = false;
    }

    public void setMember(Member member) {
        this.member = member;
    }
}
