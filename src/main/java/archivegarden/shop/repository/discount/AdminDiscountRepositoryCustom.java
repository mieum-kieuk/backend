package archivegarden.shop.repository.discount;

import archivegarden.shop.dto.admin.AdminSearchCondition;
import archivegarden.shop.entity.Discount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AdminDiscountRepositoryCustom {

    Page<Discount> findAll(AdminSearchCondition condition, Pageable pageable);

    Optional<Discount> findByIdWithProducts(Long discountId);
}
