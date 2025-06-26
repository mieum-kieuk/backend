package archivegarden.shop.service.common.upload;

import archivegarden.shop.entity.ImageType;
import archivegarden.shop.exception.global.FileUploadException;
import com.google.cloud.storage.Bucket;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FirebaseService {

    @Value("${firebase.storage-bucket-name}")
    private String firebaseStorageUrl;

    private final Bucket bucket;


    /**
     * Firebase Storage에 파일을 업로드하고 저장된 경로를 반환
     *
     * @param multipartFile 업로드할 파일
     * @param imageType     이미지 유형
     * @return Firebase 내 저장 경로 (예: "products/details/uuid-파일명")
     * @throws FileUploadException Firebase에 파일 업로드 중 오류 발생하는 경우
     */
    public String uploadImage(MultipartFile multipartFile, ImageType imageType) {
        String originalFilename = encodeFilename(multipartFile.getOriginalFilename());
        String uniqueFileName = UUID.randomUUID() + "-" + originalFilename;
        String uploadedImageUrl = String.format("products/%s/%s", imageType.name().toLowerCase(), uniqueFileName);

        try {
            bucket.create(uploadedImageUrl, multipartFile.getBytes(), multipartFile.getContentType());
        } catch (IOException e) {
            throw new FileUploadException("파일 업로드 중 오류가 발생했습니다. 다시 시도해 주세요.");
        }

        return uploadedImageUrl;
    }

    /**
     * Firebase Storage에서 파일을 다운로드하여 바이트 배열로 반환
     *
     * @param imageUrl Firebase 내 파일 저장 경로
     * @return 다운로드된 파일 데이터(byte[])
     * @throws NullPointerException 해당 경로에 파일이 존재하지 않는 경우
     */
    public byte[] downloadImage(String imageUrl) {
        return bucket.get(imageUrl).getContent();
    }

    /**
     * Firebase Storage에서 파일을 삭제
     *
     * @param imageUrl Firebase 내 삭제할 파일 저장 경로
     * @throws NullPointerException 해당 경로에 파일이 존재하지 않는 경우
     */
    public void deleteImage(String imageUrl) {
        bucket.get(imageUrl).delete();
    }

    /**
     * 원본 파일명을 UTF-8로 인코딩
     *
     * @param originalFilename 인코딩할 원본 파일명
     * @return 인코딩된 파일명
     * @throws RuntimeException 인코딩 중 오류 발생하는 경우
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
