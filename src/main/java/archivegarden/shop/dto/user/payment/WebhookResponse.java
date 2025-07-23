package archivegarden.shop.dto.user.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WebhookResponse {
    private String status;
    private String code;
    private String failReason;
}
