package archivegarden.shop.service.admin.upload;

import archivegarden.shop.dto.admin.product.product.AdminProductImageDto;
import archivegarden.shop.entity.ImageType;
import archivegarden.shop.entity.ProductImage;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminProductImageService {

    private final AdminFirebaseService firebaseService;

    /**
     * ImageType을 파리미터로 받아 ProductImage 1개 생성
     */
    public ProductImage createProductImage(MultipartFile multipartFile, ImageType imageType) {
        if(multipartFile.isEmpty()) {
            return null;
        }

        //파이어베이스에 실제 이미지 저장
        String uploadedImageUrl = firebaseService.uploadImage(multipartFile, imageType);

        return ProductImage.createProductImage(multipartFile.getOriginalFilename(), uploadedImageUrl, imageType);
    }

    /**
     * ImageType == DETAILS인 ProductImage 여러개 생성
     *
     */
    public List<ProductImage> createDetailProductImages(List<MultipartFile> detailImages) {
        List<ProductImage> productImages = new ArrayList<>();
        for (MultipartFile multipartFile : detailImages) {
            if(!multipartFile.isEmpty()) {
                productImages.add(createProductImage(multipartFile, ImageType.DETAILS));
            }
        }

        return productImages;
    }

    /**
     * 파이어베이스에서 다운로드하여 Base64로 인코딩된 데이터를 반환
     */
    public String getEncodedImageData(String imageUrl) {
        byte[] imageData = firebaseService.downloadImage(imageUrl);
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageData);
    }

    @Async("customAsyncExecutor")
    public CompletableFuture<String> getEncodedImageDataAsync(String imageUrl) {
        byte[] imageData = firebaseService.downloadImage(imageUrl);
        String encodedImageData = "data:image/png;base64," + Base64.getEncoder().encodeToString(imageData);
        return CompletableFuture.completedFuture(encodedImageData);
    }

    /**
     * ProductImage -> ProductImageDto로 변환
     */
    public AdminProductImageDto convertToProductImageDto(ProductImage productImage) {
        String encodedImageData = getEncodedImageData(productImage.getImageUrl());
        return new AdminProductImageDto(productImage, encodedImageData);
    }

    public List<AdminProductImageDto> convertToProductImageDtos(List<ProductImage> productImages) {
        return productImages.stream()
                .map(this::convertToProductImageDto)
                .collect(Collectors.toList());
    }

    /**
     * 상품 이미지 삭제
     */
    public void deleteProductImage(String imageUrl) {
        firebaseService.deleteImage(imageUrl);
    }
}
