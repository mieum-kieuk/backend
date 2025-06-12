package archivegarden.shop.service.admin.help.notice;

import archivegarden.shop.dto.admin.AdminSearchCondition;
import archivegarden.shop.dto.admin.help.notice.AddNoticeForm;
import archivegarden.shop.dto.admin.help.notice.EditNoticeForm;
import archivegarden.shop.dto.admin.help.notice.NoticeDetailsDto;
import archivegarden.shop.dto.admin.help.notice.NoticeListDto;
import archivegarden.shop.entity.Admin;
import archivegarden.shop.entity.Notice;
import archivegarden.shop.exception.ajax.AjaxEntityNotFoundException;
import archivegarden.shop.exception.common.EntityNotFoundException;
import archivegarden.shop.repository.notice.NoticeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminNoticeServiceTest {

    @Mock
    private NoticeRepository noticeRepository;

    @InjectMocks
    private AdminNoticeService adminNoticeService;

    @Mock
    private Admin admin;

    private AddNoticeForm createValidAddNoticeForm() {
        AddNoticeForm form = new AddNoticeForm();
        form.setTitle("공지사항 제목");
        form.setContent("공지사항 내용");
        return form;
    }

    @Test
    @DisplayName("공지사항 저장 시 noticeId 반환")
    void 공지사항_저장_성공() {
        //given
        AddNoticeForm form = createValidAddNoticeForm();

        doAnswer(invocation -> {
            Notice arg = invocation.getArgument(0);
            ReflectionTestUtils.setField(arg, "id", 1L);
            return arg;

        }).when(noticeRepository).save(any(Notice.class));

        //when
        Long savedNoticeId = adminNoticeService.saveNotice(form, admin);

        //then
        assertThat(savedNoticeId).isEqualTo(1L);
        verify(noticeRepository, times(1)).save(any(Notice.class));
    }

    @Test
    @DisplayName("공지사항 상세 조회시 NoticeDetailsDto 반환")
    void 공지사항_상세조회_성공() {
        //given
        Long noticeId = 1L;
        AddNoticeForm form = createValidAddNoticeForm();
        Notice notice = Notice.createNotice(form, admin);
        ReflectionTestUtils.setField(notice, "id", noticeId);
        ReflectionTestUtils.setField(notice, "createdAt", LocalDateTime.now());

        given(noticeRepository.findById(noticeId)).willReturn(Optional.of(notice));

        //when
        NoticeDetailsDto result = adminNoticeService.getNotice(noticeId);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(noticeId);
        assertThat(result.getTitle()).isEqualTo(form.getTitle());
        assertThat(result.getContent()).isEqualTo(form.getContent());
        verify(noticeRepository, times(1)).findById(noticeId);
    }

    @Test
    @DisplayName("존재하지 않는 공지사항 상세 조회 시 EntityNotFoundException 발생")
    void 공지사항_상세조회_존재X() {
        //given
        given(noticeRepository.findById(1L)).willReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> adminNoticeService.getNotice(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("존재하지 않는 공지사항입니다.");

        verify(noticeRepository, times(1)).findById(1L);
    }

    @DisplayName("공지사항 목록 조회시 페이지 반환")
    @Test
    void 공지사항_목록조회_성공() {
        AdminSearchCondition cond = new AdminSearchCondition();
        PageRequest pageable = PageRequest.of(0, 10);
        AddNoticeForm form = createValidAddNoticeForm();
        Notice notice = Notice.createNotice(form, admin);
        ReflectionTestUtils.setField(notice, "id", 1L);
        ReflectionTestUtils.setField(notice, "createdAt", LocalDateTime.now());

        PageImpl<Notice> noticePage = new PageImpl<>(List.of(notice), pageable, 1);

        given(noticeRepository.findNoticesInAdmin(cond, pageable)).willReturn(noticePage);

        //when
        Page<NoticeListDto> resultPage = adminNoticeService.getNotices(cond, pageable);

        //then
        assertThat(resultPage).isNotNull();
        assertThat(resultPage.getTotalElements()).isEqualTo(1);
        assertThat(resultPage.getContent()).hasSize(1);
        assertThat(resultPage.getContent().get(0).getId()).isEqualTo(1L);
        assertThat(resultPage.getContent().get(0).getTitle()).isEqualTo(form.getTitle());
        verify(noticeRepository, times(1)).findNoticesInAdmin(cond, pageable);
    }

    @Test
    @DisplayName("수정 폼 조회 시 EditNoticeForm 반환")
    void 공지사항_수정폼_성공() {
        //given
        AddNoticeForm form = createValidAddNoticeForm();
        Notice notice = Notice.createNotice(form, admin);
        ReflectionTestUtils.setField(notice, "id", 1L);

        given(noticeRepository.findById(1L)).willReturn(Optional.of(notice));

        //when
        EditNoticeForm result = adminNoticeService.getEditNoticeForm(1L);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(form.getTitle());
        assertThat(result.getContent()).isEqualTo(form.getContent());
    }

    @Test
    @DisplayName("수정 폼 조회 시 해당 공지사항 없으면 EntityNotFoundException 발생")
    void 공지사항_수정폼_존재X() {
        //given
        given(noticeRepository.findById(1L)).willReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> adminNoticeService.getEditNoticeForm(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("존재하지 않는 공지사항입니다.");

        verify(noticeRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("공지사항 수정 시 제목과 내용 업데이트")
    void 공지사항_수정_성공() {
        //given
        EditNoticeForm form = new EditNoticeForm();
        form.setTitle("새 공지사항 제목");
        form.setContent("새 공지사항 내용");
        Notice notice = mock(Notice.class);

        when(noticeRepository.findById(1L)).thenReturn(Optional.of(notice));

        //when
        adminNoticeService.editNotice(1L, form);

        //then
        verify(notice).update("새 공지사항 제목", "새 공지사항 내용");
    }

    @Test
    @DisplayName("존재하지 않는 공지사항 수정 시 EntityNotFoundException 발생")
    void 공지사항_수정_존재X() {
        //given
        EditNoticeForm form = new EditNoticeForm();

        when(noticeRepository.findById(1L)).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() ->  adminNoticeService.editNotice(1L, form))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("존재하지 않는 공지사항입니다.");
    }

    @Test
    @DisplayName("공지사항 삭제")
    void 공지사항_삭제_성공() {
        //given
        AddNoticeForm form = createValidAddNoticeForm();
        Notice notice = Notice.createNotice(form, admin);

        when(noticeRepository.findById(1L)).thenReturn(Optional.of(notice));

        //when
        adminNoticeService.deleteNotice(1L);

        //then
        verify(noticeRepository).delete(notice);
    }

    @Test
    @DisplayName("존재하지 않는 공지사항 삭제 시 AjaxEntityNotFoundException 발생")
    void 공지사항_삭제_존재X() {
        //when
        when(noticeRepository.findById(1L)).thenReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> adminNoticeService.deleteNotice(1L))
                .isInstanceOf(AjaxEntityNotFoundException.class)
                .hasMessage("존재하지 않는 공지사항입니다.");
    }
}