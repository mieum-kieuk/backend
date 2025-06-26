package archivegarden.shop.service.admin.product;

import archivegarden.shop.dto.admin.product.product.*;
import archivegarden.shop.entity.ImageType;
import archivegarden.shop.entity.Product;
import archivegarden.shop.entity.ProductImage;
import archivegarden.shop.exception.ajax.EntityNotFoundAjaxException;
import archivegarden.shop.exception.global.EntityNotFoundException;
import archivegarden.shop.repository.product.ProductImageRepository;
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
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminProductService {

    private final AdminProductImageService productImageService;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final Executor executor;

    /**
     * 상품 등록
     *
     * 상품 등록 시 상품 이미지들을 Firebase Storage에 업로드한 후
     * 해당 이미지 정보와 함께 상품 엔티티를 저장합니다.
     *
     * @param form 등록할 상품 정보를 담은 폼 DTO
     * @return 저장된 상품 ID
     */
    public Long addProduct(AdminAddProductForm form) {
        CompletableFuture<ProductImage> futureDisplayImage = CompletableFuture.supplyAsync(
                () -> productImageService.createProductImage(form.getDisplayImage(), ImageType.DISPLAY),
                executor);

        CompletableFuture<ProductImage> futureHoverImage = CompletableFuture.supplyAsync(
                () -> productImageService.createProductImage(form.getHoverImage(), ImageType.HOVER),
                executor);

        List<CompletableFuture<ProductImage>> futureDetailImages = form.getDetailImages().stream()
                .filter(detailImage -> !detailImage.isEmpty())
                .map(detailImage -> CompletableFuture.supplyAsync(
                        () -> productImageService.createProductImage(detailImage, ImageType.DETAILS), executor))
                .collect(Collectors.toList());

        List<CompletableFuture<?>> futureImages = new ArrayList<>();
        futureImages.add(futureDisplayImage);
        futureImages.add(futureHoverImage);
        futureImages.addAll(futureDetailImages);
        CompletableFuture.allOf(futureImages.toArray(new CompletableFuture[0])).join();

        ProductImage displayImage = futureDisplayImage.join();
        ProductImage hoverImage = futureHoverImage.join();
        List<ProductImage> detailImages = futureDetailImages.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

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
     * 상품 상세 조회
     *
     * @param productId 조회할 상품 ID
     * @return 상품 상세 정보 DTO
     * @throws EntityNotFoundException 해당 ID의 상품이 존재하지 않을 경우
     */
    @Transactional(readOnly = true)
    public AdminProductDetailsDto getProduct(Long productId) {
        Product product = productRepository.findProductInAdmin(productId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 상품입니다."));
        List<AdminProductImageDto> productImageDtos = productImageService.toProductImageDtos(product.getProductImages());
        return new AdminProductDetailsDto(product, productImageDtos);
    }

    /**
     * 상품 목록 조회
     *
     * @param cond     상품 검색 조건
     * @param pageable 페이징 정보
     * @return 검색된 상품 목록을 담은 Page 객체
     */
    @Transactional(readOnly = true)
    public Page<AdminProductListDto> getProducts(AdminProductSearchCondition cond, Pageable pageable) {
        Page<Product> products = productRepository.searchProductsInAdmin(cond, pageable);

        List<CompletableFuture<AdminProductListDto>> futures = products.getContent().stream()
                .map(product -> CompletableFuture.supplyAsync(() -> {
                    ProductImage thumbnail = product.getThumbnailImage();
                    AdminProductImageDto imageDto = productImageService.toProductImageDto(thumbnail);
                    return new AdminProductListDto(product, imageDto);
                }, executor))
                .collect(Collectors.toList());

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        List<AdminProductListDto> productList = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        return new PageImpl<>(productList, pageable, products.getTotalElements());
    }

    /**
     * 상품 수정 폼 조회
     *
     * @param productId 수정할 상품 ID
     * @return 상품 수정 폼 DTO
     * @throws EntityNotFoundException 해당 ID의 상품이 존재하지 않을 경우
     */
    @Transactional(readOnly = true)
    public AdminEditProductForm getEditProductForm(Long productId) {
        Product product = productRepository.findProductInAdmin(productId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 상품입니다."));
        List<AdminProductImageDto> productImageDtos = productImageService.toProductImageDtos(product.getProductImages());
        return new AdminEditProductForm(product, productImageDtos);
    }

    /**
     * 상품 수정
     *
     * 이미지의 경우 타입별로 다음과 같은 정책을 따릅니다:
     * - DISPLAY: 새 이미지가 업로드되면 기존 이미지를 삭제하고 교체
     * - HOVER: 삭제 플래그가 true면 제거, 새 이미지가 있으면 추가
     * - DETAILS: 삭제 대상 ID 목록에 포함되지 않은 이미지는 제거, 새 이미지가 있으면 추가
     *
     * @param productId 수정할 상품 ID
     * @param form      수정할 상품 정보가 담긴 폼 DTO
     * @throws EntityNotFoundException 상품 또는 이미지가 존재하지 않을 경우
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

            productImageService.deleteProductImage(originalDisplayImage);
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

            productImageService.deleteProductImage(originalHoverImage);
            product.removeImage(originalHoverImage);
        }

        if (!form.getHoverImage().isEmpty()) {
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
        for (int i = 0; i < detailsImageIds.size(); i++) {
            String idx = "FILE_" + detailsImageIds.get(i);
            if (!deleteDetailsImages.contains(idx)) {
                ProductImage productImage = productImageRepository.findById(detailsImageIds.get(i)).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 상품 이미지 입니다."));
                productImageService.deleteProductImage(productImage);
                product.removeImage(productImage);
            }
        }

        if (!form.getDetailImages().isEmpty()) {
            List<CompletableFuture<ProductImage>> futures = form.getDetailImages().stream()
                    .filter(detailImage -> !detailImage.isEmpty())
                    .map(image -> CompletableFuture.supplyAsync(
                            () -> productImageService.createProductImage(image, ImageType.DETAILS), executor))
                    .collect(Collectors.toList());

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            futures.stream()
                    .map(CompletableFuture::join)
                    .forEach(product::addProductImage);
        }
    }

    /**
     * 상품 단건 삭제
     *
     * @param productId 삭제할 상품 ID
     * @throws EntityNotFoundAjaxException 해당 ID의 상품이 존재하지 않을 경우
     */
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new EntityNotFoundAjaxException("존재하지 않는 상품입니다."));
        productImageService.deleteProductImages(product.getProductImages());
        productRepository.delete(product);
    }

    /**
     * 상품 여러건 삭제
     *
     * @param productIds 삭제할 상품 ID 리스트
     * @throws EntityNotFoundAjaxException 해당 ID의 상품이 존재하지 않을 경우
     */
    public void deleteProducts(List<Long> productIds) {
        productIds.stream().forEach(productId -> {
            Product product = productRepository.findById(productId).orElseThrow(() -> new EntityNotFoundAjaxException("존재하지 않는 상품입니다."));
            productImageService.deleteProductImages(product.getProductImages());
            productRepository.delete(product);
        });
    }

    /**
     * 상품명 사용 가능 여부 확인
     *
     * @param name 중복 검사할 상품명
     * @return 사용할 수 있으면 true, 이미 사용 중이면 false
     */
    @Transactional(readOnly = true)
    public boolean isAvailableName(String name) {
        return productRepository.findByName(name).isEmpty();
    }

    /**
     * 팝업창에서 상품 검색
     */
    public Page<AdminProductSummaryDto> searchProductsInPopup(AdminProductPopupSearchCondition cond, Pageable pageable) {
        Page<AdminProductSummaryDto> productPopupDtos = productRepository.searchProductsInDiscountPopup(cond, pageable);

//        List<CompletableFuture<Void>> futures = productPopupDtos.stream()
//                .map(p -> CompletableFuture.runAsync(() -> {
//                    String encodedImageData = productImageService.getEncodedImageDataAsync(p.getDisplayImageData()).join();
//                    p.setDisplayImageData(encodedImageData);
//                }, executor))
//                .collect(Collectors.toList());
//
//        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        return productPopupDtos;
    }
}
