package archivegarden.shop.controller.admin.admin;

import archivegarden.shop.config.TestSecurityConfig;
import archivegarden.shop.constant.AdminSessionConstants;
import archivegarden.shop.dto.admin.AdminSearchCondition;
import archivegarden.shop.dto.admin.admin.AdminListDto;
import archivegarden.shop.dto.admin.admin.JoinAdminForm;
import archivegarden.shop.dto.common.JoinSuccessDto;
import archivegarden.shop.exception.global.DuplicateEntityException;
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

@DisplayName("AdminAdminController 단위 테스트")
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
        form.setEmail("test1@example.com");
        return form;
    }

    @DisplayName("로그인 페이지 - GET 요청 시 페이지 반환")
    @Test
    void 로그인페이지() throws Exception {
        //when & then
        mockMvc.perform(get("/admin/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/login"));
    }

    @DisplayName("로그인 - 오류 발생 시 로그인 페이지와 메시지 반환")
    @Test
    void 로그인_실패_오류메시지반환() throws Exception {
        //when & then
        mockMvc.perform(get("/admin/login")
                        .param("error", "true")
                        .param("exception", "아이디 또는 비밀번호를 확인해 주세요."))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/login"))
                .andExpect(model().attribute("error", true))
                .andExpect(model().attribute("errorMessage", "아이디 또는 비밀번호를 확인해 주세요."));
    }

    @DisplayName("로그아웃 - 직접 URL 요청 시 관리자 홈으로 리다이렉트")
    @Test
    void 로그아웃_직접URL요청_홈리다이렉트() throws Exception {
        //when & then
        mockMvc.perform(get("/admin/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
    }

    @DisplayName("회원가입 - GET 요청 시 등록 폼 반환")
    @Test
    void 회원가입_등록폼반환() throws Exception {
        //when & then
        mockMvc.perform(get("/admin/join"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin/join"))
                .andExpect(model().attributeExists("joinForm"));
    }

    @DisplayName("회원가입 - 유효한 정보 입력 시 완료 페이지로 리다이렉트")
    @Test
    void 회원가입_성공_완료페이지리다이렉트() throws Exception {
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

    @DisplayName("회원가입 - 비밀번호 불일치 시 유효성 검증 실패로 등록 폼 반환")
    @Test
    void 회원가입_유효성검증실패_비밀번호불일치() throws Exception {
        //given
        JoinAdminForm form = createValidJoinForm();
        form.setPasswordConfirm("mismatched1234!");

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

    @DisplayName("회원가입 - 필수 필드 누락 시 유효성 검증 실패로 등록 폼 반환")
    @Test
    void 회원가입_유효성검증실패_필수필드누락() throws Exception {
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

    @DisplayName("회원가입 - 중복 관리자일 경우 등록 폼 반환")
    @Test
    void 회원가입_중복발생_등록폼반환() throws Exception {
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

    @DisplayName("회원가입 완료 - 유효한 세션일 경우 완료 페이지 반환 및 세션 제거")
    @Test
    void 회원가입완료_세션유효_정상반환() throws Exception {
        //given
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(AdminSessionConstants.JOIN_ADMIN_ID_KEY, 1L);

        JoinSuccessDto joinSuccessDto = new JoinSuccessDto("admin1", "테스트", "test@example.com");
        given(adminService.getJoinSuccessInfo(1L)).willReturn(joinSuccessDto);

        //when & then
        mockMvc.perform(get("/admin/join/complete")
                        .session
                                (session))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin/join_complete"))
                .andExpect(model().attributeExists("adminInfo"))
                .andExpect(model().attribute("adminInfo", joinSuccessDto))
                .andExpect(request().sessionAttributeDoesNotExist(AdminSessionConstants.JOIN_ADMIN_ID_KEY));

        verify(adminService, times(1)).getJoinSuccessInfo(1L);
    }

    @DisplayName("회원가입 완료 - 세션이 없을 경우 회원가입 폼 페이지로 리다이렉트")
    @Test
    void 회원가입완료_세션없음_등록폼리다이렉트() throws Exception {
        //given
        MockHttpSession session = new MockHttpSession();

        //when & then
        mockMvc.perform(get("/admin/join/complete")
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/join"));

        verify(adminService, never()).getJoinSuccessInfo(anyLong());
    }

    @DisplayName("관리자 조회 - 검색 조건 없을 경우 전체 목록 반환")
    @Test
    void 관리자조회_검색조건X_전체반환() throws Exception {
        //given
        AdminListDto admin1 = new AdminListDto(1L, "관리자1", "admin1", "admin1@mail.com", true, LocalDateTime.now());
        AdminListDto admin2 = new AdminListDto(2L, "관리자2", "admin2", "admin2@mail.com", false, LocalDateTime.now());
        PageImpl<AdminListDto> adminPage = new PageImpl<>(List.of(admin1, admin2), PageRequest.of(0, 10), 2);

        given(adminService.getAdmins(any(AdminSearchCondition.class), any(PageRequest.class))).willReturn(adminPage);

        //when & then
        mockMvc.perform(get("/admin/admins")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin/admin_list"))
                .andExpect(model().attributeExists("admins"))
                .andExpect(model().attribute("admins", adminPage));

        verify(adminService, times(1)).getAdmins(any(AdminSearchCondition.class), any(PageRequest.class));
    }

    @DisplayName("관리자 조회 - 검색 조건 있을 경우 해당 목록 반환")
    @Test
    void 관리자조회_검색조건O_검색결과반환() throws Exception {
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

    @DisplayName("관리자 조회 - 시작일은 입력하지 않고 종료일만 입력한 경우 오류 메시지 반환")
    @Test
    void 관리자조회_시작일누락_유효성검증실패() throws Exception {
        //when & then
        mockMvc.perform(get("/admin/admins")
                        .param("endDate", "2025-04-01"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin/admin_list"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "시작일시를 입력해 주세요."));

        verify(adminService, never()).getAdmins(any(AdminSearchCondition.class), any(PageRequest.class));
    }

    @DisplayName("관리자 조회 - 시작일이 미래인 경우 유효성 오류 메시지 반환")
    @Test
    void 관리자조회_시작일미래_유효성검증실패() throws Exception {
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

    @DisplayName("관리자 조회 - 시작일이 종료일보다 미래인 경우 유효성 오류 메시지 반환")
    @Test
    void 관리자조회_시작일이종료일보다미래_유효성검증실패() throws Exception {
        //when & then
        mockMvc.perform(get("/admin/admins")
                        .with(csrf())
                        .param("startDate", "2025-05-01")
                        .param("endDate", "2025-04-01"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin/admin_list"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "시작일시가 종료일시보다 과거 날짜여야 합니다."));

        verify(adminService, never()).getAdmins(any(AdminSearchCondition.class), any(PageRequest.class));
    }
}