package archivegarden.shop.dto.user.payment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class WebhookRequest {

    private String type;
    private LocalDateTime timestamp;
    private Data data;

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    public static class Data {
        private String paymentId;
        private String transactionId;
        private String cancellationId;
    }
}
