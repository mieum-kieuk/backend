package archivegarden.shop.dto.mypage.address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AddAddressForm {

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
}
