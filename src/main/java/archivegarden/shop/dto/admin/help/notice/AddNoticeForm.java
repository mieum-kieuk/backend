package archivegarden.shop.dto.admin.help.notice;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AddNoticeForm {

    @NotBlank(message = "제목을 작성해 주세요.")
    private String title;

    @Size(max = 2000, message = "내용은 최대 2000자까지 입력할 수 있습니다.")
    @NotBlank(message = "내용을 작성해 주세요.")
    private String content;
}
