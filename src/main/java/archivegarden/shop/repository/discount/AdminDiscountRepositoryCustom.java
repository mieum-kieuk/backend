package archivegarden.shop.repository.admin.promotion;

import archivegarden.shop.dto.admin.AdminSearchForm;
import archivegarden.shop.entity.Discount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminDiscountRepositoryCustom {

    Page<Discount> findAll(AdminSearchForm form, Pageable pageable);
}
