package archivegarden.shop.dto.mypage.address;

import archivegarden.shop.entity.ShippingAddress;
import lombok.Getter;

@Getter
public class AddressListDto {

    private Long id;
    private boolean isDefaultAddress;
    private String addressName;
    private String recipientName;
    private String phonenumber;
    private String address;


    public AddressListDto(ShippingAddress address) {
        this.id = address.getId();
        this.isDefaultAddress = Boolean.parseBoolean(address.getIsDefaultAddress());
        this.addressName = address.getShippingAddressName();
        this.recipientName = address.getRecipientName();
        this.address = address.getAddress().fullAddress();
        this.phonenumber = address.getPhonenumber();
    }
}
