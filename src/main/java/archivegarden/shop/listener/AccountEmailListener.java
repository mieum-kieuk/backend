package archivegarden.shop.listener;

import archivegarden.shop.event.TempPasswordIssuedEvent;
import archivegarden.shop.event.UserRegisteredEvent;
import archivegarden.shop.service.user.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class AccountEmailListener {

    private final EmailService emailService;

    /**
     * 회원가입 이메일 인증
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserRegistered(UserRegisteredEvent event) {
        emailService.sendEmailVerificationLink(event.email(), event.name(), event.createdAt());
    }

    /**
     * 임시 비밀번호 발급 메일
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTempPassword(TempPasswordIssuedEvent event) {
        emailService.sendTempPassword(event.email(), event.name(), event.tempPassword());
    }
}
