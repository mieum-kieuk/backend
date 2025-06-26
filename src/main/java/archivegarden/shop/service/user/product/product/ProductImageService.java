package archivegarden.shop.service.user.product.product;

import archivegarden.shop.dto.user.product.ProductImageDto;
import archivegarden.shop.entity.ProductImage;
import archivegarden.shop.service.common.upload.FirebaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductImageService {

    private final FirebaseService firebaseService;

    /**
     * 이미지 다운로드하고 ProductImageDto 리스트로 변환
     *
     * @param productImages 변환할 ProductImage 리스트
     * @return 변환된 ProductImageDto 리스트
     */
    public List<ProductImageDto> toProductImageDtos(List<ProductImage> productImages) {
        return productImages.stream()
                .map(this::toProductImageDto)
                .collect(Collectors.toList());
    }

    /**
     * 이미지 다운로드하고 ProductImageDto로 변환
     *
     * @param productImage 변환할 ProductImage
     * @return 변환된 ProductImageDto
     */
    public ProductImageDto toProductImageDto(ProductImage productImage) {
        String encodedImageData = downloadAndEncodeImage(productImage.getImageUrl());
        return new ProductImageDto(productImage, encodedImageData);
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
}
