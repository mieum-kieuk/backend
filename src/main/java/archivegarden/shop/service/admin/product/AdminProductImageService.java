package archivegarden.shop.service.admin.product;

import archivegarden.shop.dto.admin.product.product.AdminProductImageDto;
import archivegarden.shop.entity.ImageType;
import archivegarden.shop.entity.ProductImage;
import archivegarden.shop.service.common.upload.FirebaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminProductImageService {

    private final FirebaseService firebaseService;
    private final Executor executor;

    /**
     * 단일 이미지 파일을 업로드하고 ProductImage 엔티티 생성
     *
     * @param multipartFile 업로드할 이미지 파일
     * @param imageType     이미지 유형 (DISPLAY, HOVER, DETAILS)
     * @return 생성된 ProductImage 객체, 파일이 비어 있으면 null 반환
     */
    public ProductImage createProductImage(MultipartFile multipartFile, ImageType imageType) {
        if (multipartFile.isEmpty()) {
            return null;
        }

        String uploadedImageUrl = firebaseService.uploadImage(multipartFile, imageType);

        return ProductImage.createProductImage(multipartFile.getOriginalFilename(), uploadedImageUrl, imageType);
    }

    /**
     * 이미지 다운로드하고 AdminProductImageDto 리스트로 변환
     *
     * @param productImages 변환할 ProductImage 리스트
     * @return 변환된 AdminProductImageDto 리스트
     */
    public List<AdminProductImageDto> toProductImageDtos(List<ProductImage> productImages) {
        List<CompletableFuture<AdminProductImageDto>> futures = productImages.stream()
                .map(image -> CompletableFuture.supplyAsync(() -> {
                    String base64 = downloadAndEncodeImage(image.getImageUrl());
                    return new AdminProductImageDto(image, base64);
                }, executor))
                .collect(Collectors.toList());

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        return futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    /**
     * 이미지 다운로드하고 AdminProductImageDto로 변환
     *
     * @param productImage 변환할 ProductImage
     * @return 변환된 AdminProductImageDto
     */
    public AdminProductImageDto toProductImageDto(ProductImage productImage) {
        String encodedImageData = downloadAndEncodeImage(productImage.getImageUrl());
        return new AdminProductImageDto(productImage, encodedImageData);
    }

    /**
     * 파이어베이스에서 이미지를 다운로드한 후 Base64로 인코딩된 문자열을 반환
     *
     * @param imageUrl 이미지 URL
     * @return Base64 인코딩된 이미지 데이터 (data URI 포함)
     */
    private String downloadAndEncodeImage(String imageUrl) {
        byte[] imageData = firebaseService.downloadImage(imageUrl);
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageData);
    }

    /**
     * 상품 이미지 단건 삭제
     *
     * @param productImage 삭제할 ProductImage 리스트
     */
    public CompletableFuture<Void> deleteProductImage(ProductImage productImage) {
        return CompletableFuture.runAsync(() ->
                firebaseService.deleteImage(productImage.getImageUrl()), executor);
    }

    /**
     * 상품 이미지 복수 삭제
     *
     * @param productImages 삭제할 ProductImage 리스트
     */
    public void deleteProductImages(List<ProductImage> productImages) {
        List<CompletableFuture<Void>> futures = productImages.stream()
                .map(this::deleteProductImage)
                .collect(Collectors.toList());

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }
}
