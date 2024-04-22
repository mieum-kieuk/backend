package archivegarden.shop.repository.community;

import archivegarden.shop.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardRepositoryCustom {

    @Query("select b from Board b join fetch b.member where b.id = :noticeId")
    Optional<Board> findByIdWithMember(@Param("noticeId") Long noticeId);
}
