package archivegarden.shop.repository.product;

import archivegarden.shop.dto.user.product.ProductSearchCondition;
import archivegarden.shop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserProductRepositoryCustom {

    List<Product> findLatestProducts();

    Page<Product> searchProducts(String keyword, Pageable pageable);

    Optional<Product> findProduct(Long productId);

    Page<Product> findProductsByCategory(ProductSearchCondition condition, Pageable pageable);
}
