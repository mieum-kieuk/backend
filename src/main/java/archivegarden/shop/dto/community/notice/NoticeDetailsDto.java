package archivegarden.shop.dto.community.notice;

import archivegarden.shop.entity.Board;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class NoticeDetailsDto {

    private Long id;
    private String title;
    private String content;
    private String writer;
    private String createdAt;

    public NoticeDetailsDto(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.writer = board.getMember().getName();
        this.createdAt = DateTimeFormatter.ofPattern("yyyy.MM.dd").format(board.getCreatedAt());
    }
}
