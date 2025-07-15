package archivegarden.shop.service.user.product.product;

import archivegarden.shop.dto.user.product.*;
import archivegarden.shop.entity.Product;
import archivegarden.shop.exception.global.EntityNotFoundException;
import archivegarden.shop.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductImageService productImageService;
    private final Executor executor;

    /**
     * 최신 상품 9개 조회
     *
     * @return 상품 목록 DTO 리스트
     */
    public List<ProductListDto> getLatestProducts() {
        List<Product> products = productRepository.findLatestProducts();
        return convertToProductListDtoAsync(products);
    }

    /**
     * 키워드 기반 상품 검색
     *
     * @param keyword  검색어
     * @param pageable 페이징 정보
     * @return 검색된 상품 목록 Page 객체
     */
    public Page<ProductListDto> searchProducts(String keyword, Pageable pageable) {
        Page<Product> products = productRepository.searchProducts(keyword, pageable);
        List<ProductListDto> productListDtos = convertToProductListDtoAsync(products.getContent());
        return new PageImpl<>(productListDtos, pageable, products.getTotalElements());
    }

    /**
     * 상품 목록 조회
     *
     * @param cond 검색 조건
     * @param pageable  페이징 정보
     * @return 검색된 상품 목록 Page 객체
     */
    public Page<ProductListDto> getProducts(ProductSearchCondition cond, Pageable pageable) {
        Page<Product> products = productRepository.findProductsByCategory(cond, pageable);
        List<ProductListDto> productListDtos = convertToProductListDtoAsync(products.getContent());
        return new PageImpl<>(productListDtos, pageable, products.getTotalElements());
    }

    /**
     * 상품 상세 정보 조회
     *
     * @param productId 조회할 상품 ID
     * @return 상품 상세 정보 DTO
     * @throws EntityNotFoundException 해당 ID의 상품이 존재하지 않을 경우
     */
    public ProductDetailsDto getProduct(Long productId) {
        Product product = productRepository.findProduct(productId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 상품입니다."));
        List<ProductImageDto> productImageDtos = productImageService.toProductImageDtos(product.getProductImages());
        return new ProductDetailsDto(product, productImageDtos);
    }

    /**
     * 문의 팝업창 내 상품 검색
     *
     * @param cond 검색 조건
     * @param pageable  페이징 정보
     * @return 상품 요약 정보 Page 객체
     */
    public Page<ProductSummaryDto> searchProductsInPopup(ProductPopupSearchCondition cond, Pageable pageable) {
        Page<ProductSummaryDto> productSummaryDtos = productRepository.searchProductsInInquiryPopup(cond, pageable);

        List<CompletableFuture<Void>> futures = productSummaryDtos.getContent().stream()
                .map(product -> CompletableFuture.runAsync(() -> {
                    String encodedImageData = productImageService.downloadAndEncodeImage(product.getDisplayImage());
                    product.setDisplayImage(encodedImageData);
                }, executor))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        return productSummaryDtos;
    }

    /**
     * Product → ProductListDto 변환
     *
     * @param products 변환할 Product 리스트
     * @return 변환된 ProductListDto 리스트
     */
    private List<ProductListDto> convertToProductListDtoAsync(List<Product> products) {
        List<CompletableFuture<ProductListDto>> futures = products.stream()
                .map(product -> CompletableFuture.supplyAsync(() -> {
                    List<ProductImageDto> thumbnailImageDtos = productImageService.toProductImageDtos(product.getThumbnailImages());
                    return new ProductListDto(product, thumbnailImageDtos);
                }, executor))
                .collect(Collectors.toList());

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        return futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }
}