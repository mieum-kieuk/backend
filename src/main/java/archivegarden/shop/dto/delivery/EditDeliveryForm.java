package archivegarden.shop.dto.mypage.address;

import archivegarden.shop.entity.Address;
import archivegarden.shop.entity.Delivery;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EditDeliveryForm {

    private Long id;

    @NotBlank(message = "배송지명을 입력해 주세요.")
    private String deliveryName;

    @NotBlank(message = "수령인을 입력해 주세요.")
    private String recipientName;

    private String zipCode;
    private String basicAddress;
    private String detailAddress;

    private String phonenumber1;
    private String phonenumber2;
    private String phonenumber3;

    private Boolean isDefaultDelivery;

    public EditDeliveryForm(Delivery delivery) {
        this.id = delivery.getId();
        this.deliveryName = delivery.getDeliveryName();
        this.recipientName = delivery.getRecipientName();

        Address address = delivery.getAddress();
        this.zipCode = address.getZipCode();
        this.basicAddress = address.getBasicAddress();
        this.detailAddress = address.getDetailAddress();

        String[] phonenumber = delivery.getPhonenumber().split("-");
        this.phonenumber1 = phonenumber[0];
        this.phonenumber2 = phonenumber[1];
        this.phonenumber3 = phonenumber[2];

        this.isDefaultDelivery = Boolean.parseBoolean(delivery.getIsDefaultDelivery());
    }
}
