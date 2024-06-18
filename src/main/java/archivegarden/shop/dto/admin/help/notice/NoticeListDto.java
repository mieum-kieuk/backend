package archivegarden.shop.dto.admin.help.notice;

import archivegarden.shop.entity.Notice;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class NoticeListDto {

    private Long id;
    private String title;
    private String createdAt;

    public NoticeListDto(Notice notice) {
        this.id = notice.getId();
        this.title = notice.getTitle();
        this.createdAt = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss").format(notice.getCreatedAt());
    }
}
