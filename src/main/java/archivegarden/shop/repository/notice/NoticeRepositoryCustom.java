package archivegarden.shop.repository.notice;

import archivegarden.shop.dto.admin.AdminSearchCondition;
import archivegarden.shop.dto.user.community.notice.NoticeSearchForm;
import archivegarden.shop.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NoticeRepositoryCustom {

    Page<Notice> findNotices(NoticeSearchForm form, Pageable pageable);

    Page<Notice> findNoticesInAdmin(AdminSearchCondition cond, Pageable pageable);
}
