package archivegarden.shop.repository.community;

import archivegarden.shop.dto.community.notice.NoticeSearchForm;
import archivegarden.shop.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardRepositoryCustom {

    Page<Notice> findNoticeAll(NoticeSearchForm form, Pageable pageable);
}
