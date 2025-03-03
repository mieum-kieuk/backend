package archivegarden.shop.service.common.upload;

import archivegarden.shop.entity.ImageType;
import archivegarden.shop.exception.common.FileUploadException;
import com.google.cloud.storage.Bucket;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class FirebaseService {

    @Value("${firebase.storage-bucket-name}")
    private String firebaseStorageUrl;

    private final Bucket bucket;

    /**
     * 파이어베이스에 파일 저장
     */
    public String uploadImage(MultipartFile multipartFile, ImageType imageType) {

        String originalFilename = encodeFilename(multipartFile.getOriginalFilename());
        String uniqueFileName = UUID.randomUUID() + "-" + originalFilename;
        String uploadedImageUrl = String.format("products/%s/%s", imageType.name().toLowerCase(), uniqueFileName);

        try {
         bucket.create(uploadedImageUrl, multipartFile.getBytes(), multipartFile.getContentType());
        } catch (IOException e) {
            throw new FileUploadException("이미지 업로드 중 오류가 발생했습니다.");
        }

        return uploadedImageUrl;
    }

    /**
     * 파이어베이스에서 파일 다운로드
     */
    public byte[] downloadImage(String imageUrl) {
        return bucket.get(imageUrl).getContent();
    }


    /**
     * 파이어베이스에서 파일 삭제
     */
    public void deleteImage(String imageUrl) {
        bucket.get(imageUrl).delete();
    }

    /**
     * 파일명 인코딩
     */
    private String encodeFilename(String originalFilename) {
        String encodedFilename = "";
        try {
            encodedFilename = URLEncoder.encode(originalFilename, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("파일 이름 인코딩 중 오류가 발생했습니다.");
        }
        return encodedFilename;
    }
}
