package archivegarden.shop.dto.admin.help.notice;

import archivegarden.shop.entity.Notice;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EditNoticeForm {

    private Long id;

    @NotBlank(message = "제목을 작성해 주세요.")
    private String title;

    @Size(max = 2000, message = "내용은 최대 2000자까지 입력할 수 있습니다.")
    @NotBlank(message = "내용을 작성해 주세요.")
    private String content;

    public EditNoticeForm(Notice notice) {
        this.id = notice.getId();
        this.title = notice.getTitle();
        this.content = notice.getContent();
    }
}
