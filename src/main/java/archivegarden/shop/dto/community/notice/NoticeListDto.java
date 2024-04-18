package archivegarden.shop.dto.community.notice;

import archivegarden.shop.entity.Notice;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class NoticeListDto {

    private Long id;
    private String title;
    private int hit;
    private String writer;
    private String createdAt;

    public NoticeListDto(Notice notice) {
        this.id = notice.getId();
        this.title = notice.getTitle();
        this.hit = notice.getHit();
        this.writer = notice.getMember().getName();
        this.createdAt = DateTimeFormatter.ofPattern("yyyy.MM.dd").format(notice.getCreatedAt());
    }
}
