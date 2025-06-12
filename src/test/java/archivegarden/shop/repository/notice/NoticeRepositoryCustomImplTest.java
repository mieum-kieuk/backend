package archivegarden.shop.repository.notice;

import archivegarden.shop.config.TestAuditingConfig;
import archivegarden.shop.dto.admin.AdminSearchCondition;
import archivegarden.shop.dto.admin.admin.JoinAdminForm;
import archivegarden.shop.dto.admin.help.notice.AddNoticeForm;
import archivegarden.shop.entity.Admin;
import archivegarden.shop.entity.Notice;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestAuditingConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class NoticeRepositoryCustomImplTest {

    @Autowired
    EntityManager em;

    @Autowired
    NoticeRepositoryCustomImpl noticeRepositoryCustom;

    private Notice notice1;
    private Notice notice2;
    private Notice notice3;

    @BeforeEach
    void setUp() {
        TestAuditingConfig.setTime(LocalDateTime.of(2025, 1, 1, 0, 0, 0));

        JoinAdminForm form = new JoinAdminForm();
        form.setLoginId("test1");
        form.setPassword("test1234!");
        form.setPasswordConfirm("test1234!");
        form.setName("테스터");
        form.setEmail("test@example.com");
        Admin admin = Admin.createAdmin(form);
        em.persist(admin);

        AddNoticeForm form1 = new AddNoticeForm();
        form1.setTitle("기존 공지사항1 제목");
        form1.setContent("기존 공지사항2 내용");
        notice1 = Notice.createNotice(form1, admin);
        em.persist(notice1);

        TestAuditingConfig.advanceSeconds(30);

        AddNoticeForm form2 = new AddNoticeForm();
        form2.setTitle("기존 공지사항2 제목");
        form2.setContent("기존 공지사항2 내용");
        notice2 = Notice.createNotice(form2, admin);
        em.persist(notice2);

        TestAuditingConfig.advanceMonth(1);

        AddNoticeForm form3 = new AddNoticeForm();
        form3.setTitle("새 공지사항1 제목");
        form3.setContent("새 공지사항2 내용");
        notice3 = Notice.createNotice(form3, admin);
        em.persist(notice3);

        em.flush();
        em.clear();
    }

    @DisplayName("조건없이 공지사항 목록 조회로 기본 정렬 확인")
    @Test
    void 공지사항_목록조회_검색조건X() {
        //given
        AdminSearchCondition cond = new AdminSearchCondition();
        Pageable pageable = PageRequest.of(0, 10);

        //when
        Page<Notice> resultPage = noticeRepositoryCustom.findNoticesInAdmin(cond, pageable);

        //then
        assertThat(resultPage).isNotNull();
        assertThat(resultPage.getTotalElements()).isEqualTo(3);
        assertThat(resultPage.getContent()).hasSize(3);

        List<Notice> content = resultPage.getContent();

        assertThat(content.get(0).getTitle()).isEqualTo(notice3.getTitle());
        assertThat(content.get(1).getTitle()).isEqualTo(notice2.getTitle());
        assertThat(content.get(2).getTitle()).isEqualTo(notice1.getTitle());
    }

    @DisplayName("검색조건(제목)으로 공지사항 목록 조회")
    @Test
    void 공지사항_목록조회_검색조건O_제목() {
        //given
        AdminSearchCondition cond = new AdminSearchCondition();
        cond.setSearchKey("title");
        cond.setKeyword("기존공지");   //notice1, notice2 포함
        Pageable pageable = PageRequest.of(0, 10);

        //when
        Page<Notice> resultPage = noticeRepositoryCustom.findNoticesInAdmin(cond, pageable);

        //then
        assertThat(resultPage).isNotNull();
        assertThat(resultPage.getTotalElements()).isEqualTo(2);
        assertThat(resultPage.getContent()).hasSize(2);

        List<Notice> content = resultPage.getContent();

        assertThat(content.get(0).getTitle()).isEqualTo(notice2.getTitle());
        assertThat(content.get(1).getTitle()).isEqualTo(notice1.getTitle());
    }

    @DisplayName("검색조건(내용)으로 공지사항 목록 조회")
    @Test
    void 공지사항_목록조회_검색조건O_내용() {
        //given
        AdminSearchCondition cond = new AdminSearchCondition();
        cond.setSearchKey("content");
        cond.setKeyword("새");   //notice3 포함
        Pageable pageable = PageRequest.of(0, 10);

        //when
        Page<Notice> resultPage = noticeRepositoryCustom.findNoticesInAdmin(cond, pageable);

        //then
        assertThat(resultPage).isNotNull();
        assertThat(resultPage.getTotalElements()).isEqualTo(1);
        assertThat(resultPage.getContent()).hasSize(1);

        List<Notice> content = resultPage.getContent();

        assertThat(content.get(0).getTitle()).isEqualTo(notice3.getTitle());
    }

    @DisplayName("일치하는 결과 없게 공지사항 목록 조회")
    @Test
    void 공지사항_목록조회_일치하는결과X() {
        //given
        AdminSearchCondition cond = new AdminSearchCondition();
        cond.setSearchKey("title");
        cond.setKeyword("배송");
        Pageable pageable = PageRequest.of(0, 10);

        //when
        Page<Notice> resultPage = noticeRepositoryCustom.findNoticesInAdmin(cond, pageable);

        //then
        assertThat(resultPage).isNotNull();
        assertThat(resultPage.getTotalElements()).isEqualTo(0);
        assertThat(resultPage.getContent()).isEmpty();
    }
}