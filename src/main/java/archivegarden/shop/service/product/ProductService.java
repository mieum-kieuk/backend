package archivegarden.shop.service.product;

import archivegarden.shop.dto.shop.product.ProductListDto;
import archivegarden.shop.dto.shop.product.ProductSearchCondition;
import archivegarden.shop.entity.Category;
import archivegarden.shop.repository.shop.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductListDto> getMainProducts() {
        return productRepository.findLatestProducts().stream()
                .map(ProductListDto::new)
                .collect(Collectors.toList());
    }

    public Page<ProductListDto> getProducts(ProductSearchCondition condition, Pageable pageable) {
        return productRepository.findAllByCategory(condition, pageable).map(ProductListDto::new);
    }
}