package archivegarden.shop.repository.point;

import archivegarden.shop.entity.SavedPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavedPointRepository extends JpaRepository<SavedPoint, Long> {
}
