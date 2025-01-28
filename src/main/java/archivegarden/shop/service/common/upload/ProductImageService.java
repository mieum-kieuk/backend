package archivegarden.shop.service.common.upload;

import archivegarden.shop.dto.admin.product.product.ProductImageDto;
import archivegarden.shop.entity.ImageType;
import archivegarden.shop.entity.ProductImage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductImageService {

    private final FirebaseService firebaseService;

    /**
     * ImageType을 파리미터로 받아 상품 이미지 한 장 저장
     */
    public ProductImage createProductImage(MultipartFile multipartFile, ImageType imageType) {
        if(multipartFile.isEmpty()) {
            return null;
        }

        String uploadedImageUrl = firebaseService.uploadImage(multipartFile, imageType);

        return ProductImage.createProductImage(uploadedImageUrl, imageType);
    }

    /**
     * ImageType == DETAILS인 상품 이미지 여러장 저장
     *
     */
    public List<ProductImage> createProductImages(List<MultipartFile> detailsImages) {
        List<ProductImage> productImages = new ArrayList<>();
        for (MultipartFile multipartFile : detailsImages) {
            if(!multipartFile.isEmpty()) {
                productImages.add(createProductImage(multipartFile, ImageType.DETAILS));
            }
        }

        return productImages;
    }

    /**
     * 파이어베이스에서 다운로드하여 Base64로 인코딩된 데이터를 반환
     */
    public String getEncodedImageUrl(ProductImage productImage) {
        byte[] content= firebaseService.downloadImage(productImage.getImageUrl());
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(content);
    }

    /**
     * 상품 실제 이미지 데이터 파이어베이스에서 조회 후 DTO 리스트로 변환
     */
    public List<ProductImageDto> getProductImageDtos(List<ProductImage> productImages) {
        return productImages.stream()
                .map(image -> {
                    byte[] content = firebaseService.downloadImage(image.getImageUrl());
                    String encodedImage = "data:image/png;base64," + Base64.getEncoder().encodeToString(content);
                    return new ProductImageDto(image, encodedImage);
                })
                .collect(Collectors.toList());
    }

    /**
     * 상품 이미지 삭제
     */
    public void deleteProductImage(String imageUrl) {
        firebaseService.deleteImage(imageUrl);
    }
}
