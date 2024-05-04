package archivegarden.shop.dto.mypage.address;

import archivegarden.shop.entity.ShippingAddress;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EditAddressForm {

    private Long id;

    @NotBlank(message = "배송지명을 입력해 주세요.")
    private String addressName;

    @NotBlank(message = "수령인을 입력해 주세요.")
    private String recipientName;

    private String zipCode;
    private String basicAddress;
    private String detailAddress;

    @Pattern(regexp = "^01(0|1|[6-9])$")
    private String phonenumber1;

    @Pattern(regexp = "^(\\d){3,4}$")
    private String phonenumber2;

    @Pattern(regexp = "^(\\d){4}$")
    private String phonenumber3;

    private Boolean isDefaultAddress;

    public EditAddressForm(ShippingAddress address) {
        this.id = address.getId();
        this.addressName = address.getShippingAddressName();
        this.recipientName = address.getRecipientName();
        this.zipCode = address.getAddress().getZipCode();
        this.basicAddress = address.getAddress().getBasicAddress();
        this.detailAddress = address.getAddress().getDetailAddress();
        String[] phonenumber = address.getPhonenumber().split("-");
        this.phonenumber1 = phonenumber[0];
        this.phonenumber2 = phonenumber[1];
        this.phonenumber3 = phonenumber[2];
        this.isDefaultAddress = Boolean.parseBoolean(address.getIsDefaultAddress());
    }
}
