package archivegarden.shop.repository.product;

import archivegarden.shop.dto.community.inquiry.ProductPopupDto;
import archivegarden.shop.dto.product.ProductSearchCondition;
import archivegarden.shop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductRepositoryCustom {

    Product findProduct(Long productId);

    List<Product> findMainProducts();

    Page<Product> findAllByCategory(ProductSearchCondition condition, Pageable pageable);

    Page<Product> search(String keyword, Pageable pageable);

    Page<ProductPopupDto> findDtoAllPopup(String keyword, Pageable pageable);

    //==관리자 페이지==//
//    Page<ProductListDto> findAdminDtoAll(ProductSearchForm form, Pageable pageable);
}
