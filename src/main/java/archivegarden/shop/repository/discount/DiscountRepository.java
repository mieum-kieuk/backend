package archivegarden.shop.repository.discount;

import archivegarden.shop.entity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DiscountRepository extends JpaRepository<Discount, Long>, AdminDiscountRepositoryCustom {

    Optional<Discount> findByName(String name);
}
