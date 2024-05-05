package archivegarden.shop.repository.community;

import archivegarden.shop.entity.Qna;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QnaRepository extends JpaRepository<Qna, Long> {

    Page<Qna> findByMemberId(Long memberId, Pageable pageable);
}
