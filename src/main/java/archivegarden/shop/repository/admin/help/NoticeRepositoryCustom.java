package archivegarden.shop.repository.admin.help;

import archivegarden.shop.dto.admin.help.notice.NoticeSearchForm;
import archivegarden.shop.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NoticeRepositoryCustom {

    Page<Notice> findNoticeAll(NoticeSearchForm form, Pageable pageable);
}
