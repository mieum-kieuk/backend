package archivegarden.shop.dto.admin.help.notice;

import archivegarden.shop.entity.Notice;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class NoticeDetailsDto {

    private Long id;
    private String title;
    private String content;
    private String createdAt;

    public NoticeDetailsDto(Notice notice) {
        this.id = notice.getId();
        this.title = notice.getTitle();
        this.content = notice.getContent();
        this.createdAt = DateTimeFormatter.ofPattern("yyyy.MM.dd").format(notice.getCreatedAt());
    }
}
