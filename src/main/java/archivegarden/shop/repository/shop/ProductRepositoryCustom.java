package archivegarden.shop.repository.shop;

import archivegarden.shop.dto.shop.product.ProductSearchCondition;
import archivegarden.shop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductRepositoryCustom {

    List<Product> findLatestProducts();

    Page<Product> findAllByCategory(ProductSearchCondition condition, Pageable pageable);

    Page<Product> search(String keyword, Pageable pageable);

    Page<Product> findAllPopup(Pageable pageable, String keyword);
}
