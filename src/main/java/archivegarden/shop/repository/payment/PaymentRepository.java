package archivegarden.shop.repository.payment;

import archivegarden.shop.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("select p from Payment p where p.merchantUid = :paymentId")
    Optional<Payment> findByPaymentId(@Param("paymentId") String paymentId);
}
