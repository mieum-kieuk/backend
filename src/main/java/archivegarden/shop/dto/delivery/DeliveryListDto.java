package archivegarden.shop.dto.delivery;

import archivegarden.shop.entity.Delivery;
import lombok.Getter;

@Getter
public class DeliveryListDto {

    private Long id;
    private String deliveryName;
    private String recipientName;
    private String address;
    private String phonenumber;
    private boolean isDefault;

    public DeliveryListDto(Delivery delivery) {
        this.id = delivery.getId();
        this.deliveryName = delivery.getDeliveryName();
        this.recipientName = delivery.getRecipientName();
        this.address = delivery.getAddress().fullAddress();
        this.phonenumber = delivery.getPhonenumber();
        this.isDefault = delivery.isDefault();
    }
}
