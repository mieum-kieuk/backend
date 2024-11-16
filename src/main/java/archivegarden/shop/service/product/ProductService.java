package archivegarden.shop.service.product;

import archivegarden.shop.dto.admin.product.product.ProductImageDto;
import archivegarden.shop.dto.order.OrderProductListDto;
import archivegarden.shop.dto.user.product.ProductDetailsDto;
import archivegarden.shop.dto.user.product.ProductListDto;
import archivegarden.shop.dto.user.product.ProductSearchCondition;
import archivegarden.shop.entity.Member;
import archivegarden.shop.entity.Product;
import archivegarden.shop.exception.NotFoundException;
import archivegarden.shop.exception.ProductNotFoundException;
import archivegarden.shop.repository.order.CartRepository;
import archivegarden.shop.repository.product.ProductRepository;
import archivegarden.shop.service.upload.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {

    private final ProductImageService productImageService;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;

    /**
     * 최신 상품 9개 조회
     */
    public List<ProductListDto> getLatestProducts() {
        List<Product> products = productRepository.findLatestProducts();
        List<ProductListDto> productListDto = getProductListDto(products);
        return productListDto;
    }

    /**
     * 상품 검색
     */
    public Page<ProductListDto> searchProducts(String keyword, Pageable pageable) {
        Page<Product> productPages = productRepository.searchProducts(keyword, pageable);
        List<ProductListDto> productListDtos = getProductListDto(productPages.getContent());
        return new PageImpl<>(productListDtos, pageable, productPages.getTotalElements());
    }

    /**
     * 상품 목록 조회
     */
    public Page<ProductListDto> getProducts(ProductSearchCondition condition, Pageable pageable) {
        Page<Product> productPages = productRepository.findAllByCategory(condition, pageable);
        List<ProductListDto> productListDtos = getProductListDto(productPages.getContent());
        return new PageImpl<>(productListDtos, pageable, productPages.getTotalElements());
    }

    /**
     * 상품 단건 조회
     *
     * @throws ProductNotFoundException
     */
    public ProductDetailsDto getProduct(Long productId) {
        Product product = productRepository.findProduct(productId);
        if (product == null) {
            throw new ProductNotFoundException("존재하지 않는 상품입니다.");
        }

        List<ProductImageDto> productImageDtos = downloadProductImages(product);
        return new ProductDetailsDto(product, productImageDtos);
    }

    /**
     * 팝업창
     * 상품 목록 조회 + 페이지네이션
     */
//    public Page<ProductPopupResultDto> getPopupProducts(String keyword, Pageable pageable) {
//        return productRepository.findDtoAllPopup(keyword, pageable);
//    }

    /**
     * 주문하려는 상품 목록 조회
     *
     * @throws NotFoundException
     */
    public List<OrderProductListDto> getOrderProducts(Member loginMember, List<Long> productIds) {
        //Product 조회
        productIds.forEach(id -> productRepository.findById(id).orElseThrow(() -> new NotFoundException("존재하지 않는 상품입니다.")));

        return cartRepository.findOrderProducts(loginMember, productIds)
                .stream()
                .map(OrderProductListDto::new)
                .collect(Collectors.toList());
    }

    private List<ProductListDto> getProductListDto(List<Product> products) {
        List<CompletableFuture<ProductListDto>> productListDtoFutures = products.stream()
                .map(product -> CompletableFuture.supplyAsync(() -> {
                    //각 Product의 이미지 다운로드 작업
                    List<CompletableFuture<String>> imageFutures = product.getProductImages().stream()
                            .map(productImage -> CompletableFuture.supplyAsync(() ->
                                    productImageService.downloadImage(productImage.getImageUrl())))
                            .collect(Collectors.toList());

                    //다운로드된 이미지 URL 리스트 생성
                    List<String> displayImageUrls = imageFutures.stream()
                            .map(CompletableFuture::join)
                            .collect(Collectors.toList());

                    //ProductListDto 생성
                    return new ProductListDto(product, displayImageUrls);
                }))
                .collect(Collectors.toList());

        //모든 비동기 작업 완료 후 ProductListDto 리스트 생성
        List<ProductListDto> productListDtos = productListDtoFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        return productListDtos;
    }

    private List<ProductImageDto> downloadProductImages(Product product) {
        return product.getProductImages().stream()
                .map(image -> {
                    String encodedImage = productImageService.downloadImage(image.getImageUrl());
                    return new ProductImageDto(image, encodedImage);
                })
                .collect(Collectors.toList());
    }
}