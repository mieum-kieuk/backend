package archivegarden.shop.repository.discount;

import archivegarden.shop.entity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountRepository extends JpaRepository<Discount, Long>, AdminDiscountRepositoryCustom {
}
