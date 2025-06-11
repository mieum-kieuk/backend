package archivegarden.shop.controller.admin.admin;

import archivegarden.shop.config.TestSecurityConfig;
import archivegarden.shop.exception.ajax.AjaxEntityNotFoundException;
import archivegarden.shop.service.admin.admin.AdminAdminService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminAdminAjaxController.class)
@Import(TestSecurityConfig.class)
@MockBean(JpaMetamodelMappingContext.class)
class AdminAdminAjaxControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminAdminService adminService;

    @DisplayName("로그인 아이디 중복 검사 - 사용 가능")
    @Test
    void checkLoginIdDuplicate_Available() throws Exception {
        //given
        String availableId = "availableId";
        given(adminService.isLoginIdAvailable(availableId)).willReturn(true);

        //when & then
        mockMvc.perform(post("/ajax/admin/check/loginId")
                        .with(csrf())
                        .param("loginId", availableId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("사용 가능한 아이디입니다."));

        verify(adminService, times(1)).isLoginIdAvailable(availableId);
    }

    @DisplayName("로그인 아이디 중복 검사 - 이미 사용 중")
    @Test
    void checkLoginIdDuplicate_NotAvailable() throws Exception {
        //given
        String takenId = "takenId";
        given(adminService.isLoginIdAvailable(takenId)).willReturn(false);

        //when & then
        mockMvc.perform(post("/ajax/admin/check/loginId")
                        .with(csrf())
                        .param("loginId", takenId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("이미 사용 중인 아이디입니다."));

        verify(adminService, times(1)).isLoginIdAvailable(takenId);
    }

    @DisplayName("이메일 중복 검사 - 사용 가능")
    @Test
    void checkEmailDuplicate_Available() throws Exception {
        //given
        String availableEmail = "available@example.com";
        given(adminService.isEmailAvailable(availableEmail)).willReturn(true);

        //when & then
        mockMvc.perform(post("/ajax/admin/check/email")
                        .with(csrf())
                        .param("email", availableEmail))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("사용 가능한 이메일입니다."));

        verify(adminService, times(1)).isEmailAvailable(availableEmail);
    }

    @DisplayName("이메일 중복 검사 - 이미 사용 중")
    @Test
    void checkEmailDuplicate_NotAvailable() throws Exception {
        //given
        String takenEmail = "taken@example.com";
        given(adminService.isEmailAvailable(takenEmail)).willReturn(false);

        //when & then
        mockMvc.perform(post("/ajax/admin/check/email")
                        .with(csrf())
                        .param("email", takenEmail))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("이미 사용 중인 이메일입니다."));

        verify(adminService, times(1)).isEmailAvailable(takenEmail);
    }

    @DisplayName("관리자 삭제 - 성공")
    @Test
    void deleteAdmin_Success() throws Exception {
        //given
        doNothing().when(adminService).deleteAdmin(1L);

        //when & then
        mockMvc.perform(delete("/ajax/admin/admin")
                        .with(csrf())
                        .param("adminId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("삭제되었습니다."));

        verify(adminService, times(1)).deleteAdmin(1L);
    }

    @DisplayName("관리자 삭제 - 존재하지 않는 관리자")
    @Test
    void deleteAdmin_NotFound() throws Exception {
        //given
        doThrow(new AjaxEntityNotFoundException("존재하지 않는 관리자입니다."))
                .when(adminService).deleteAdmin(1L);

        //when & then
        mockMvc.perform(delete("/ajax/admin/admin")
                        .with(csrf())
                        .param("adminId", "1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("존재하지 않는 관리자입니다."));

        verify(adminService, times(1)).deleteAdmin(1L);
    }

    @DisplayName("관리자 권한 부여 - 성공")
    @Test
    void authorizeAdmin_Success() throws Exception {
        //given
        doNothing().when(adminService).authorizeAdmin(1L);

        //when & then
        mockMvc.perform(post("/ajax/admin/admin/auth")
                        .with(csrf())
                        .param("adminId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("승인되었습니다."));

        verify(adminService, times(1)).authorizeAdmin(1L);
    }

    @DisplayName("관리자 권한 부여 - 존재하지 않는 관리자")
    @Test
    void authorizeAdmin_NotFound() throws Exception {
        //given
        doThrow(new AjaxEntityNotFoundException("존재하지 않는 관리자입니다."))
                .when(adminService).authorizeAdmin(1L);

        //when & then
        mockMvc.perform(post("/ajax/admin/admin/auth")
                        .with(csrf())
                        .param("adminId", "1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("존재하지 않는 관리자입니다."));

        verify(adminService, times(1)).authorizeAdmin(1L);
    }
}