package archivegarden.shop.service.product;

import archivegarden.shop.dto.community.inquiry.ProductPopupDto;
import archivegarden.shop.dto.product.ProductDetailsDto;
import archivegarden.shop.dto.product.ProductListDto;
import archivegarden.shop.dto.product.ProductSearchCondition;
import archivegarden.shop.entity.Product;
import archivegarden.shop.exception.ProductNotFoundException;
import archivegarden.shop.repository.product.ProductRepository;
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
     * 홈 화면
     * 최신 상품 9개 조회
     */
    public List<ProductListDto> getMainProducts() {
        return productRepository.findMainProducts().stream()
                .map(ProductListDto::new)
                .collect(Collectors.toList());
    }

    /**
     * 검색 + 페이지네이션
     */
    public Page<ProductListDto> searchProducts(String keyword, Pageable pageable) {
        return productRepository.search(keyword, pageable).map(ProductListDto::new);
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
     * @throws ProductNotFoundException
     */
    public ProductDetailsDto getProduct(Long productId) {
        //Product 조회
        Product product = productRepository.findProduct(productId);
        if(product == null) {
            throw new ProductNotFoundException("존재하지 않는 상품입니다.");
        }

        return new ProductDetailsDto(product);
    }

    /**
     * 팝업창
     * 상품 목록 조회 + 페이지네이션
     */
    public Page<ProductPopupDto> getPopupProducts(String keyword, Pageable pageable) {
        return productRepository.findDtoAllPopup(keyword, pageable);
    }
}