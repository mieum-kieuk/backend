package archivegarden.shop.dto.community.qna;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class AddQnaForm {

    @NotBlank(message = "제목을 입력해 주세요.")
    private String title;

    @NotBlank(message = "내용을 입력해 주세요.")
    private String content;

    private boolean isSecret;
    private MultipartFile image1;
    private MultipartFile image2;
    private MultipartFile image3;
    private MultipartFile image4;
    private MultipartFile image5;
    private Long productId;
}
