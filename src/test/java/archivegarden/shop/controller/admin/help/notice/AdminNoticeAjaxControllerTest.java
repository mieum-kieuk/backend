package archivegarden.shop.controller.admin.help.notice;

import archivegarden.shop.config.TestSecurityConfig;
import archivegarden.shop.exception.ajax.AjaxEntityNotFoundException;
import archivegarden.shop.service.admin.help.notice.AdminNoticeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminNoticeAjaxController.class)
@Import(TestSecurityConfig.class)
@MockBean(JpaMetamodelMappingContext.class)
class AdminNoticeAjaxControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminNoticeService noticeService;

    @DisplayName("공지사항 삭제 후 200 반환")
    @Test
    void 삭제_성공() throws Exception {
        //given
        doNothing().when(noticeService).deleteNotice(1L);

        //when & then
        mockMvc.perform(delete("/ajax/admin/notice")
                        .with(csrf())
                        .param("noticeId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("삭제되었습니다."));

        verify(noticeService, times(1)).deleteNotice(1L);
    }

    @DisplayName("존재하지 않는 공지사항 삭제 시 404 반환")
    @Test
    void 삭제_존재X() throws Exception {
        //given
        doThrow(new AjaxEntityNotFoundException("존재하지 않는 공지사항입니다.")).when(noticeService).deleteNotice(1L);

        //when & then
        mockMvc.perform(delete("/ajax/admin/notice")
                        .with(csrf())
                        .param("noticeId", "1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("존재하지 않는 공지사항입니다."));

        verify(noticeService, times(1)).deleteNotice(1L);
    }
}