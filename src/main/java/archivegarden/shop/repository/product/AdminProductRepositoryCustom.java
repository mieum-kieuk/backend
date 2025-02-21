package archivegarden.shop.repository.product;

import archivegarden.shop.dto.admin.product.product.AdminProductSummaryDto;
import archivegarden.shop.dto.admin.product.product.AdminProductPopupSearchCondition;
import archivegarden.shop.dto.admin.product.product.AdminProductSearchCondition;
import archivegarden.shop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminProductRepositoryCustom {

    Page<Product> findAllProduct(AdminProductSearchCondition condition, Pageable pageable);

    Page<AdminProductSummaryDto> searchProductsInDiscountPopup(AdminProductPopupSearchCondition condition, Pageable pageable);
}
