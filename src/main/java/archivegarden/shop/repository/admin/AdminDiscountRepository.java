package archivegarden.shop.repository.admin;

import archivegarden.shop.entity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminDiscountRepository extends JpaRepository<Discount, Long> {
}
