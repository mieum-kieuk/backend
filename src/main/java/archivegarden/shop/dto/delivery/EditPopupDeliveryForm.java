package archivegarden.shop.dto.delivery;

import archivegarden.shop.entity.Address;
import archivegarden.shop.entity.Delivery;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EditPopupDeliveryForm {

    private Long id;

    @NotBlank(message = "수령인을 입력해 주세요.")
    private String recipientName;

    private String basicAddress;
    private String detailAddress;

    private String phonenumber1;
    private String phonenumber2;
    private String phonenumber3;

    public EditPopupDeliveryForm(Delivery delivery) {
        this.id = delivery.getId();
        this.recipientName = delivery.getRecipientName();

        Address address = delivery.getAddress();
        this.basicAddress = address.getBasicAddress();
        this.detailAddress = address.getDetailAddress();

        String[] phonenumber = delivery.getPhonenumber().split("-");
        this.phonenumber1 = phonenumber[0];
        this.phonenumber2 = phonenumber[1];
        this.phonenumber3 = phonenumber[2];
    }
}
