package archivegarden.shop.service.payment;

import archivegarden.shop.repository.payment.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public boolean findPayment(String merchantUid) {
        return true;
    }
}
