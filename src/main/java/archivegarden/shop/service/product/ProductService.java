package archivegarden.shop.service.product;

import archivegarden.shop.dto.admin.product.product.ProductImageDto;
import archivegarden.shop.dto.order.OrderProductListDto;
import archivegarden.shop.dto.user.product.PopupProductDto;
import archivegarden.shop.dto.user.product.ProductDetailsDto;
import archivegarden.shop.dto.user.product.ProductListDto;
import archivegarden.shop.dto.user.product.ProductSearchCondition;
import archivegarden.shop.entity.Member;
import archivegarden.shop.entity.Product;
import archivegarden.shop.entity.ProductImage;
import archivegarden.shop.exception.NotFoundException;
import archivegarden.shop.exception.common.EntityNotFoundException;
import archivegarden.shop.repository.order.CartRepository;
import archivegarden.shop.repository.product.ProductRepository;
import archivegarden.shop.service.common.upload.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        Page<Product> productPages = productRepository.findProductsByCategory(condition, pageable);
        List<ProductListDto> productListDtos = getProductListDto(productPages.getContent());
        return new PageImpl<>(productListDtos, pageable, productPages.getTotalElements());
    }

    /**
     * 상품 단건 조회
     *
     * @throws EntityNotFoundException
     */
    public ProductDetailsDto getProduct(Long productId) {
        Product product = productRepository.findProduct(productId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 상품입니다."));

        List<String> imageUrls = product.getProductImages().stream()
                .map(ProductImage::getImageUrl)
                .collect(Collectors.toList());

        List<String> downloadedImageUrls = downloadProductImagesAsync(imageUrls);

        List<ProductImageDto> productImageDtos = createProductImageDtos(product, downloadedImageUrls);

        return new ProductDetailsDto(product, productImageDtos);
    }


    /**
     * 팝업창에서 상품 검색
     */
    public Page<PopupProductDto> getPopupProducts(String keyword, Pageable pageable) {
        Page<PopupProductDto> productPages = productRepository.searchProductsInPopup(keyword, pageable);

        List<PopupProductDto> popupProductDtos = productPages.getContent().stream()
                .map(product ->
                        {
                            String donwloadedImageUrl = downloadProductImage(product.getDisplayImageUrl());
                            product.setDisplayImageUrl(donwloadedImageUrl);
                            return product;
                        }
                ).collect(Collectors.toList());

        return new PageImpl<>(popupProductDtos, pageable, productPages.getTotalElements());
    }

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

    /**
     * 상품들 비동기 처리
     */
    private List<ProductListDto> getProductListDto(List<Product> products) {
        List<CompletableFuture<ProductListDto>> productListDtoFutures = products.stream()
                .map(product -> processProductAsync(product))
                .collect(Collectors.toList());

        List<ProductListDto> productListDtos = productListDtoFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        return productListDtos;
    }

    /**
     * 상품 비동기 처리
     */
    private CompletableFuture<ProductListDto> processProductAsync(Product product) {
        return CompletableFuture.supplyAsync(() -> {
            List<String> imageUrls = product.getProductImages().stream()
                    .map(ProductImage::getImageUrl)
                    .collect(Collectors.toList());

            //각 상품의 이미지 비동기 다운로드
            List<String> downloadImageUrls = downloadProductImagesAsync(imageUrls);

            return new ProductListDto(product, downloadImageUrls);
        });
    }

    /**
     * 주어진 상품의 상품 이미지들을 비동기 다운로드
     */
    private List<String> downloadProductImagesAsync(List<String> imageUrls) {
//        List<CompletableFuture<String>> imageUrlFutures = imageUrls.stream()
//                .map(imageUrl -> CompletableFuture.supplyAsync(() -> productImageService.downloadImage(imageUrl)))
//                .collect(Collectors.toList());
        List<CompletableFuture<String>> imageUrlFutures = null;
        return imageUrlFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    /**
     * ProductImageDto 객체 생성
     */
    private List<ProductImageDto> createProductImageDtos(Product product, List<String> downloadedImageUrls) {
        return IntStream.range(0, product.getProductImages().size())
                .mapToObj(index -> {
                    ProductImage productImage = product.getProductImages().get(index);
                    String imageUrl = downloadedImageUrls.get(index);  // 비동기 다운로드 후 해당 인덱스의 URL
                    return new ProductImageDto(productImage, imageUrl);
                })
                .collect(Collectors.toList());
    }

    /**
     * 상품 이미지 다운로드
     */
    private String downloadProductImage(String imageUrl) {
        return null;
//        return productImageService.downloadImage(imageUrl);
    }
}