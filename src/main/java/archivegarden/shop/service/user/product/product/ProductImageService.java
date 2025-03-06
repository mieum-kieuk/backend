package archivegarden.shop.service.user.product.product;

import archivegarden.shop.dto.user.product.ProductImageDto;
import archivegarden.shop.entity.ProductImage;
import archivegarden.shop.service.common.upload.FirebaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductImageService {

    private final FirebaseService firebaseService;

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
    public ProductImageDto convertToProductImageDto(ProductImage productImage) {
        String encodedImageData = getEncodedImageData(productImage.getImageUrl());
        return new ProductImageDto(productImage, encodedImageData);
    }

    public List<ProductImageDto> convertToProductImageDtos(List<ProductImage> productImages) {
        return productImages.stream()
                .map(this::convertToProductImageDto)
                .collect(Collectors.toList());
    }
}
