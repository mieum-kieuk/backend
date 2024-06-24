package archivegarden.shop.dto.delivery;

import archivegarden.shop.entity.Delivery;
import lombok.Getter;

@Getter
public class DeliveryListDto {

    private Long id;
    private String deliveryName;
    private String recipientName;
    private String zipCode;
    private String basicAddress;
    private String detailAddress;
    private String phonenumber;
    private boolean isDefaultDelivery;


    public DeliveryListDto(Delivery delivery) {
        this.id = delivery.getId();
        this.deliveryName = delivery.getDeliveryName();
        this.recipientName = delivery.getRecipientName();
        this.zipCode = delivery.getAddress().getZipCode();
        this.basicAddress = delivery.getAddress().getBasicAddress();
        this.detailAddress = delivery.getAddress().getDetailAddress();
        this.phonenumber = delivery.getPhonenumber();
        this.isDefaultDelivery = Boolean.parseBoolean(delivery.getIsDefaultDelivery());
    }
}
