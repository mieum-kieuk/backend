package archivegarden.shop.service.admin.email;

import archivegarden.shop.exception.global.EmailSendFailedException;
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

    private static final String ADMIN_AUTH_TEMPLATE = "email/template/admin_auth_complete";

    private final TemplateEngine templateEngine;
    private final JavaMailSender javaMailSender;

    @Value(("${app.base-url}"))
    private String baseUrl;

    @Value("${app.admin-login-path}")
    private String loginPath;

    @Value("${spring.mail.username}")
    private String from;

    /**
     * 관리자 권한 부여 완료 이메일 전송
     *
     * 관리자 권한이 부여되었음을 알리는 이메일을 해당 관리자에게 전송합니다.
     *
     * @param to   이메일 수신자 주소
     * @param name 수신자 이름
     */
    public void sendAdminAuthComplete(String to, String name) {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("loginUrl", baseUrl + loginPath);


        MimeMessagePreparator preparator = mimeMessage -> {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

            String content = templateEngine.process(ADMIN_AUTH_TEMPLATE, context);

            helper.setTo(to);
            helper.setFrom(from);
            helper.setSubject("[미음키읔] 관리자 권한이 부여되었습니다.");
            helper.setText(content, true);
        };

        try {
            javaMailSender.send(preparator);
        } catch (Exception e) {
            throw new EmailSendFailedException("이메일 전송중 오류가 발생했습니다. 다시 시도해 주세요.", e);
        }
    }
}
