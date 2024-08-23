package archivegarden.shop.service.upload;

import archivegarden.shop.entity.ImageType;
import archivegarden.shop.entity.ProductImage;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductImageService {

    private final Bucket bucket;

    @Value("${firebase.storage-url}")
    private String firebaseStorageUrl;

    public ProductImage uploadProductImage(MultipartFile multipartFile, ImageType imageType) throws IOException {
        if(multipartFile.isEmpty()) {
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String uniqueFileName = UUID.randomUUID() + "-" + originalFilename;

        String blob = String.format("products/%s/%s", imageType.name().toLowerCase(), uniqueFileName);
        if(bucket.get(blob) != null) {
            bucket.get(blob).delete();
        }

        bucket.create(blob, multipartFile.getBytes());
        return ProductImage.createProductImage(blob, imageType);
    }

    public List<ProductImage> uploadProductImages(List<MultipartFile> multipartFiles, ImageType type) throws IOException {
        List<ProductImage> storeProductImages = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            if(!multipartFile.isEmpty()) {
                storeProductImages.add(uploadProductImage(multipartFile, type));
            }
        }

        return storeProductImages;
    }

    public String downloadImage(String imageUrl) {
        byte[] content = bucket.get(imageUrl).getContent();
        String base64Image = Base64.getEncoder().encodeToString(content);
        return "data:image/png;base64," + base64Image;
    }

    public void deleteImage(String imageUrl) {
        bucket.get(imageUrl).delete();
    }

    public void deleteImages(List<ProductImage> productImages) {
        for (ProductImage productImage : productImages) {
            deleteImage(productImage.getImageUrl());
        }
    }
}
