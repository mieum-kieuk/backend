package archivegarden.shop.security;

import archivegarden.shop.security.annotation.WithMockAdmin;
import archivegarden.shop.security.annotation.WithMockAnonymous;
import archivegarden.shop.security.annotation.WithMockMember;
import archivegarden.shop.service.user.community.NoticeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("인증/인가 통합 테스트")
public class SecurityIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    NoticeService noticeService;

    @Nested
    @DisplayName("비인증 사용자")
    class UnauthenticatedTest {

        @Test
        @DisplayName("비인증 사용자가 존재하지 않는 페이지 요청시 404 에러페이지 반환")
        public void 비인증사용자_존재X페이지요청_404에러페이지반환() throws Exception {
            //when & then
            mockMvc.perform(get("/not-exist"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("비인증 사용자가 공개 페이지 요청시 해당 페이지 반환")
        public void 비인증사용자_공개페이지요청_페이지반환() throws Exception {
            //when & then
            mockMvc.perform(get("/"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("index"));
        }

        @Test
        @DisplayName("비인증 사용자가 USER 권한 페이지 요청시 사용자 로그인 페이지로 리다이렉트")
        public void 비인증사용자_USER페이지요청_로그인리다이렉트() throws Exception {
            //when & then
            mockMvc.perform(get("/community/inquiries/add"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrlPattern("**/login"));
        }

        @Test
        @DisplayName("비인증 사용자가 ADMIN 권한 페이지 요청시 관리자 로그인 페이지로 리다이렉트")
        public void 비인증사용자_ADMIN페이지요청_관리자로그인리다이렉트() throws Exception {
            //when & then
            mockMvc.perform(get("/admin/admins"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/admin/login"));
        }
    }

    @Nested
    @WithMockAnonymous
    @DisplayName("ANONYMOUS 권한 사용자")
    class AnonymousTest {

        @Test
        @DisplayName("ANONYMOUS 권한 사용자가 존재하지 않는 페이지 요청시 404 에러페이지 반환")
        public void ANONYMOUS권한_존재X페이지요청_404에러페이지반환() throws Exception {
            //when & then
            mockMvc.perform(get("/not-exist"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("ANONYMOUS 권한 사용자가 공개 페이지 요청시 해당 페이지 반환")
        public void ANONYMOUS권한_공개페이지요청_페이지반환() throws Exception {
            //when & then
            mockMvc.perform(get("/"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("index"));
        }

        @Test
        @DisplayName("ANONYMOUS 권한 사용자가 USER 권한 페이지 요청시 관리자 로그아웃 후 사용자 로그인 페이지로 리다이렉트")
        public void ANONYMOUS권한_USER페이지요청_페이지반환() throws Exception {
            //when & then
            mockMvc.perform(get("/community/inquiries/add"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/login"));
        }

        @Test
        @DisplayName("ANONYMOUS 권한 사용자가 ADMIN 페이지 요청시 권한 없음 페이지로 리다이렉트")
        public void ANONYMOUS권한_ADMIN페이지요청_페이지반환() throws Exception {
            //when & then
            mockMvc.perform(get("/admin/admins"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/access-denied"));
        }
    }

    @Nested
    @WithMockMember
    @DisplayName("USER 권한 사용자")
    class UserTest {

        @Test
        @DisplayName("USER 권한 사용자가 존재하지 않는 페이지 요청시 404 에러페이지 반환")
        public void USER권한_존재X페이지요청_404에러페이지반환() throws Exception {
            //when & then
            mockMvc.perform(get("/not-exist"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("USER 권한 사용자가 공개 페이지 요청시 해당 페이지 반환")
        public void USER권한_공개페이지요청_페이지반환() throws Exception {
            //when & then
            mockMvc.perform(get("/"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("index"));
        }

        @Test
        @DisplayName("USER 권한 사용자가 USER 권한 페이지 요청시 해당 페이지 반환")
        public void USER권한_USER페이지요청_페이지반환() throws Exception {
            //when & then
            mockMvc.perform(get("/community/inquiries/add"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("user/community/inquiry/add_inquiry"))
                    .andExpect(model().attributeExists("form"));
        }

        @Test
        @DisplayName("USER 권한 사용자가 ADMIN 페이지 요청시 권한 없음 페이지로 리다이렉트")
        public void USER권한_ADMIN페이지요청_권한없음리다이렉트() throws Exception {
            //when & then
            mockMvc.perform(get("/admin/admins"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/access-denied"));
        }
    }

    @Nested
    @WithMockAdmin
    @DisplayName("ADMIN 권한 사용자")
    class AdminTest {

        @Test
        @DisplayName("ADMIN 권한 사용자가 존재하지 않는 페이지 요청시 404 에러페이지 반환")
        public void ADMIN권한_존재X페이지요청_404에러페이지반환() throws Exception {
            //when & then
            mockMvc.perform(get("/not-exist"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("ADMIN 권한 사용자가 공개 페이지 요청시 해당 페이지 반환")
        public void ADMIN권한_공개페이지요청_페이지반환() throws Exception {
            //when & then
            mockMvc.perform(get("/"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("index"));
        }

        @Test
        @DisplayName("ADMIN 권한 사용자가 USER 권한 페이지 요청시 관리자 로그아웃 후 사용자 로그인 페이지로 리다이렉트")
        public void ADMIN권한_USER페이지요청_페이지반환() throws Exception {
            //when & then
            mockMvc.perform(get("/community/inquiries/add"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/login"));
        }

        @Test
        @WithMockAdmin
        @DisplayName("ADMIN 권한 사용자가 ADMIN 페이지 요청시 해당 페이지 반환")
        public void ADMIN권한_ADMIN페이지요청_페이지반환() throws Exception {
            //when & then
            mockMvc.perform(get("/admin/admins"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("admin/admin/admin_list"));
        }
    }
}
