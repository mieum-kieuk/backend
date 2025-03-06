package archivegarden.shop.service.user.product.product;

import archivegarden.shop.dto.order.OrderProductListDto;
import archivegarden.shop.dto.user.product.*;
import archivegarden.shop.entity.Discount;
import archivegarden.shop.entity.Member;
import archivegarden.shop.entity.Product;
import archivegarden.shop.entity.ProductImage;
import archivegarden.shop.exception.NotFoundException;
import archivegarden.shop.exception.common.EntityNotFoundException;
import archivegarden.shop.repository.order.CartRepository;
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
    private final CartRepository cartRepository;
    private final ProductImageService productImageService;
    private final Executor customAsyncExecutor;

    /**
     * 최신 상품 9개 조회
     */
    public List<ProductListDto> getLatestProducts() {
        List<Product> products = productRepository.findLatestProducts();
        return createProductListDtos(products);
    }

    /**
     * 상품 검색
     */
    public Page<ProductListDto> searchProducts(String keyword, Pageable pageable) {
        Page<Product> products = productRepository.searchProducts(keyword, pageable);
        List<ProductListDto> productListDtos = createProductListDtos(products.getContent());
        return new PageImpl<>(productListDtos, pageable, products.getTotalElements());
    }

    /**
     * 상품 목록 조회
     */
    public Page<ProductListDto> getProducts(ProductSearchCondition condition, Pageable pageable) {
        Page<Product> products = productRepository.findProductsByCategory(condition, pageable);
        List<ProductListDto> productListDtos = createProductListDtos(products.getContent());
        return new PageImpl<>(productListDtos, pageable, products.getTotalElements());
    }

    /**
     * 상품 단건 조회
     *
     * @throws EntityNotFoundException
     */
    public ProductDetailsDto getProduct(Long productId) {
        Product product = productRepository.findProduct(productId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 상품입니다."));
        List<ProductImageDto> productImageDtos = productImageService.convertToProductImageDtos(product.getProductImages());
        return new ProductDetailsDto(product, productImageDtos);
    }

    public void updateProductsWithNewDiscount(Discount discount) {
        List<Product> products = productRepository.findAll();
        for (Product product : products) {
            product.updateDiscount(discount);
        }
    }

    /**
     * 팝업창에서 상품 검색
     */
    public Page<PopupProductDto> getPopupProducts(String keyword, Pageable pageable) {
//        Page<PopupProductDto> productPages = productRepository.searchProductsInPopup(keyword, pageable);

//        List<PopupProductDto> popupProductDtos = productPages.getContent().stream()
//                .map(product ->
//                        {
//                            String donwloadedImageUrl = downloadProductImage(product.getDisplayImageUrl());
//                            product.setDisplayImageUrl(donwloadedImageUrl);
//                            return product;
//                        }
//                ).collect(Collectors.toList());

//        return new PageImpl<>(popupProductDtos, pageable, productPages.getTotalElements());
        return null;
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
     *  상품별 DTO를 병렬로 생성하는 공통 로직
     */
    private List<ProductListDto> createProductListDtos(List<Product> products) {
        // 상품별 DTO를 병렬로 생성
        List<CompletableFuture<ProductListDto>> futures = products.stream()
                .map(product -> CompletableFuture.supplyAsync(() -> createProductListDto(product), customAsyncExecutor))
                .collect(Collectors.toList());

        // 모든 DTO가 준비될 때까지 기다리기
        return futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    /**
     * ProductListDto를 생성하는 공통 로직
     */
    private ProductListDto createProductListDto(Product product) {
        List<String> imageDatas = getImageData(product.getProductImages());
        return new ProductListDto(product, imageDatas);
    }

    /**
     * 비동기적으로 이미지 데이터를 가져오는 공통 로직
     */
    private List<String> getImageData(List<ProductImage> productImages) {
        List<CompletableFuture<String>> imageDataFutures = productImages.stream()
                .map(productImage -> productImageService.getEncodedImageDataAsync(productImage.getImageUrl()))
                .collect(Collectors.toList());

        // 모든 이미지 데이터가 준비될 때까지 기다리기
        return imageDataFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }
}