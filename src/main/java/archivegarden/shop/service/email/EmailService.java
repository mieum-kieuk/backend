package archivegarden.shop.service.email;

import archivegarden.shop.entity.Member;
import archivegarden.shop.exception.NoSuchMemberException;
import archivegarden.shop.exception.ajax.AjaxNotFoundException;
import archivegarden.shop.repository.member.MemberRepository;
import archivegarden.shop.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
    private final PasswordEncoder passwordEncoder;

    @Value("${spring.mail.username}")
    private String from;

    /**
     * 이메일 인증 링크 전송
     */
    public void sendValidationRequestEmail(String to, LocalDateTime created) {

        String uuid = UUID.randomUUID().toString();
        String verificationUrl = "http://localhost:8080/email/verification/link?address=" + to + "&uuid=" + uuid;

        Context context = new Context();
        context.setVariable("email", to);
        context.setVariable("created", DateTimeFormatter.ofPattern("yyyy년 MM월 dd일").format(created));
        context.setVariable("verificationUrl", verificationUrl);

        MimeMessagePreparator preparator = mimeMessage -> {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

            String content = templateEngine.process("email/template/verification_link", context);

            helper.setTo(to);
            helper.setFrom(from);
            helper.setSubject("[미음키읔] 회원가입을 축하드립니다.");
            helper.setText(content, true);
        };

        redisUtil.setDataExpire(to, uuid, 3 * 60L);

        javaMailSender.send(preparator);
    }

    /**
     * 이메일 검증
     *
     * @throws NoSuchMemberException
     */
    public String verifyEmailLink(String email, String uuid) {

        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new NoSuchMemberException("존재하지 않는 회원입니다."));

        if (redisUtil.existData(email)) {
            if (Boolean.valueOf(member.isEmailVerified())) {    //이미 인증 완료
                return "email/verification_complete";
            } else if (redisUtil.getData(email).equals(uuid)) {    //처음 성공
                updateEmailVerified(email);
                return "email/verification_success";
            } else {    //uuid 일치X
                return "email/verification_fail";
            }
        } else {    //인증 유효 시간 만료
            return "email/verification_timeout";
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

    /**
     * 임시 비밀번호 발급
     *
     * @throws AjaxNotFoundException
     */
    public Long sendTempPassword(String to) {

        //Member 조회
        Member member = memberRepository.findByEmail(to).orElseThrow(() -> new AjaxNotFoundException("존재하지 않는 회원입니다."));

        //임시 비밀번호 발급
        String tempPassword = getTempPassword();

        //임시 비밀번호 전송
        Context context = new Context();
        context.setVariable("name", member.getName());
        context.setVariable("tempPassword", tempPassword);
        context.setVariable("loginUrl", "http://localhost:8080/login");

        MimeMessagePreparator preparator = mimeMessage -> {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

            String content = templateEngine.process("email/template/temp_password", context);

            helper.setTo(to);
            helper.setFrom(from);
            helper.setSubject("[미음키읔] 임시 비밀번호가 발급되었습니다.");
            helper.setText(content, true);
        };

        javaMailSender.send(preparator);

        //비밀번호 업데이트
        String encodedPassword = passwordEncoder.encode(tempPassword);
        member.updatePassword(encodedPassword);

        return member.getId();
    }

    /**
     * 임시 비밀번호 생성
     */
    private String getTempPassword() {
        char[] charSet = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

        String password = "";

        // 문자 배열 길이의 값을 랜덤으로 10개를 뽑아 구문을 작성함
        int idx = 0;
        for (int i = 0; i < 10; i++) {
            idx = (int) (charSet.length * Math.random());
            password += charSet[idx];
        }
        return password;
    }


    /**
     * 관리자 권환 부여 완료 이메일 전송
     */
    public void sendAuthComplete(String to, String name) {

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
