package archivegarden.shop.controller.user.member;

import archivegarden.shop.TestSecurityConfig;
import archivegarden.shop.constant.SessionConstants;
import archivegarden.shop.dto.user.member.PhonenumberRequestDto;
import archivegarden.shop.dto.user.member.VerificationRequestDto;
import archivegarden.shop.service.user.member.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberAjaxController.class)
@Import(TestSecurityConfig.class)
@MockBean(JpaMetamodelMappingContext.class)
public class MemberAjaxControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    MemberService memberService;

    @Nested
    @DisplayName("아이디/이메일 중복 검사")
    class CheckDuplicateTests {

        @Test
        @DisplayName("아이디 중복 검사 - 사용 가능")
        public void checkLoginIdDuplicate_Available() throws Exception {
            //given
            String availableId = "availableId";
            when(memberService.isAvailableLoginId(availableId)).thenReturn(true);

            //when
            ResultActions actions = mockMvc.perform(post("/ajax/member/check/loginId")
                    .with(csrf())
                    .param("loginId", availableId));

            //then
            actions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                    .andExpect(jsonPath("$.message").value("사용 가능한 아이디입니다."));

            verify(memberService).isAvailableLoginId(availableId);
        }

        @Test
        @DisplayName("아이디 중복 검사 - 이미 사용 중")
        public void checkLoginIdDuplicate_Taken() throws Exception {
            //given
            String takenId = "takenId";
            when(memberService.isAvailableLoginId(takenId)).thenReturn(false);

            //when
            ResultActions actions = mockMvc.perform(post("/ajax/member/check/loginId")
                    .with(csrf())
                    .param("loginId", takenId));

            //then
            actions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(jsonPath("$.message").value("이미 사용 중인 아이디입니다."));
        }

        @Test
        @DisplayName("이메일 중복 검사 - 사용 가능")
        public void checkEmailDuplicate_Available() throws Exception {
            //given
            String availableEmail = "availableEmail";
            when(memberService.isAvailableEmail(availableEmail)).thenReturn(true);

            //when
            ResultActions actions = mockMvc.perform(post("/ajax/member/check/email")
                    .with(csrf())
                    .param("email", availableEmail));

            //then
            actions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                    .andExpect(jsonPath("$.message").value("사용 가능한 이메일입니다."));

            verify(memberService).isAvailableEmail(availableEmail);
        }

        @Test
        @DisplayName("이메일 중복 검사 - 이미 사용 중")
        public void checkEmailDuplicate_Taken() throws Exception {
            //given
            String takenEmail = "takenEmail";
            when(memberService.isAvailableEmail(takenEmail)).thenReturn(false);

            //when
            ResultActions actions = mockMvc.perform(post("/ajax/member/check/email")
                    .with(csrf())
                    .param("email", takenEmail));

            //then
            actions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(jsonPath("$.message").value("이미 사용 중인 이메일입니다."));
        }
    }

    @Nested
    @DisplayName("인증번호 발송 및 검증")
    class VerificationTests {

        @Test
        @DisplayName("휴대전화번호 인증번호 발송 - 성공")
        void sendSms_Success() throws Exception {
            // given
            PhonenumberRequestDto dto = new PhonenumberRequestDto("010", "1234", "5678");
            String phonenumber = dto.getFormattedPhonenumber();
            when(memberService.isAvailablePhonenumber(phonenumber)).thenReturn(true);
            doNothing().when(memberService).sendVerificationNo(phonenumber);

            //when & then
            mockMvc.perform(post("/ajax/member/send/verificationNo")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .flashAttr("phonenumberRequestDto", dto))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                    .andExpect(jsonPath("$.message").value("인증번호가 발송되었습니다.\n인증번호를 받지 못하셨다면 휴대전화번호를 확인해 주세요."));
        }

        @Test
        @DisplayName("휴대전화번호 인증번호 발송 - 이미 등록된 번호")
        void sendSms_PhoneNumberTaken() throws Exception {
            // given
            PhonenumberRequestDto dto = new PhonenumberRequestDto("010", "1234", "5678");
            String phonenumber = dto.getFormattedPhonenumber();
            when(memberService.isAvailablePhonenumber(phonenumber)).thenReturn(false);

            // when & then
            mockMvc.perform(post("/ajax/member/send/verificationNo")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .flashAttr("phonenumberRequestDto", dto))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(jsonPath("$.message").value("입력하신 휴대전화번호는 이미 다른 계정에 등록되어 있습니다."));
        }

        @Test
        @DisplayName("인증번호 검증 - 성공")
        void checkVerificationNo_Success() throws Exception {
            // given
            VerificationRequestDto dto = new VerificationRequestDto("010", "1234", "5678", "123456");
            when(memberService.validateVerificationNo(any(VerificationRequestDto.class))).thenReturn(true);

            //when & then
            mockMvc.perform(post("/ajax/member/check/verificationNo")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .flashAttr("verificationRequestDto", dto))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                    .andExpect(jsonPath("$.message").value("인증번호 확인에 성공하였습니다."));
        }

        @Test
        @DisplayName("인증번호 검증 - 실패")
        void checkVerificationNo_Fail() throws Exception {
            // given
            VerificationRequestDto dto = new VerificationRequestDto("010", "1234", "5678", "123456");
            when(memberService.validateVerificationNo(any(VerificationRequestDto.class))).thenReturn(false);

            //when & then
            mockMvc.perform(post("/ajax/member/check/verificationNo")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .flashAttr("verificationRequestDto", dto))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(jsonPath("$.message").value("인증번호가 일치하지 않습니다.\n확인 후 다시 시도해 주세요."));
        }
    }

    @Nested
    @DisplayName("아이디/비밀번호 찾기")
    class FindCredentialTests {

        @Test
        @DisplayName("이메일로 아이디 찾기 - 성공")
        void findIdByEmail_Success() throws Exception {
            // given
            String name = "테스트";
            String email = "test@example.com";
            Long memberId = 1L;
            when(memberService.checkLoginIdExistsByEmail(name, email)).thenReturn(memberId);
            MockHttpSession session = new MockHttpSession();

            mockMvc.perform(post("/ajax/member/find-id/email")
                            .with(csrf())
                            .param("name", name)
                            .param("email", email)
                            .session(session))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                    .andExpect(request().sessionAttribute(SessionConstants.FIND_ID_MEMBER_ID_KEY, memberId));
        }

        @Test
        @DisplayName("이메일로 아이디 찾기 - 정보 없음")
        void findIdByEmail_NotFound() throws Exception {
            // given
            String name = "테스트";
            String email = "test@example.com";
            when(memberService.checkLoginIdExistsByEmail(name, email)).thenReturn(null);

            // when & then
            mockMvc.perform(post("/ajax/member/find-id/email")
                            .with(csrf())
                            .param("name", name)
                            .param("email", email))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(jsonPath("$.message").value("가입 시 입력하신 회원 정보가 맞는지\n다시 한번 확인해 주세요."));

        }

        @Test
        @DisplayName("휴대전화번호로 아이디 찾기 - 성공")
        void findIdByPhonenumber_Success() throws Exception {
            // given
            String name = "테스트";
            String phonenumber = "010-1234-5678";
            Long memberId = 1L;
            when(memberService.checkIdExistsByPhonenumber(name, phonenumber)).thenReturn(memberId);

            // when & then
            mockMvc.perform(post("/ajax/member/find-id/phonenumber")
                            .with(csrf())
                            .param("name", name)
                            .param("phonenumber", phonenumber))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                    .andExpect(request().sessionAttribute(SessionConstants.FIND_ID_MEMBER_ID_KEY, memberId));
        }

        @Test
        @DisplayName("휴대전화번호로 비밀번호 찾기 - 성공")
        void findPasswordByPhonenumber_Success() throws Exception {
            // given
            String loginId = "testuser1";
            String name = "테스트";
            String phonenumber = "010-1234-5678";
            String foundEmail = "test@example.com";
            when(memberService.checkPasswordExistsByPhonenumber(loginId, name, phonenumber)).thenReturn(foundEmail);

            // when & then
            mockMvc.perform(post("/ajax/member/find-password/phonenumber")
                            .with(csrf())
                            .param("loginId", loginId)
                            .param("name", name)
                            .param("phonenumber", phonenumber))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                    .andExpect(request().sessionAttribute(SessionConstants.FIND_PASSWORD_EMAIL_KEY, foundEmail));
        }
    }
}