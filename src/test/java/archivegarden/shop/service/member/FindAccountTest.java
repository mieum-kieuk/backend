package archivegarden.shop.service.member;

import archivegarden.shop.dto.member.FindIdForm;
import archivegarden.shop.dto.member.FindIdResultDto;
import archivegarden.shop.dto.member.MemberSaveDto;
import archivegarden.shop.dto.member.MemberSaveForm;
import archivegarden.shop.entity.FindAccountType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class FindAccountTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MemberService memberService;

    @Transactional
    @Test
    @DisplayName("이메일로 아이디 찾기_성공")
    public void findIdByEmail() {
        //given
        String email = "test1234@gmail.com";
        memberService.join(new MemberSaveDto(new MemberSaveForm("test1234", "test1234!!", "test1234!!", "테스터", "", "", "", "010", "1111", "1111", email, true, true, true, true)));

        FindIdForm form = new FindIdForm(FindAccountType.EMAIL, "테스터", email, null, null, null);

        //when
        Optional<FindIdResultDto> result = memberService.findId(form);

        //then
        assertThat(result.get().getName()).isEqualTo("테스터");

    }

    @Transactional
    @Test
    @DisplayName("이메일로 아이디 찾기_실패")
    public void findIdByEmail_NameX() throws Exception {
        //given
        String email = "test1234@gmail.com";
        memberService.join(new MemberSaveDto(new MemberSaveForm("test1234", "test1234!!", "test1234!!", "테스터", "", "", "", "010", "1111", "1111", email, true, true, true, true)));

        FindIdForm form = new FindIdForm(FindAccountType.EMAIL, "유저", "test1234@gmail.com", null, null, null);

        //when
        Optional<FindIdResultDto> result = memberService.findId(form);

        //then
        Assertions.assertThat(result.isEmpty()).isTrue();
    }
}
