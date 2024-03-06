package archivegarden.shop.service.email;

import archivegarden.shop.entity.Member;
import archivegarden.shop.repository.MemberRepository;
import archivegarden.shop.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class EmailService {

    private final MemberRepository memberRepository;
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    private final RedisUtil redisUtil;

    @Value("${spring.mail.username}")
    private String from;

    /**
     * 이메일 인증 링크 전송
     */
    public void sendValidationRequestEmail(String to, LocalDateTime created) {

        String uuid = UUID.randomUUID().toString();
        String verificationUrl = "http://localhost:8080/verification/email/link?address=" + to + "&uuid=" + uuid;

        Context context = new Context();
        context.setVariable("email", to);
        context.setVariable("created", DateTimeFormatter.ofPattern("yyyy년 MM월 dd일").format(created));
        context.setVariable("verificationUrl", verificationUrl);

        MimeMessagePreparator preparator = mimeMessage -> {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

            String content = templateEngine.process("email/email_verification_template", context);

            helper.setTo(to);
            helper.setFrom(from);
            helper.setSubject("[ArchiveGarden] 회원가입을 축하드립니다.");
            helper.setText(content, true);
        };

        redisUtil.setDataExpire(to, uuid, 3 * 60L);

        javaMailSender.send(preparator);
    }

    /**
     * 이메일 검증
     */
    public String verifyEmailLink(String email, String uuid) {

        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new NoSuchElementException("존재하지 않는 이메일입니다."));

        if(redisUtil.existData(email)) {
            if(Boolean.valueOf(member.getIsEmailVerified())) {    //이미 인증 완료
                return "email/email_verification_complete";
            } else if(redisUtil.getData(email).equals(uuid)) {    //처음 성공
                updateEmailVerified(email);
                return "email/email_verification_success";
            } else {    //uuid 일치X
                return "email/email_verification_fail";
            }
        } else {    //인증 유효 시간 만료
            return "email/email_verification_timeout";
        }
    }

    /**
     * 이메일 인증 완료
     * FALSE -> TRUE
     */
    private void updateEmailVerified(String email) {
        //엔티티 조회
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));

        //이메일 인증 필드 수정
        member.completeEmailVerification();
    }
}
