package archivegarden.shop.service.product;

import archivegarden.shop.dto.shop.product.ProductDetailsDto;
import archivegarden.shop.dto.shop.product.ProductListDto;
import archivegarden.shop.dto.shop.product.ProductSearchCondition;
import archivegarden.shop.entity.Product;
import archivegarden.shop.exception.NoSuchProductException;
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

    /**
     * 최신 상품 9개 조회
     */
    public List<ProductListDto> getMainProducts() {
        return productRepository.findLatestProducts().stream()
                .map(ProductListDto::new)
                .collect(Collectors.toList());
    }

    /**
     * 상품 목록 조회 + 페이지네이션
     */
    public Page<ProductListDto> getProducts(ProductSearchCondition condition, Pageable pageable) {
        return productRepository.findAllByCategory(condition, pageable).map(ProductListDto::new);
    }

    /**
     * 상품 단건 조회
     *
     * @throws NoSuchProductException
     */
    public ProductDetailsDto getProduct(Long productId) {
        //엔티티 조회
        Product product = productRepository.findById(productId).orElseThrow(() -> new NoSuchProductException("존재하지 않는 상품입니다."));

        return new ProductDetailsDto(product);
    }
}