package archivegarden.shop.controller.admin.admin;

import archivegarden.shop.config.TestSecurityConfig;
import archivegarden.shop.exception.api.EntityNotFoundAjaxException;
import archivegarden.shop.exception.global.EmailSendFailedException;
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

@DisplayName("AdminAdminAjaxController 단위 테스트")
@WebMvcTest(AdminAdminAjaxController.class)
@Import(TestSecurityConfig.class)
@MockBean(JpaMetamodelMappingContext.class)
class AdminAdminAjaxControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminAdminService adminService;

    @DisplayName("로그인 아이디 중복 검사 - 사용 가능한 경우 성공(200) 응답 반환")
    @Test
    void 로그인아이디중복검사_사용가능_성공() throws Exception {
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

    @DisplayName("로그인 아이디 중복 검사 - 이미 사용 중인 경우 오류(400) 응답 반환")
    @Test
    void 로그인아이디중복검사_사용중_오류() throws Exception {
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

    @DisplayName("이메일 중복 검사 - 사용 가능한 경우 성공(200) 응답 반환")
    @Test
    void 이메일중복검사_사용가능_성공() throws Exception {
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

    @DisplayName("이메일 중복 검사 - 이미 사용 중인 경우 오류(400) 응답 반환")
    @Test
    void 이메일중복검사_사용중_오류() throws Exception {
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

    @DisplayName("관리자 삭제 - 정상 삭제 시 성공(200) 응답 반환")
    @Test
    void 관리자삭제_성공() throws Exception {
        //given
        doNothing().when(adminService).deleteAdmin(1L);

        //when & then
        mockMvc.perform(delete("/ajax/admin/admin")
                        .with(csrf())
                        .param("adminId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("관리자가 삭제되었습니다."));

        verify(adminService, times(1)).deleteAdmin(1L);
    }

    @DisplayName("관리자 삭제 - 존재하지 않는 관리자일 경우 예외 발생, 오류(404) 응답 반환")
    @Test
    void 관리자삭제_존재하지않음_예외() throws Exception {
        //given
        doThrow(new EntityNotFoundAjaxException("존재하지 않는 관리자입니다."))
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

    @DisplayName("관리자 권한 부여 - 정상 승인 시 성공(200) 응답 반환")
    @Test
    void 관리자권한부여_성공() throws Exception {
        //given
        doNothing().when(adminService).authorizeAdmin(1L);

        //when & then
        mockMvc.perform(post("/ajax/admin/admin/auth")
                        .with(csrf())
                        .param("adminId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("관리자 권한이 부여되었습니다."));

        verify(adminService, times(1)).authorizeAdmin(1L);
    }

    @DisplayName("관리자 권한 부여 - 존재하지 않는 관리자일 경우 예외 발생, 오류(404) 응답 반환")
    @Test
    void 관리자권한부여_존재하지않음_예외() throws Exception {
        //given
        doThrow(new EntityNotFoundAjaxException("존재하지 않는 관리자입니다."))
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

    @DisplayName("관리자 권한 부여 - 이메일 전송 실패 시 예외 발생, 오류(500) 응답 반환")
    @Test
    void 관리자권한부여_이메일전송실패_예외() throws Exception {
        //given
        doThrow(new EmailSendFailedException("이메일 전송에 실패했습니다."))
                .when(adminService).authorizeAdmin(1L);

        //when & then
        mockMvc.perform(post("/ajax/admin/admin/auth")
                        .with(csrf())
                        .param("adminId", "1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("이메일 전송에 실패했습니다."));
    }
}