package archivegarden.shop.dto.user.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomDataDTO {
    private Long orderId;
    private String recipientName;
    private String zipCode;
    private String addressLine1;
    private String addressLine2;
    private String phonenumber;
    private String deliveryRequestMsg;
    private Integer usePoints;
    private String deliveryOption;
    private String deliveryName;   // 있을 수도, 없을 수도
    private Boolean isDefaultDelivery;
}
