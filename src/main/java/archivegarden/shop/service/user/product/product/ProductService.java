package archivegarden.shop.service.user.product.product;

import archivegarden.shop.dto.user.product.*;
import archivegarden.shop.entity.Product;
import archivegarden.shop.entity.ProductImage;
import archivegarden.shop.exception.common.EntityNotFoundException;
import archivegarden.shop.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductImageService productImageService;

    /**
     * 최신 상품 9개 조회
     */
    public List<ProductListDto> getLatestProducts() {
        List<Product> products = productRepository.findLatestProducts();

        List<CompletableFuture<ProductListDto>> futures = new ArrayList<>();
        for (Product product : products) {
            List<ProductImage> productImages = product.getProductImages();

            // 이미지 데이터를 비동기적으로 가져오기
            List<CompletableFuture<String>> imageDataFutures = new ArrayList<>();
            for (ProductImage productImage : productImages) {
                CompletableFuture<String> imageDataFuture = productImageService
                        .getEncodedImageDataAsync(productImage.getImageUrl());
                imageDataFutures.add(imageDataFuture);
            }

            // 이미지 데이터가 모두 준비되면 ProductListDto 생성
            CompletableFuture<ProductListDto> productListDtoFuture = CompletableFuture
                    .allOf(imageDataFutures.toArray(new CompletableFuture[0]))
                    .thenApplyAsync(v -> {
                        // 비동기적으로 이미지를 모두 가져왔을 때, 이미지 데이터를 리스트로 모은 후 ProductListDto 생성
                        List<String> imageDatas = imageDataFutures.stream()
                                .map(CompletableFuture::join)
                                .collect(Collectors.toList());
                        return new ProductListDto(product, imageDatas);
                    });

            futures.add(productListDtoFuture);
        }

        // 모든 ProductListDto가 준비될 때까지 기다리기
        return futures.stream()
                .map(CompletableFuture::join) // 각 future가 완료될 때까지 기다림
                .collect(Collectors.toList());
    }

    /**
     * 상품 검색
     */
    public Page<ProductListDto> searchProducts(String keyword, Pageable pageable) {
        Page<Product> products = productRepository.searchProducts(keyword, pageable);

        List<CompletableFuture<ProductListDto>> futures = new ArrayList<>();
        for (Product product : products) {
            List<ProductImage> productImages = product.getProductImages();

            // 이미지 데이터를 비동기적으로 가져오기
            List<CompletableFuture<String>> imageDataFutures = new ArrayList<>();
            for (ProductImage productImage : productImages) {
                CompletableFuture<String> imageDataFuture = productImageService
                        .getEncodedImageDataAsync(productImage.getImageUrl());
                imageDataFutures.add(imageDataFuture);
            }

            // 이미지 데이터가 모두 준비되면 ProductListDto 생성
            CompletableFuture<ProductListDto> productListDtoFuture = CompletableFuture
                    .allOf(imageDataFutures.toArray(new CompletableFuture[0]))
                    .thenApplyAsync(v -> {
                        // 비동기적으로 이미지를 모두 가져왔을 때, 이미지 데이터를 리스트로 모은 후 ProductListDto 생성
                        List<String> imageDatas = imageDataFutures.stream()
                                .map(CompletableFuture::join)
                                .collect(Collectors.toList());
                        return new ProductListDto(product, imageDatas);
                    });

            futures.add(productListDtoFuture);
        }

        // 모든 ProductListDto가 준비될 때까지 기다리기
        List<ProductListDto> productListDtos = futures.stream()
                .map(CompletableFuture::join) // 각 future가 완료될 때까지 기다림
                .collect(Collectors.toList());

        return new PageImpl<>(productListDtos, pageable, products.getTotalElements());
    }

    /**
     * 상품 목록 조회
     */
    public Page<ProductListDto> getProducts(ProductSearchCondition condition, Pageable pageable) {
        Page<Product> products = productRepository.findProductsByCategory(condition, pageable);

        List<CompletableFuture<ProductListDto>> futures = new ArrayList<>();
        for (Product product : products) {
            List<ProductImage> productImages = product.getProductImages();

            // 이미지 데이터를 비동기적으로 가져오기
            List<CompletableFuture<String>> imageDataFutures = new ArrayList<>();
            for (ProductImage productImage : productImages) {
                CompletableFuture<String> imageDataFuture = productImageService
                        .getEncodedImageDataAsync(productImage.getImageUrl());
                imageDataFutures.add(imageDataFuture);
            }

            // 이미지 데이터가 모두 준비되면 ProductListDto 생성
            CompletableFuture<ProductListDto> productListDtoFuture = CompletableFuture
                    .allOf(imageDataFutures.toArray(new CompletableFuture[0]))
                    .thenApplyAsync(v -> {
                        // 비동기적으로 이미지를 모두 가져왔을 때, 이미지 데이터를 리스트로 모은 후 ProductListDto 생성
                        List<String> imageDatas = imageDataFutures.stream()
                                .map(CompletableFuture::join)
                                .collect(Collectors.toList());
                        return new ProductListDto(product, imageDatas);
                    });

            futures.add(productListDtoFuture);
        }

        // 모든 ProductListDto가 준비될 때까지 기다리기
        List<ProductListDto> productListDtos = futures.stream()
                .map(CompletableFuture::join) // 각 future가 완료될 때까지 기다림
                .collect(Collectors.toList());

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

    /**
     * 팝업창에서 상품 검색
     */
    public Page<ProductSummaryDto> searchProductsInPopup(ProductPopupSearchCondition condition, Pageable pageable) {
        Page<ProductSummaryDto> productSummaryDtos = productRepository.searchProductsInInquiryPopup(condition, pageable);
        productSummaryDtos.forEach(p -> {
            String encodedImageData = productImageService.getEncodedImageData(p.getDisplayImageData());
            p.setDisplayImageData(encodedImageData);
        });

        return productSummaryDtos;
    }
}