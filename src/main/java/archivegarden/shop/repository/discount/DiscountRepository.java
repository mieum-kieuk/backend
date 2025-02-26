package archivegarden.shop.repository.discount;

import archivegarden.shop.entity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DiscountRepository extends JpaRepository<Discount, Long>, AdminDiscountRepositoryCustom {

    @Query("select d from Discount d where d.startedAt > current_timestamp")
    List<Discount> findNewDiscounts();
}
