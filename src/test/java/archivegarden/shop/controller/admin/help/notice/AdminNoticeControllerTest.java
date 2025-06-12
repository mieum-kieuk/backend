package archivegarden.shop.controller.admin.help.notice;

import archivegarden.shop.dto.admin.AdminSearchCondition;
import archivegarden.shop.dto.admin.help.notice.AddNoticeForm;
import archivegarden.shop.dto.admin.help.notice.EditNoticeForm;
import archivegarden.shop.dto.admin.help.notice.NoticeDetailsDto;
import archivegarden.shop.dto.admin.help.notice.NoticeListDto;
import archivegarden.shop.exception.common.EntityNotFoundException;
import archivegarden.shop.security.WithMockAdmin;
import archivegarden.shop.service.admin.help.notice.AdminNoticeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

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

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("AdminNoticeController 통합 보안 및 기능 테스트")
class AdminNoticeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminNoticeService noticeService;

    private AddNoticeForm createValidAddNoticeForm() {
        AddNoticeForm form = new AddNoticeForm();
        form.setTitle("공지사항 제목");
        form.setContent("공지사항 내용");
        return form;
    }

    @Nested
    @DisplayName("공지사항 등록 폼 요청")
    class AddNoticeFormTest {

        @Test
        @WithMockAdmin
        @DisplayName("관리자가 등록 폼 요청 시 등록 폼 페이지 반환")
        void 관리자_등록폼_반환() throws Exception {
            //when & then
            mockMvc.perform(get("/admin/notice/add"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("admin/help/notice/add_notice"))
                    .andExpect(model().attributeExists("addNoticeForm"));
        }

        @Test
        @DisplayName("비인증 사용자가 등록 폼 요청 시 로그인 페이지로 이동")
        void 비인증사용자_로그인페이지로_리다이렉트() throws Exception {
            //when & then
            mockMvc.perform(get("/admin/notice/add"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/admin/login"));
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("일반 사용자가 등록 폼 요청 시 접근 거부")
        void 일반사용자_등록폼_접근거부() throws Exception {
            //when & then
            mockMvc.perform(get("/admin/notice/add"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/accessDenied"));
        }
    }

    @Nested
    @DisplayName("공지사항 등록")
    class AddNoticeTest {

        @Test
        @WithMockAdmin
        @DisplayName("유효한 입력으로 등록 요청 시 등록 후 상세 페이지 이동")
        void 관리자_등록_성공() throws Exception {
            //given
            given(noticeService.saveNotice(Mockito.any(), Mockito.any())).willReturn(1L);

            //when & then
            mockMvc.perform(post("/admin/notice/add")
                            .with(csrf())
                            .param("title", "공지사항 제목")
                            .param("content", "공지사항 내용"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/admin/notice/1"));
        }

        @Test
        @WithMockAdmin
        @DisplayName("필수 필드(제목) 누락 시 검증 오류로 등록 폼 재반환")
        void 관리자_등록_유효성실패() throws Exception {
            //given
            AddNoticeForm form = createValidAddNoticeForm();
            form.setTitle("");

            //when & then
            mockMvc.perform(post("/admin/notice/add")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .flashAttr("addNoticeForm", form))
                    .andExpect(status().isOk())
                    .andExpect(view().name("admin/help/notice/add_notice"));
        }

        @Test
        @DisplayName("비인증 사용자가 등록 요청 시 로그인 페이지로 이동")
        void 비인증사용자_로그인페이지로_리다이렉트() throws Exception {
            //given
            AddNoticeForm form = createValidAddNoticeForm();

            //when & then
            mockMvc.perform(post("/admin/notice/add")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .flashAttr("addNoticeForm", form))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/accessDenied"));
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("일반 사용자가 등록 요청 시 접근 거부")
        void 일반사용자_등록_접근거부() throws Exception {
            //given
            AddNoticeForm form = createValidAddNoticeForm();

            //when & then
            mockMvc.perform(post("/admin/notice/add")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .flashAttr("addNoticeForm", form))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/accessDenied"));
        }
    }

    @Nested
    @DisplayName("공지사항 상세 조회")
    class NoticeDetailsTest {

        @Test
        @WithMockAdmin
        @DisplayName("관리자가 상세 조회 요청 시 상세 페이지 반환")
        void 관리자_상세조회_성공() throws Exception {
            //given
            NoticeDetailsDto dto = new NoticeDetailsDto();
            dto.setId(1L);
            dto.setTitle("공지사항 제목");
            dto.setContent("공지사항 내용");
            dto.setCreatedAt(LocalDateTime.now().toString());

            given(noticeService.getNotice(1L)).willReturn(dto);

            //when & then
            mockMvc.perform(get("/admin/notice/1"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("admin/help/notice/notice_details"));
        }

        @Test
        @WithMockAdmin
        @DisplayName("존재하지 않는 공지사항 상세 조회 시 에러 페이지로 이동")
        void 관리자_상세조회_존재X() throws Exception {
            //given
            doThrow(new EntityNotFoundException("존재하지 않는 공지사항입니다."))
                    .when(noticeService).getNotice(1L);

            //when & then
            mockMvc.perform(get("/admin/notice/1"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("error/common/entity_not_found.html"));
        }

        @Test
        @DisplayName("비인증 사용자가 상세 조회 요청 시 로그인 페이지로 이동")
        void 비인증사용자_로그인페이지로_리다이렉트() throws Exception {
            mockMvc.perform(get("/admin/notice/1"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/admin/login"));
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("일반 사용자가 상세 조회 요청 시 접근 거부")
        void 일반사용자_상세조회_접근거부() throws Exception {
            mockMvc.perform(get("/admin/notice/1"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/accessDenied"));
        }
    }

    @Nested
    @DisplayName("공지사항 목록 조회")
    class NoticeListTest {

        @Test
        @WithMockAdmin
        @DisplayName("관리자가 검색 조건 없이 목록 조회 요청 시 리스트 페이지 반환")
        void 관리자_목록조회_검색조건X_성공() throws Exception {
            //given
            NoticeListDto notice1 = new NoticeListDto(1L, "공지사항 제목1", LocalDateTime.now().toString());
            NoticeListDto notice2 = new NoticeListDto(2L, "공지사항 제목2", LocalDateTime.now().toString());
            PageImpl<NoticeListDto> noticePage = new PageImpl<>(List.of(notice1, notice2), PageRequest.of(0, 10), 2);

            given(noticeService.getNotices(any(AdminSearchCondition.class), any(PageRequest.class))).willReturn(noticePage);

            //when & then
            mockMvc.perform(get("/admin/notice"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("admin/help/notice/notice_list"))
                    .andExpect(model().attributeExists("notices"))
                    .andExpect(model().attribute("notices", noticePage));

            verify(noticeService, times(1)).getNotices(any(AdminSearchCondition.class), any(PageRequest.class));
        }

        @Test
        @WithMockAdmin
        @DisplayName("관리자가 검색 조건 포함해 목록 조회 요청 시 페이지 반환")
        void 관리자_목록조회_검색조건O_성공() throws Exception {
            //given
            AdminSearchCondition condition = new AdminSearchCondition();
            condition.setSearchKey("1");
            condition.setKeyword("title");

            NoticeListDto notice1 = new NoticeListDto(1L, "공지사항 제목1", LocalDateTime.now().toString());
            PageImpl<NoticeListDto> noticePage = new PageImpl<>(List.of(notice1), PageRequest.of(0, 10), 1);

            given(noticeService.getNotices(any(AdminSearchCondition.class), any(PageRequest.class))).willReturn(noticePage);

            //when & then
            mockMvc.perform(get("/admin/notice")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .flashAttr("cond", condition))
                    .andExpect(status().isOk())
                    .andExpect(view().name("admin/help/notice/notice_list"))
                    .andExpect(model().attributeExists("notices"))
                    .andExpect(model().attribute("notices", noticePage))
                    .andExpect(model().attribute("notices", hasProperty("content", hasSize(1))));

            verify(noticeService, times(1)).getNotices(any(AdminSearchCondition.class), any(PageRequest.class));
        }

        @Test
        @DisplayName("비인증 사용자가 목록 조회 요청 시 로그인 페이지로 이동")
        void 비인증사용자_로그인페이지로_리다이렉트() throws Exception {
            //when & then
            mockMvc.perform(get("/admin/notice"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/admin/login"));
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("일반 사용자가 목록 조회 요청 시 접근 거부")
        void 일반사용자_목록조회_접근거부() throws Exception {
            //when & then
            mockMvc.perform(get("/admin/notice"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/accessDenied"));
        }
    }

    @Nested
    @DisplayName("공지사항 수정 폼 요청")
    class EditNoticeFormTest {

        @Test
        @WithMockAdmin
        @DisplayName("관리자가 수정 폼 요청 시 수정 폼 페이지 반환")
        void 관리자_수정폼_반환() throws Exception {
            //given
            EditNoticeForm form = new EditNoticeForm(1L, "공지사항 제목", "공지사항 내용");

            given(noticeService.getEditNoticeForm(1L)).willReturn(form);

            //when & then
            mockMvc.perform(get("/admin/notice/1/edit"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("admin/help/notice/edit_notice"))
                    .andExpect(model().attributeExists("editNoticeForm"))
                    .andExpect(model().attribute("editNoticeForm", form));
        }

        @Test
        @DisplayName("비인증 사용자가 수정 폼 요청 시 로그인 페이지로 이동")
        void 비인증사용자_로그인페이지로_리다이렉트() throws Exception {
            mockMvc.perform(get("/admin/notice/1/edit"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/admin/login"));
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("일반 사용자가 수정 폼 요청 시 접근 거부")
        void 일반사용자_수정폼_접근거부() throws Exception {
            //when & then
            mockMvc.perform(get("/admin/notice/1/edit"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/accessDenied"));
        }
    }

    @Nested
    @DisplayName("공지사항 수정")
    class EditNoticeTest {

        @Test
        @WithMockAdmin
        @DisplayName("유효한 입력으로 수정 요청 시 등록 후 상세 페이지 이동")
        void 관리자_수정_성공() throws Exception {
            //when & then
            mockMvc.perform(post("/admin/notice/1/edit")
                            .with(csrf())
                            .param("title", "새 공지사항 제목")
                            .param("content", "새 공지사항 내용"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/admin/notice/1"));
        }

        @Test
        @DisplayName("비인증 사용자가 수정 요청 시 로그인 페이지로 이동")
        void 비인증사용자_로그인페이지로_리다이렉트() throws Exception {
            //when & then
            mockMvc.perform(post("/admin/notice/1/edit")
                            .with(csrf())
                            .param("title", "새 공지사항 제목")
                            .param("content", "새 공지사항 내용"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/admin/login"));
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("일반 사용자가 수정 요청 시 접근 거부")
        void 일반사용자_수정_접근거부() throws Exception {
            //when & then
            mockMvc.perform(post("/admin/notice/1/edit")
                            .with(csrf())
                            .param("title", "새 공지사항 제목")
                            .param("content", "새 공지사항 내용"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/accessDenied"));
        }
    }
}