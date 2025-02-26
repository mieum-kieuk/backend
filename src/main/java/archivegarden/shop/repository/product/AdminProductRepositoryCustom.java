package archivegarden.shop.repository.product;

import archivegarden.shop.dto.admin.product.product.AdminProductPopupSearchCondition;
import archivegarden.shop.dto.admin.product.product.AdminProductSearchCondition;
import archivegarden.shop.dto.admin.product.product.AdminProductSummaryDto;
import archivegarden.shop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AdminProductRepositoryCustom {

    Optional<Product> findProductInAdmin(Long productId);

    Page<Product> findAllProductInAdmin(AdminProductSearchCondition condition, Pageable pageable);

    Page<AdminProductSummaryDto> searchProductsInDiscountPopup(AdminProductPopupSearchCondition condition, Pageable pageable);
}
