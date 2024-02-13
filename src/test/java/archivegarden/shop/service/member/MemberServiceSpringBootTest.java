package archivegarden.shop.service.member;

import archivegarden.shop.service.MemberService;
import archivegarden.shop.web.form.MemberSaveDto;
import archivegarden.shop.web.form.MemberSaveForm;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ObjectError;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class MemberServiceSpringBootTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MemberService memberService;

    @Test
    @DisplayName("회원가입 성공")
    public void join() throws Exception {
        //given
        MemberSaveForm form = new MemberSaveForm("test1234", "test1234!@", "test1234!@", "테스터", "010-1234-5678", "test@gmail.com", true, true, true, true);

        //when
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/members/join")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("form", form))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/members/join/complete"));
    }

    @Test
    @DisplayName("회원가입 실패: loginId_null")
    public void joinX_loginId1() throws Exception {
        //given
        MemberSaveForm form = new MemberSaveForm(null, "test1234!@", "test1234!@", "테스터", "010-1234-5678", "test@gmail.com", true, true, true, false);

        //when
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/members/join")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("form", form))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrors("form", "loginId"))
                .andExpect(model().attributeHasFieldErrorCode("form", "loginId", "NotBlank"));
    }

    @Test
    @DisplayName("회원가입 실패: loginId_empty")
    public void joinX_loginId2() throws Exception {
        //given
        MemberSaveForm form = new MemberSaveForm("", "test1234!@", "test1234!@", "테스터", "010-1234-5678", "test@gmail.com", true, true, true, false);

        //when
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/members/join")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("form", form))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrors("form", "loginId"))
                .andExpect(model().attributeHasFieldErrorCode("form", "loginId", "NotBlank"));
    }

    @Test
    @DisplayName("회원가입 실패: loginId_blank")
    public void joinX_loginId3() throws Exception {
        //given
        MemberSaveForm form = new MemberSaveForm(" ", "test1234!@", "test1234!@", "테스터", "010-1234-5678", "test@gmail.com", true, true, true, false);

        //when
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/members/join")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("form", form))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrors("form", "loginId"))
                .andExpect(model().attributeHasFieldErrorCode("form", "loginId", "NotBlank"));
    }

    @Test
    @DisplayName("회원가입 실패: loginId_한글")
    public void joinX_loginId4() throws Exception {
        //given
        MemberSaveForm form = new MemberSaveForm("테스트", "test1234!@", "test1234!@", "테스터", "010-1234-5678", "test@gmail.com", true, true, true, false);

        //when
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/members/join")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("form", form))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrors("form", "loginId"))
                .andExpect(model().attributeHasFieldErrorCode("form", "loginId", "Pattern"));
    }

    @Test
    @DisplayName("회원가입 실패: loginId_특수문자")
    public void joinX_loginId5() throws Exception {
        //given
        MemberSaveForm form = new MemberSaveForm("@%$&%#@", "test1234!@", "test1234!@", "테스터", "010-1234-5678", "test@gmail.com", true, true, true, false);

        //when
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/members/join")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("form", form))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrors("form", "loginId"))
                .andExpect(model().attributeHasFieldErrorCode("form", "loginId", "Pattern"));
    }

    @Test
    @DisplayName("회원가입 실패: loginId_영문O숫자X")
    public void joinX_loginId6() throws Exception {
        //given
        MemberSaveForm form = new MemberSaveForm("testId", "test1234!@", "test1234!@", "테스터", "010-1234-5678", "test@gmail.com", true, true, true, false);

        //when
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/members/join")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("form", form))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrors("form", "loginId"))
                .andExpect(model().attributeHasFieldErrorCode("form", "loginId", "Pattern"));
    }

    @Test
    @DisplayName("회원가입 실패: loginId_영문X숫자O")
    public void joinX_loginId7() throws Exception {
        //given
        MemberSaveForm form = new MemberSaveForm("1234567", "test1234!@", "test1234!@", "테스터", "010-1234-5678", "test@gmail.com", true, true, true, false);

        //when
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/members/join")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("form", form))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrors("form", "loginId"))
                .andExpect(model().attributeHasFieldErrorCode("form", "loginId", "Pattern"));
    }

    @Test
    @DisplayName("회원가입 실패: loginId_길이부족")
    public void joinX_loginId8() throws Exception {
        //given
        MemberSaveForm form = new MemberSaveForm("test", "test1234!@", "test1234!@", "테스터", "010-1234-5678", "test@gmail.com", true, true, true, false);

        //when
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/members/join")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("form", form))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrors("form", "loginId"))
                .andExpect(model().attributeHasFieldErrorCode("form", "loginId", "Pattern"));
    }

    @Test
    @DisplayName("회원가입 실패: loginId_길이초과")
    public void joinX_loginId9() throws Exception {
        //given
        MemberSaveForm form = new MemberSaveForm("testtesttesttest1234", "test1234!@", "test1234!@", "테스터", "010-1234-5678", "test@gmail.com", true, true, true, false);

        //when
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/members/join")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("form", form))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrors("form", "loginId"))
                .andExpect(model().attributeHasFieldErrorCode("form", "loginId", "Pattern"));
    }

    @Test
    @DisplayName("회원가입 실패: password_null")
    public void joinX_password1() throws Exception {
        //given
        MemberSaveForm form = new MemberSaveForm("test1234", null, "test1234!@", "테스터", "010-1234-5678", "test@gmail.com", true, true, true, false);

        //when
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/members/join")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("form", form))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrors("form", "password"))
                .andExpect(model().attributeHasFieldErrorCode("form", "password", "NotBlank"));
    }

    @Test
    @DisplayName("회원가입 실패: password_empty")
    public void joinX_password2() throws Exception {
        //given
        MemberSaveForm form = new MemberSaveForm("test1234", "", "test1234!@", "테스터", "010-1234-5678", "test@gmail.com", true, true, true, false);

        //when
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/members/join")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("form", form))
                .andExpect(model().errorCount(2))
                .andExpect(model().attributeHasFieldErrors("form", "password"))
                .andExpect(model().attributeHasFieldErrorCode("form", "password", "NotBlank"));
    }

    @Test
    @DisplayName("회원가입 실패: password_영문O숫자O특수문자X")
    public void joinX_password3() throws Exception {
        //given
        MemberSaveForm form = new MemberSaveForm("test1234", "test123456", "test123456", "테스터", "010-1234-5678", "test@gmail.com", true, true, true, false);

        //when
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/members/join")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("form", form))
                .andExpect(model().errorCount(2))
                .andExpect(model().attributeHasFieldErrors("form", new String[]{"password", "passwordConfirm"}))
                .andExpect(model().attributeHasFieldErrorCode("form", "password", "Pattern"))
                .andExpect(model().attributeHasFieldErrorCode("form", "passwordConfirm", "Pattern"));
    }

    @Test
    @DisplayName("회원가입 실패: password_일치X")
    public void joinX_password_confirm() throws Exception {
        //given
        MemberSaveForm form = new MemberSaveForm("test1234", "test1234!@", "test1234!#", "테스터", "010-1234-5678", "test@gmail.com", true, true, true, false);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .post("/members/join")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("form", form))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasErrors("form"))
                .andReturn();

        BeanPropertyBindingResult bindingResult = (BeanPropertyBindingResult) mvcResult.getModelAndView().getModelMap().getAttribute("org.springframework.validation.BindingResult.form");
        ObjectError globalError = bindingResult.getGlobalError();
        String defaultMessage = globalError.getDefaultMessage();

        //then
        Assertions.assertThat(defaultMessage).isEqualTo("동일한 비밀번호를 입력해주세요.");
    }

    @Test
    @DisplayName("아이디 중복 검증: 이미 사용중인 아이디")
    public void join_duplicate_loginId1() {
        //given
        MemberSaveForm form = new MemberSaveForm("test1234", "test1234!@", "test1234!@", "테스터", "010-1234-5678", "test@gmail.com", true, true, true, true);
        memberService.join(new MemberSaveDto(form));

        //when
        boolean isNewLoginId = memberService.duplicateLoginId("test1234");

        //then
        Assertions.assertThat(isNewLoginId).isFalse();
    }

    @Test
    @DisplayName("아이디 중복 검증: 새로운 아이디")
    public void join_duplicate_loginId2() {
        //given
        MemberSaveForm form = new MemberSaveForm("test1234", "test1234!@", "test1234!@", "테스터", "010-1234-5678", "test@gmail.com", true, true, true, true);
        memberService.join(new MemberSaveDto(form));

        //when
        boolean isNewLoginId = memberService.duplicateLoginId("new1234");

        //then
        Assertions.assertThat(isNewLoginId).isTrue();
    }
}