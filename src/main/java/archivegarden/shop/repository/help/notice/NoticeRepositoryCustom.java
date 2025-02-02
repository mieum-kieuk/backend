package archivegarden.shop.repository.admin.help.notice;

import archivegarden.shop.dto.admin.AdminSearchCondition;
import archivegarden.shop.dto.user.community.notice.NoticeSearchForm;
import archivegarden.shop.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NoticeRepositoryCustom {

    Page<Notice> findNoticeAll(NoticeSearchForm form, Pageable pageable);

    Page<Notice> findAdminNoticeAll(AdminSearchCondition form, Pageable pageable);
}
