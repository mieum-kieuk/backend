package archivegarden.shop.repository.admin.promotion;

import archivegarden.shop.entity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminDiscountRepository extends JpaRepository<Discount, Long>, AdminDiscountRepositoryCustom {
}
