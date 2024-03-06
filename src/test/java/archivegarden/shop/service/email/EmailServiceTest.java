package archivegarden.shop.service.email;

import archivegarden.shop.dto.member.MemberSaveDto;
import archivegarden.shop.dto.member.MemberSaveForm;
import archivegarden.shop.service.member.MemberService;
import archivegarden.shop.util.RedisUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private RedisUtil redisUtil;

    @Test
    @DisplayName("인증 이메일 전송")
    public void send_email_verification() {
        //given
        String to = "veryvery98@naver.com";
        LocalDateTime created = LocalDateTime.now();

        //when
        emailService.sendValidationRequestEmail(to, created);
    }

    @Transactional
    @Test
    @DisplayName("이메일 인증 성공")
    public void verify_email_verification_success() {
        //given
        String to = "test@gmail.com";
        String uuid = UUID.randomUUID().toString();
        memberService.join(new MemberSaveDto(new MemberSaveForm("test1234", "test1234!!", "test1234!!", "테스터", "", "", "", "010", "1111", "1111", to, true, true, true, true)));
        redisUtil.setDataExpire(to, uuid, 1L * 60);

        //when
        String result = emailService.verifyEmailLink(to, uuid);

        //then
        assertThat(result).isEqualTo("email/email_verification_success");
    }

    @Transactional
    @Test
    @DisplayName("이메일 인증 성공 - 유효시간 내에 다시 시도")
    public void verify_email_verification_success_again() {
        //given
        String to = "test@gmail.com";
        String uuid = UUID.randomUUID().toString();
        memberService.join(new MemberSaveDto(new MemberSaveForm("test1234", "test1234!!", "test1234!!", "테스터", "", "", "", "010", "1111", "1111", to, true, true, true, true)));
        redisUtil.setDataExpire(to, uuid, 1L * 60);

        //when
        String firstResult = emailService.verifyEmailLink(to, uuid);
        String secondResult = emailService.verifyEmailLink(to, uuid);

        //then
        assertThat(firstResult).isEqualTo("email/email_verification_success");
        assertThat(secondResult).isEqualTo("email/email_verification_complete");
    }

    @Transactional
    @Test
    @DisplayName("이메일 인증 실패_유효시간 만료")
    public void verify_email_verification_timeout() throws InterruptedException {
        //given
        String to = "test@gmail.com";
        String uuid = UUID.randomUUID().toString();
        memberService.join(new MemberSaveDto(new MemberSaveForm("test1234", "test1234!!", "test1234!!", "테스터", "", "", "", "010", "1111", "1111", to, true, true, true, true)));
        redisUtil.setDataExpire(to, uuid, 1L * 60);

        //when
        Thread.sleep(65 * 1000);
        String result = emailService.verifyEmailLink(to, uuid);

        //then
        assertThat(result).isEqualTo("email/email_verification_timeout");
    }

    @Transactional
    @Test
    @DisplayName("이메일 인증 실패_uuid 일치X")
    public void verify_email_verification_tfail() throws InterruptedException {
        //given
        String to = "test@gmail.com";
        String uuid = UUID.randomUUID().toString();
        String wrongUuid = UUID.randomUUID().toString();
        memberService.join(new MemberSaveDto(new MemberSaveForm("test1234", "test1234!!", "test1234!!", "테스터", "", "", "", "010", "1111", "1111", to, true, true, true, true)));
        redisUtil.setDataExpire(to, uuid, 1L * 60);

        //when
        String result = emailService.verifyEmailLink(to, wrongUuid);

        //then
        assertThat(result).isEqualTo("email/email_verification_fail");
    }
}