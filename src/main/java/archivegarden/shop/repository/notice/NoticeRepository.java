package archivegarden.shop.repository.notice;

import archivegarden.shop.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long>, NoticeRepositoryCustom {
}
