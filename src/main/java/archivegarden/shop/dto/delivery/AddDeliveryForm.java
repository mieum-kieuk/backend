package archivegarden.shop.dto.delivery;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AddDeliveryForm {

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

    private boolean defaultDelivery;

    public AddDeliveryForm(String deliveryName, String recipientName, String zipCode, String basicAddress, String detailAddress, String phonenumber, boolean defaultDelivery) {
        this.deliveryName = deliveryName;
        this.recipientName = recipientName;
        this.zipCode = zipCode;
        this.basicAddress = basicAddress;
        this.detailAddress = detailAddress;
        String[] phonenumbers = phonenumber.split("-");
        this.phonenumber1 = phonenumbers[0];
        this.phonenumber2 = phonenumbers[1];
        this.phonenumber3 = phonenumbers[2];
        this.defaultDelivery = defaultDelivery;
    }
}
