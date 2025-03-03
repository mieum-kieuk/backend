package archivegarden.shop.service.user.product.product;

import archivegarden.shop.service.common.upload.FirebaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductImageService {

    private final FirebaseService firebaseService;

    @Async("customAsyncExecutor")
    public CompletableFuture<String> getEncodedImageDataAsync(String imageUrl) {
        byte[] imageData = firebaseService.downloadImage(imageUrl);
        String encodedImageData = "data:image/png;base64," + Base64.getEncoder().encodeToString(imageData);
        return CompletableFuture.completedFuture(encodedImageData);
    }
}
