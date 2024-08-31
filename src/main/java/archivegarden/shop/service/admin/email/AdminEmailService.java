package archivegarden.shop.service.admin.email;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminEmailService {

    private final TemplateEngine templateEngine;
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;

    /**
     * 관리자 권환 부여 완료 이메일 전송
     */
    public void sendAdminAuthComplete(String to, String name) {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("loginUrl", "http://localhost:8080/admin/login");


        MimeMessagePreparator preparator = mimeMessage -> {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

            String content = templateEngine.process("email/template/admin_auth_complete", context);

            helper.setTo(to);
            helper.setFrom(from);
            helper.setSubject("[미음키읔] 관리자 인증이 완료되었습니다.");
            helper.setText(content, true);
        };

        javaMailSender.send(preparator);
    }
}
