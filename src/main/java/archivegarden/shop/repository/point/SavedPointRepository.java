package archivegarden.shop.repository.point;

import archivegarden.shop.entity.SavedPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SavedPointRepository extends JpaRepository<SavedPoint, Long> {

    @Query(nativeQuery = true, value = "SELECT BALANCE FROM SAVED_POINT WHERE MEMBER_ID = :memberId ORDER BY CREATED_AT DESC LIMIT 1")
    Integer findBalance(@Param("memberId") Long memberId);
}
