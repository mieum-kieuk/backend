package archivegarden.shop.dto.payment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Portone {

    private String paymentId;
    private String transactionType;
    private String txId;
    private String code;
    private String message;
}
