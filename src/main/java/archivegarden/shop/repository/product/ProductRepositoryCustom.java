package archivegarden.shop.repository.product;

import archivegarden.shop.dto.admin.product.product.AdminProductSearchCondition;
import archivegarden.shop.dto.user.product.ProductPopupDto;
import archivegarden.shop.dto.user.product.ProductSearchCondition;
import archivegarden.shop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductRepositoryCustom {

    Product findProduct(Long productId);

    List<Product> findMainProducts();

    Page<Product> searchProducts(String keyword, Pageable pageable);

    Page<Product> findAllByCategory(ProductSearchCondition condition, Pageable pageable);

    Page<ProductPopupDto> findDtoAllPopup(String keyword, Pageable pageable);

    //==관리자 페이지==//
    Page<Product> findProductAll(AdminProductSearchCondition condition, Pageable pageable);

    Optional<Product> findByIdFetchJoin(Long productId);
}
