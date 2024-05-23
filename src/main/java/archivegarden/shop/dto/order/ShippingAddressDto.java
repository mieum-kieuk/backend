package archivegarden.shop.dto.order;

import archivegarden.shop.entity.ShippingAddress;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShippingAddressDto {

    private String addressName;
    private String recipientName;
    private String zipCode;
    private String basicAddress;
    private String detailAddress;
    private String phonenumber1;
    private String phonenumber2;
    private String phonenumber3;


    public ShippingAddressDto(ShippingAddress address) {
        this.addressName = address.getShippingAddressName();
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
