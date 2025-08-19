package archivegarden.shop.service.user.email;

import archivegarden.shop.entity.Member;
import archivegarden.shop.exception.api.EntityNotFoundAjaxException;
import archivegarden.shop.exception.global.EmailSendFailedException;
import archivegarden.shop.exception.global.EntityNotFoundException;
import archivegarden.shop.repository.member.MemberRepository;
import archivegarden.shop.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class EmailService {

    private final RedisUtil redisUtil;
    private final MemberRepository memberRepository;
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    private final PasswordEncoder passwordEncoder;

    @Value(("${app.base-url}"))
    private String baseUrl;

    @Value("${app.email.verification-path}")
    private String emailVerificationPath;

    @Value("${app.login-path}")
    private String loginPath;

    @Value("${spring.mail.username}")
    private String from;

    private static final long EMAIL_VERIFICATION_EXPIRE_SECONDS = 60 * 3L;  // 3분

    /**
     * 회원가입 완료 후 본인 인증 메일 전송
     *
     * @param to      수신자 이메일
     * @param name    가입한 이름
     * @param created 가입 일시
     */
    public void sendEmailVerificationLink(String to, String name, LocalDateTime created) {
        String uuid = UUID.randomUUID().toString();
        String verificationUrl = baseUrl + emailVerificationPath + "?address=" + to + "&uuid=" + uuid;

        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("created", DateTimeFormatter.ofPattern("yyyy년 M월 d일").format(created));
        context.setVariable("verificationUrl", verificationUrl);

        MimeMessagePreparator preparator = mimeMessage -> {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

            String content = templateEngine.process("user/email/template/join_complete", context);

            helper.setTo(to);
            helper.setFrom(from);
            helper.setSubject("[미음키읔] 회원가입을 축하드립니다.");
            helper.setText(content, true);
        };

        redisUtil.saveData(to, uuid, EMAIL_VERIFICATION_EXPIRE_SECONDS);
    }

    /**
     * 마이페이지에서 인증 메일 재전송
     *
     * @param to   수신자 이메일
     * @param name 수신자 이름
     */
    public void sendEmailVerificationLinkInMyPage(String to, String name) {
        String uuid = UUID.randomUUID().toString();
        String verificationUrl = baseUrl + emailVerificationPath + "?address=" + to + "&uuid=" + uuid;

        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("verificationUrl", verificationUrl);

        MimeMessagePreparator preparator = mimeMessage -> {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

            String content = templateEngine.process("user/email/template/validate_email", context);

            helper.setTo(to);
            helper.setFrom(from);
            helper.setSubject("[미음키읔] 이메일 인증 요청");
            helper.setText(content, true);
        };

        redisUtil.saveData(to, uuid, EMAIL_VERIFICATION_EXPIRE_SECONDS);

        javaMailSender.send(preparator);
    }

    /**
     * 이메일 인증 처리
     * <p>
     * Redis에 저장된 UUID와 사용자가 요청한 UUID를 비교하여 이메일 인증을 처리합니다.
     * 인증 완료 상태에 따라 결과 화면 경로를 반환합니다.
     *
     * @param email 수신자 이메일
     * @param uuid  인증 요청에 포함된 UUID
     * @return 인증 결과에 따른 뷰 이름
     * @throws EntityNotFoundException 회원이 존재하지 않는 경우
     */
    public String verifyEmail(String email, String uuid) {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));

        if (redisUtil.existData(email)) {
            if (member.isEmailVerified()) {    //이미 인증 완료
                return "user/email/verification_complete";
            } else if (redisUtil.getData(email).equals(uuid)) {    //인증 성공
                updateEmailVerification(email, true);
                return "user/email/verification_success";
            } else {    //uuid 일치X
                return "user/email/verification_fail";
            }
        } else {    //인증 유효시간 만료
            return "user/email/verification_timeout";
        }
    }

    /**
     * 임시 비밀번호 발송
     *
     * 사용자의 이메일로 임시 비밀번호를 발급하여 전송하고, 비밀번호를 갱신합니다.
     *
     * @param memberId 수신자 ID
     * @return 갱신된 회원 이메일
     * @throws EntityNotFoundAjaxException 회원이 존재하지 않는 경우
     * @throws EmailSendFailedException 이메일 발송 중 오류가 발생하는 경우
     */
    public String sendTempPassword(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundAjaxException("존재하지 않는 회원입니다."));
        String tempPassword = createTempPassword();

        Context context = new Context();
        context.setVariable("name", member.getName());
        context.setVariable("tempPassword", tempPassword);
        context.setVariable("loginUrl", baseUrl + loginPath);

        MimeMessagePreparator preparator = mimeMessage -> {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

            String content = templateEngine.process("user/email/template/temp_password", context);

            helper.setTo(member.getEmail());
            helper.setFrom(from);
            helper.setSubject("[미음키읔] 임시 비밀번호가 발급되었습니다.");
            helper.setText(content, true);
        };


        try {
            javaMailSender.send(preparator);
        } catch (MailAuthenticationException e) {
            throw new EmailSendFailedException("메일 인증에 실패했습니다. 토큰을 확인해 주세요.");
        } catch (MailParseException | MailPreparationException e) {
            throw new EmailSendFailedException("메시지 생성에 실패했습니다.");
        } catch (MailSendException e) {
            throw new EmailSendFailedException("메일 전송 중 알 수 없는 오류가 발생했습니다.");
        } catch (MailException e) {
            throw new EmailSendFailedException("메일 전송 중 예기치 못한 오류가 발생했습니다.");
        }

        String encodedPassword = passwordEncoder.encode(tempPassword);
        member.updatePassword(encodedPassword);

        return member.getEmail();
    }

    /**
     * 이메일 인증 상태 업데이트
     *
     * 사용자의 이메일 인증 여부를 DB에 업데이트합니다.
     *
     * @param email           회원 이메일
     * @param isEmailVerified 인증 여부 (true: 인증됨)
     * @throws EntityNotFoundException 회원이 존재하지 않는 경우
     */
    private void updateEmailVerification(String email, boolean isEmailVerified) {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));
        member.updateEmailVerificationStatus(isEmailVerified);
    }

    /**
     * 임시 비밀번호 생성
     * <p>
     * 영문 대문자와 숫자로 구성된 10자리 임시 비밀번호를 생성합니다.
     *
     * @return 임시 비밀번호
     */
    private String createTempPassword() {
        char[] charSet = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        String password = "";
        int idx = 0;
        for (int i = 0; i < 10; i++) {
            idx = (int) (charSet.length * Math.random());
            password += charSet[idx];
        }

        return password;
    }
}
