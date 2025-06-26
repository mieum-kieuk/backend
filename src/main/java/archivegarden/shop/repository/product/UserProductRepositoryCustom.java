package archivegarden.shop.repository.product;

import archivegarden.shop.dto.user.product.ProductPopupSearchCondition;
import archivegarden.shop.dto.user.product.ProductSearchCondition;
import archivegarden.shop.dto.user.product.ProductSummaryDto;
import archivegarden.shop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserProductRepositoryCustom {

    Optional<Product> findProduct(Long productId);

    Page<Product> findProductsByCategory(ProductSearchCondition cond, Pageable pageable);

    Page<Product> searchProducts(String keyword, Pageable pageable);

    List<Product> findLatestProducts();

    Page<ProductSummaryDto> searchProductsInInquiryPopup(ProductPopupSearchCondition cond, Pageable pageable);
}
