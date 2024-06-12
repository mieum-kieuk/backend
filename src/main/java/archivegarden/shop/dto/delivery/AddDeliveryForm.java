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

    private Boolean isDefaultDelivery;
}
