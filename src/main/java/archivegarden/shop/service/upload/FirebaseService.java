package archivegarden.shop.service.upload;

import archivegarden.shop.entity.ImageType;
import archivegarden.shop.entity.ProductImage;
import com.google.cloud.storage.Bucket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductFileStore {

    private final Bucket bucket;

    public ProductImage storeFile(MultipartFile multipartFile, ImageType type) throws IOException {
        if(multipartFile.isEmpty()) {
            return null;
        }

        String blob = "/productImages" + "/display";
        if(bucket.get(blob) != null) {
            bucket.get(blob).delete();
        }

        bucket.create(blob, multipartFile.getBytes());
        return ProductImage.createProductImage(blob);
    }

    public List<ProductImage> storeFiles(List<MultipartFile> multipartFiles, ImageType type) throws IOException {
        List<ProductImage> storeProductImages = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            if(!multipartFile.isEmpty()) {
                storeProductImages.add(storeFile(multipartFile, type));
            }
        }

        return storeProductImages;
    }
}
