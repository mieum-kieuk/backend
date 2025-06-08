package archivegarden.shop.controller.user.member;

import archivegarden.shop.TestSecurityConfig;
import archivegarden.shop.constant.SessionConstants;
import archivegarden.shop.dto.admin.member.membership.AdminAddMembershipForm;
import archivegarden.shop.dto.common.JoinSuccessDto;
import archivegarden.shop.dto.user.member.FindIdResultDto;
import archivegarden.shop.dto.user.member.JoinMemberForm;
import archivegarden.shop.entity.Member;
import archivegarden.shop.entity.Membership;
import archivegarden.shop.exception.common.DuplicateEntityException;
import archivegarden.shop.service.user.member.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberController.class)
@Import(TestSecurityConfig.class)
@MockBean(JpaMetamodelMappingContext.class)
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    private JoinMemberForm createValidJoinForm() {
        JoinMemberForm form = new JoinMemberForm();
        form.setLoginId("testuser1");
        form.setPassword("password123P!");
        form.setPasswordConfirm("password123P!");
        form.setName("테스트");
        form.setEmail("test@example.com");
        form.setZipCode("12345");
        form.setBasicAddress("서울시 테스트구");
        form.setDetailAddress("테스트로123");
        form.setPhonenumber1("010");
        form.setPhonenumber2("1234");
        form.setPhonenumber3("5678");
        form.setAgreeToTermsOfUse(true);
        form.setAgreeToPersonalInformation(true);
        return form;
    }

    @Test
    @DisplayName("회원가입 폼 요청")
    void addMemberForm_ShouldReturnJoinView() throws Exception {
        //when & then
        mockMvc.perform(get("/member/join"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/member/join"))
                .andExpect(model().attributeExists("joinForm"));
    }

    @Test
    @DisplayName("회원가입 성공")
    public void join_WhenValidForm_ShouldRedirectToComplete() throws Exception {
        //given
        JoinMemberForm form = createValidJoinForm();
        when(memberService.join(any(JoinMemberForm.class))).thenReturn(1L);
        doNothing().when(memberService).checkMemberDuplicate(any(JoinMemberForm.class));

        //when & then
        mockMvc.perform(post("/member/join")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("joinForm", form))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/member/join/complete"))
                .andExpect(request().sessionAttribute(SessionConstants.JOIN_MEMBER_ID_KEY, 1L));

        verify(memberService).checkMemberDuplicate(any(JoinMemberForm.class));
        verify(memberService).join(any(JoinMemberForm.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 유효성 검증 (비밀번호 불일치)")
    public void join_WhenPasswordMismatch_ShouldReturnJoinViewWithErrors() throws Exception {
        //given
        JoinMemberForm form = createValidJoinForm();
        form.setPasswordConfirm("mismatchedPassword123P!");

        //when & then
        mockMvc.perform(post("/member/join")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("joinForm", form))
                .andExpect(status().isOk())
                .andExpect(view().name("user/member/join"))
                .andExpect(model().attributeHasFieldErrors("joinForm", "passwordConfirm"))
                .andExpect(model().attributeHasFieldErrorCode("joinForm", "passwordConfirm", "error.field.passwordConfirm.mismatch"));

        verify(memberService, never()).checkMemberDuplicate(any(JoinMemberForm.class));
        verify(memberService, never()).join(any(JoinMemberForm.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 중복 회원")
    void join_WhenDuplicateMember_ShouldRedirectToJoinWithFlashAttribute() throws Exception {
        //given
        JoinMemberForm form = createValidJoinForm();
        String errorMessage = "이미 존재하는 회원입니다.";
        doThrow(new DuplicateEntityException(errorMessage))
                .when(memberService).checkMemberDuplicate(any(JoinMemberForm.class));

        //when & then
        mockMvc.perform(post("/member/join")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("joinForm", form))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/member/join"))
                .andExpect(flash().attribute("duplicateError", errorMessage));

        verify(memberService).checkMemberDuplicate(any(JoinMemberForm.class));
        verify(memberService, never()).join(any(JoinMemberForm.class));
    }

    @Test
    @DisplayName("회원가입 완료 페이지 - 세션 유효")
    void joinComplete_WhenSessionIsValid_ShouldShowCompletePage() throws Exception {
        //given
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionConstants.JOIN_MEMBER_ID_KEY, 1L);

        JoinSuccessDto dto = new JoinSuccessDto("testuser1", "테스트유저", "test@example.com");
        when(memberService.joinComplete(1L)).thenReturn(dto);

        //when & then
        mockMvc.perform(get("/member/join/complete")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("user/member/join_complete"))
                .andExpect(model().attribute("memberInfo", dto))
                .andExpect(request().sessionAttributeDoesNotExist(SessionConstants.JOIN_MEMBER_ID_KEY));
    }

    @Test
    @DisplayName("회원가입 완료 페이지 - 세션 무효")
    void joinComplete_WhenSessionIsInvalid_ShouldRedirectToJoin() throws Exception {
        //when & then
        mockMvc.perform(get("/member/join/complete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/member/join"));
    }

    @Test
    @DisplayName("아이디 찾기 폼 요청")
    void findId_ShouldReturnFindIdView() throws Exception {
        //when & then
        mockMvc.perform(get("/member/find-id"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/member/find_id"));
    }

    @Test
    @DisplayName("아이디 찾기 결과 페이지 - 세션 유효")
    void findIdResult_WhenSessionIsValid_ShouldShowResultPage() throws Exception {
        //given
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionConstants.FIND_ID_MEMBER_ID_KEY, 1L);

        AdminAddMembershipForm form = new AdminAddMembershipForm("Basic", 1, 1000, 10000);
        Membership mockMembership = Membership.createMembership(form, 0);
        Member mockMember = Member.createMember(createValidJoinForm(), mockMembership);
        ReflectionTestUtils.setField(mockMember, "createdAt", LocalDateTime.now());

        FindIdResultDto dto = new FindIdResultDto(mockMember);
        when(memberService.findIdComplete(1L)).thenReturn(dto);

        //when & then
        mockMvc.perform(get("/member/find-id/complete")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("user/member/find_id_complete"))
                .andExpect(model().attribute("member", dto))
                .andExpect(request().sessionAttributeDoesNotExist(SessionConstants.FIND_ID_MEMBER_ID_KEY));
    }

    @Test
    @DisplayName("아이디 찾기 결과 페이지 - 세션 무효")
    void findIdResult_WhenSessionIsInvalid_ShouldRedirectToFindId() throws Exception {
        //when & then
        mockMvc.perform(get("/member/find-id/complete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/member/find-id"));
    }

    @Test
    @DisplayName("임시 비밀번호 전송될 이메일 확인 페이지 - 세션 유효")
    void verifyEmail_WhenSessionIsValid_ShouldShowSendTempPasswordPage() throws Exception {
        //given
        MockHttpSession session = new MockHttpSession();
        String testEmail = "test@example.com";
        session.setAttribute(SessionConstants.FIND_PASSWORD_EMAIL_KEY, testEmail);

        //when & then
        mockMvc.perform(get("/member/find-password/send")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("user/member/send_temporary_password"))
                .andExpect(model().attribute("email", testEmail));
    }

    @Test
    @DisplayName("임시 비밀번호 전송될 이메일 확인 페이지 - 세션 무효")
    void verifyEmail_WhenSessionIsInvalid_ShouldRedirectToFindPassword() throws Exception {
        //when & then
        mockMvc.perform(get("/member/find-password/send"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/member/find-password"));
    }
}