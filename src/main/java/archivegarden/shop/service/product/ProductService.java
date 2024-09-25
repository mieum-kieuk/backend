package archivegarden.shop.service.product;

import archivegarden.shop.dto.admin.product.product.ProductImageDto;
import archivegarden.shop.dto.order.OrderProductListDto;
import archivegarden.shop.dto.user.product.ProductSearchCondition;
import archivegarden.shop.dto.user.product.ProductDetailsDto;
import archivegarden.shop.dto.user.product.ProductListDto;
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
import java.util.stream.Stream;

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
    public List<ProductListDto> getMainProducts() {
        List<Product> products = productRepository.findMainProducts();
        List<ProductListDto> productListDto = getProductListDto(products.stream());
        return productListDto;
    }

    /**
     * 상품 검색
     */
    public Page<ProductListDto> searchProducts(String keyword, Pageable pageable) {
        Page<Product> productPages = productRepository.searchProducts(keyword, pageable);
        List<ProductListDto> productListDtos = getProductListDto(productPages.stream());
        return new PageImpl<>(productListDtos, pageable, productPages.getTotalElements());
    }

    /**
     * 상품 목록 조회
     */
    public Page<ProductListDto> getProducts(ProductSearchCondition condition, Pageable pageable) {
        Page<Product> productPages = productRepository.findAllByCategory(condition, pageable);
        List<ProductListDto> productListDtos = getProductListDto(productPages.stream());
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

    private List<ProductListDto> getProductListDto(Stream<Product> products) {
        List<CompletableFuture<ProductListDto>> productListDtoFutures = products
                .map(product -> CompletableFuture.supplyAsync(() -> {
                    List<CompletableFuture<String>> imageFutures = product.getProductImages().stream()
                            .map(productImage -> CompletableFuture.supplyAsync(() ->
                                    productImageService.downloadImage(productImage.getImageUrl())))
                            .collect(Collectors.toList());

                    List<String> displayImageUrls = imageFutures.stream()
                            .map(CompletableFuture::join)
                            .collect(Collectors.toList());

                    return new ProductListDto(product, displayImageUrls);
                }))
                .collect(Collectors.toList());

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