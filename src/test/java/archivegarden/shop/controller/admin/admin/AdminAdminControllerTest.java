package archivegarden.shop.controller.admin.admin;

import archivegarden.shop.config.TestSecurityConfig;
import archivegarden.shop.constant.AdminSessionConstants;
import archivegarden.shop.dto.admin.AdminSearchCondition;
import archivegarden.shop.dto.admin.admin.AdminListDto;
import archivegarden.shop.dto.admin.admin.JoinAdminForm;
import archivegarden.shop.dto.common.JoinSuccessDto;
import archivegarden.shop.exception.common.DuplicateEntityException;
import archivegarden.shop.service.admin.admin.AdminAdminService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminAdminController.class)
@Import(TestSecurityConfig.class)
@MockBean(JpaMetamodelMappingContext.class)
class AdminAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminAdminService adminService;

    private JoinAdminForm createValidJoinForm() {
        JoinAdminForm form = new JoinAdminForm();
        form.setLoginId("test1");
        form.setPassword("test1234!");
        form.setPasswordConfirm("test1234!");
        form.setName("테스터");
        form.setEmail("test@example.com");
        return form;
    }

    @DisplayName("관리자 로그인 페이지 요청 - 성공")
    @Test
    void login_Success() throws Exception {
        //when & then
        mockMvc.perform(get("/admin/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/login"));
    }

    @DisplayName("관리자 로그인 페이지 요청 - 에러 메시지 포함")
    @Test
    void login_WithError() throws Exception {
        //when & then
        mockMvc.perform(get("/admin/login")
                        .param("error", "true")
                        .param("exception", "에러 메시지"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/login"))
                .andExpect(model().attribute("error", true))
                .andExpect(model().attribute("errorMessage", "에러 메시지"));
    }

    @DisplayName("직접 로그아웃 경로 접근 통해 로그아웃 요청 - 홈으로 리다이렉트")
    @Test
    void redirectToHome_Success() throws Exception {
        //when & then
        mockMvc.perform(get("/admin/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
    }

    @DisplayName("관리자 회원가입 폼 요청 - 성공")
    @Test
    void addAdminForm_Success() throws Exception {
        //when & then
        mockMvc.perform(get("/admin/join"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin/join"))
                .andExpect(model().attributeExists("joinForm"));
    }

    @DisplayName("관리자 회원가입 요청 - 성공")
    @Test
    void join_Success() throws Exception {
        //given
        JoinAdminForm form = createValidJoinForm();
        MockHttpSession session = new MockHttpSession();

        given(adminService.join(any(JoinAdminForm.class))).willReturn(1L);

        //when & then
        mockMvc.perform(post("/admin/join")
                        .with(csrf())
                        .session(session)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("joinForm", form))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/join/complete"))
                .andExpect(request().sessionAttribute(AdminSessionConstants.JOIN_ADMIN_ID_KEY, 1L));

        verify(adminService, times(1)).checkAdminDuplicate(any(JoinAdminForm.class));
        verify(adminService, times(1)).join(any(JoinAdminForm.class));
    }

    @DisplayName("관리자 회원가입 요청 - 비밀번호 불일치")
    @Test
    void join_PasswordMismatch() throws Exception {
        //given
        JoinAdminForm form = createValidJoinForm();
        form.setPasswordConfirm("mismatchedPassword1234!");

        //when & then
        mockMvc.perform(post("/admin/join")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("joinForm", form))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin/join"))
                .andExpect(model().attributeHasFieldErrors("joinForm", "passwordConfirm"))
                .andExpect(model().attributeHasFieldErrorCode("joinForm", "passwordConfirm", "error.field.passwordConfirm.mismatch"));

        verify(adminService, never()).checkAdminDuplicate(any(JoinAdminForm.class));
        verify(adminService, never()).join(any(JoinAdminForm.class));
    }

    @DisplayName("관리자 회원가입 요청 - 필수 필드 누락")
    @Test
    void join_ValidationErrors() throws Exception {
        //given
        JoinAdminForm form = createValidJoinForm();
        form.setName("");   //필수 필드 name 누락

        //when & then
        mockMvc.perform(post("/admin/join")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("joinForm", form))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin/join"))
                .andExpect(model().attributeHasFieldErrors("joinForm", "name"));

        verify(adminService, never()).checkAdminDuplicate(any(JoinAdminForm.class));
        verify(adminService, never()).join(any(JoinAdminForm.class));
    }

    @DisplayName("관리자 회원가입 요청 - 중복 관리자 예외 발생")
    @Test
    void join_DuplicateAdminException() throws Exception {
        //given
        JoinAdminForm form = createValidJoinForm();
        doThrow(new DuplicateEntityException("이미 존재하는 관리자입니다."))
                .when(adminService).checkAdminDuplicate(any(JoinAdminForm.class));

        //when & then
        mockMvc.perform(post("/admin/join")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("joinForm", form))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin/join"))
                .andExpect(model().attributeHasErrors("joinForm"));

        verify(adminService, times(1)).checkAdminDuplicate(any(JoinAdminForm.class));
        verify(adminService, never()).join(any(JoinAdminForm.class));
    }

    @DisplayName("회원가입 완료 페이지 요청 - 성공 (세션 유효)")
    @Test
    void joinComplete_Success() throws Exception {
        //given
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(AdminSessionConstants.JOIN_ADMIN_ID_KEY, 1L);

        JoinSuccessDto joinSuccessDto = new JoinSuccessDto("admin1", "테스트", "test@example.com");
        given(adminService.getJoinSuccessInfo(1L)).willReturn(joinSuccessDto);

        //when & then
        mockMvc.perform(get("/admin/join/complete")
                        .with(csrf())
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin/join_complete"))
                .andExpect(model().attributeExists("adminInfo"))
                .andExpect(model().attribute("adminInfo", joinSuccessDto))
                .andExpect(request().sessionAttributeDoesNotExist(AdminSessionConstants.JOIN_ADMIN_ID_KEY));

        verify(adminService, times(1)).getJoinSuccessInfo(1L);
    }

    @DisplayName("회원가입 완료 페이지 요청 - 세션 무효")
    @Test
    void joinComplete_NoAdminIdInSession() throws Exception {
        //given
        MockHttpSession session = new MockHttpSession();

        //when & then
        mockMvc.perform(get("/admin/join/complete")
                        .with(csrf())
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/join"));

        verify(adminService, never()).getJoinSuccessInfo(anyLong());
    }

    @DisplayName("전체 관리자 조회 - 성공 (검색 조건 없음)")
    @Test
    void admins_NoSearchCondition_Success() throws Exception {
        //given
        AdminListDto admin1 = new AdminListDto(1L, "관리자1", "admin1", "admin1@mail.com", true, LocalDateTime.now());
        AdminListDto admin2 = new AdminListDto(2L, "관리자2", "admin2", "admin2@mail.com", false, LocalDateTime.now());
        PageImpl<AdminListDto> adminPage = new PageImpl<>(List.of(admin1, admin2), PageRequest.of(0, 10), 2);

        given(adminService.getAdmins(any(AdminSearchCondition.class), any(PageRequest.class))).willReturn(adminPage);

        //when & then
        mockMvc.perform(get("/admin/admins"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin/admin_list"))
                .andExpect(model().attributeExists("admins"))
                .andExpect(model().attribute("admins", adminPage));

        verify(adminService, times(1)).getAdmins(any(AdminSearchCondition.class), any(PageRequest.class));
    }

    @DisplayName("전체 관리자 조회 - 성공 (검색 조건 포함)")
    @Test
    void admins_WithSearchCondition_Success() throws Exception {
        //given
        AdminSearchCondition condition = new AdminSearchCondition();
        condition.setSearchKey("admin");
        condition.setStartDate(LocalDate.of(2025, 4, 1));
        condition.setEndDate(LocalDate.of(2025, 5, 1));
        AdminListDto admin1 = new AdminListDto(1L, "관리자1", "admin1", "admin1@mail.com", true, LocalDateTime.of(2025, 4, 5, 12, 10));
        PageImpl<AdminListDto> adminPage = new PageImpl<>(List.of(admin1), PageRequest.of(0, 10), 1);

        given(adminService.getAdmins(any(AdminSearchCondition.class), any(PageRequest.class))).willReturn(adminPage);

        //when & then
        mockMvc.perform(get("/admin/admins")
                        .param("keyword", "admin")
                        .param("startDate", "2025-04-01")
                        .param("endDate", "2025-05-01"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin/admin_list"))
                .andExpect(model().attributeExists("admins"))
                .andExpect(model().attribute("admins", adminPage))
                .andExpect(model().attribute("admins", hasProperty("content", hasSize(1))));

        verify(adminService, times(1)).getAdmins(any(AdminSearchCondition.class), any(PageRequest.class));
    }

    @DisplayName("전체 관리자 조회 - 날짜 유효성 검증 실패 (시작일시 없음, 종료일시 있음)")
    @Test
    void admins_DateValidationFail_StartDateNull() throws Exception {
        //when & then
        mockMvc.perform(get("/admin/admins")
                        .param("endDate", "2025-04-01"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin/admin_list"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "시작일시를 입력해 주세요."));

        verify(adminService, never()).getAdmins(any(AdminSearchCondition.class), any(PageRequest.class));
    }

    @DisplayName("전체 관리자 조회 - 날짜 유효성 검증 실패 (시작일시가 현재보다 미래)")
    @Test
    void admins_DateValidationFail_StartDateInFuture() throws Exception {
        //when & then
        LocalDate futureDate = LocalDate.now().plusDays(1);
        mockMvc.perform(get("/admin/admins")
                        .param("startDate", futureDate.toString())
                        .param("endDate", futureDate.plusDays(1).toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin/admin_list"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "시작일시는 과거 또는 현재 날짜여야 합니다."));

        verify(adminService, never()).getAdmins(any(AdminSearchCondition.class), any(PageRequest.class));
    }

    @DisplayName("전체 관리자 조회 - 날짜 유효성 검증 실패 (시작일시가 종료일시보다 미래)")
    @Test
    void admins_DateValidationFail_StartDateAfterEndDate() throws Exception {
        //when & then
        mockMvc.perform(get("/admin/admins")
                        .param("startDate", "2025-05-01")
                        .param("endDate", "2025-04-01"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin/admin_list"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "시작일시가 종료일시보다 과거 날짜여야 합니다."));

        verify(adminService, never()).getAdmins(any(AdminSearchCondition.class), any(PageRequest.class));
    }
}