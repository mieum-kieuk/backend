package archivegarden.shop.dto.delivery;

import archivegarden.shop.entity.Delivery;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeliveryDto {

    private String deliveryName;
    private String recipientName;
    private String zipCode;
    private String basicAddress;
    private String detailAddress;
    private String phonenumber1;
    private String phonenumber2;
    private String phonenumber3;


    public DeliveryDto(Delivery address) {
        this.deliveryName = address.getDeliveryName();
        this.recipientName = address.getRecipientName();
        this.zipCode = address.getAddress().getZipCode();
        this.basicAddress = address.getAddress().getBasicAddress();
        this.detailAddress = address.getAddress().getDetailAddress();
        String[] phonenumbers = address.getPhonenumber().split("-");
        this.phonenumber1 = phonenumbers[0];
        this.phonenumber2 = phonenumbers[1];
        this.phonenumber3 = phonenumbers[2];
    }
}
