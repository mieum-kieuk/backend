package archivegarden.shop.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    private Long amount;

    @Column(name = "pg_provider")
    private String pgProvider;

    @Column(name = "buyer_email")
    private String buyerEmail;

    @Column(name = "card_name")
    private String cardName;

    @Column(name = "card_quota")
    private Long cardQuota;

    private String currency;

    @Column(name = "imp_uid")
    private String impUid;

    @Column(name = "merchant_uid")
    private String merchantUid;

    @Column(name = "pay_method")
    private String payMethod;

    private String status;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "failed_at")
    private LocalDateTime failedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Builder
    public Payment(Long id, Long amount, String pgProvider, String buyerEmail, String cardName, Long cardQuota, String currency, String impUid, String merchantUid, String payMethod, String status, LocalDateTime paidAt, LocalDateTime failedAt, Order order) {
        this.id = id;
        this.amount = amount;
        this.pgProvider = pgProvider;
        this.buyerEmail = buyerEmail;
        this.cardName = cardName;
        this.cardQuota = cardQuota;
        this.currency = currency;
        this.impUid = impUid;
        this.merchantUid = merchantUid;
        this.payMethod = payMethod;
        this.status = status;
        this.paidAt = paidAt;
        this.failedAt = failedAt;
    }

    public void updateStatus(String status, LocalDateTime cancelledAt) {
        this.status = status;
        this.cancelledAt = cancelledAt;
    }
}
