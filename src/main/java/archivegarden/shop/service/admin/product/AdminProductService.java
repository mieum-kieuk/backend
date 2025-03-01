package archivegarden.shop.service.admin.product;

import archivegarden.shop.dto.admin.product.product.*;
import archivegarden.shop.entity.ImageType;
import archivegarden.shop.entity.Product;
import archivegarden.shop.entity.ProductImage;
import archivegarden.shop.exception.ajax.AjaxEntityNotFoundException;
import archivegarden.shop.exception.common.EntityNotFoundException;
import archivegarden.shop.repository.product.ProductImageRepository;
import archivegarden.shop.repository.product.ProductRepository;
import archivegarden.shop.service.admin.upload.AdminProductImageService;
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
@Transactional
@RequiredArgsConstructor
public class AdminProductService {

    private final AdminProductImageService productImageService;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final Executor customAsyncExecutor;

    /**
     * 상품 저장
     */
    public Long saveProduct(AdminAddProductForm form) {
        ProductImage displayImage = productImageService.createProductImage(form.getDisplayImage(), ImageType.DISPLAY);
        ProductImage hoverImage = productImageService.createProductImage(form.getHoverImage(), ImageType.HOVER);
        List<ProductImage> detailImages = productImageService.createDetailProductImages(form.getDetailImages());

        Product product = Product.builder()
                .form(form)
                .displayImage(displayImage)
                .hoverImage(hoverImage)
                .detailImages(detailImages)
                .build();

        productRepository.save(product);

        return product.getId();
    }

    /**
     * 상품 단건 조회
     *
     * @throws EntityNotFoundException
     */
    @Transactional(readOnly = true)
    public AdminProductDetailsDto getProduct(Long productId) {
        Product product = productRepository.findProductInAdmin(productId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 상품입니다."));
        List<AdminProductImageDto> productImageDtos = productImageService.convertToProductImageDtos(product.getProductImages());
        return new AdminProductDetailsDto(product, productImageDtos);
    }

    /**
     * 상품 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<AdminProductListDto> getProducts(AdminProductSearchCondition condition, Pageable pageable) {
        Page<Product> products = productRepository.findAllProductInAdmin(condition, pageable);

        List<CompletableFuture<AdminProductListDto>> futures = products.getContent().stream()
                .map(product -> CompletableFuture.supplyAsync(() -> {
                    // 각 상품에 대해 이미지 변환 작업을 비동기적으로 수행
                    List<AdminProductImageDto> productImageDtos = productImageService.convertToProductImageDtos(product.getProductImages());
                    return new AdminProductListDto(product, productImageDtos);
                }, customAsyncExecutor))
                .collect(Collectors.toList());

        List<AdminProductListDto> productList = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        return new PageImpl<>(productList, pageable, products.getTotalElements());
    }

    /**
     * 상품 수정 폼 조회
     *
     * @throws EntityNotFoundException
     */
    @Transactional(readOnly = true)
    public AdminEditProductForm getEditProductForm(Long productId) {
        Product product = productRepository.findProduct(productId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 상품입니다."));
        List<AdminProductImageDto> productImageDtos = productImageService.convertToProductImageDtos(product.getProductImages());
        return new AdminEditProductForm(product, productImageDtos);
    }

    /**
     * 상품 수정
     *
     * @throws EntityNotFoundException
     */
    public void updateProduct(Long productId, AdminEditProductForm form) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 상품입니다."));
        product.update(form);

        //ImageType == DISPLAY ProductImage 수정
        if (!form.getDisplayImage().isEmpty()) {
            ProductImage originalDisplayImage = product.getProductImages()
                    .stream()
                    .filter(productImage -> productImage.getImageType() == ImageType.DISPLAY)
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 상품 이미지 입니다."));

            productImageService.deleteProductImage(originalDisplayImage.getImageUrl());
            product.removeImage(originalDisplayImage);

            ProductImage newDisplayImage = productImageService.createProductImage(form.getDisplayImage(), ImageType.DISPLAY);
            product.addProductImage(newDisplayImage);
        }

        //ImageType == HOVER ProductImage 수정
        if (form.isHoverImageDeleted()) {
            ProductImage originalHoverImage = product.getProductImages()
                    .stream()
                    .filter(productImage -> productImage.getImageType() == ImageType.HOVER)
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 상품 이미지 입니다."));

            productImageService.deleteProductImage(originalHoverImage.getImageUrl());
            product.removeImage(originalHoverImage);
        }

        if(!form.getHoverImage().isEmpty()) {
            ProductImage newHoverImage = productImageService.createProductImage(form.getHoverImage(), ImageType.HOVER);
            product.addProductImage(newHoverImage);
        }

        //ImageType == DETAILS ProductImage 수정
        List<Long> detailsImageIds = product.getProductImages()
                .stream()
                .filter(productImage -> productImage.getImageType() == ImageType.DETAILS)
                .map(productImage -> productImage.getId())
                .collect(Collectors.toList());

        List<String> deleteDetailsImages = form.getDeleteDetailImages();
        for(int i = 0; i < detailsImageIds.size(); i++) {
            String idx = "FILE_" + detailsImageIds.get(i);
            if(!deleteDetailsImages.contains(idx)) {
                ProductImage productImage = productImageRepository.findById(detailsImageIds.get(i)).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 상품 이미지 입니다."));
                productImageService.deleteProductImage(productImage.getImageUrl());
                product.removeImage(productImage);
            }
        }

        if (!form.getDetailImages().isEmpty()) {
            List<ProductImage> productImages = productImageService.createDetailProductImages(form.getDetailImages());
            productImages.forEach(m -> product.addProductImage(m));
        }
    }

    /**
     * 상품 단건 삭제
     *
     * @throws AjaxEntityNotFoundException
     */
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new AjaxEntityNotFoundException("존재하지 않는 상품입니다."));
        List<ProductImage> productImages = product.getProductImages();
        productImages.forEach((image) -> productImageService.deleteProductImage(image.getImageUrl()));
        productRepository.delete(product);
    }

    /**
     * 상품 여러건 삭제
     *
     * @throws AjaxEntityNotFoundException
     */
    public void deleteProducts(List<Long> productIds) {
        productIds.stream().forEach(productId -> {
            Product product = productRepository.findById(productId).orElseThrow(() -> new AjaxEntityNotFoundException("존재하지 않는 상품입니다."));
            List<ProductImage> productImages = product.getProductImages();
            productImages.forEach((image) -> productImageService.deleteProductImage(image.getImageUrl()));
            productRepository.delete(product);
        });
    }

    /**
     * 상품명 중복 검사
     */
    public boolean isAvailableName(String name) {
        return productRepository.findByName(name).isEmpty();
    }

    /**
     * 팝업창에서 상품 검색
     */
    public Page<AdminProductSummaryDto> searchProductsInPopup(AdminProductPopupSearchCondition condition, Pageable pageable) {
        Page<AdminProductSummaryDto> productPopupDtos = productRepository.searchProductsInDiscountPopup(condition, pageable);
        List<CompletableFuture<Void>> futures = productPopupDtos.stream()
                .map(p -> CompletableFuture.runAsync(() -> {
                    String encodedImageData = productImageService.getEncodedImageDataAsync(p.getDisplayImageData()).join();
                    p.setDisplayImageData(encodedImageData);
                }, customAsyncExecutor))
                .collect(Collectors.toList());

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        return productPopupDtos;
    }
}
