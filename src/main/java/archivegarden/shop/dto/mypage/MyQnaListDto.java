package archivegarden.shop.dto.mypage;

import archivegarden.shop.entity.Qna;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
public class MyQnaListDto {

    private Long id;
    private String title;
    private String createdAt;
    private String isReplied;

    public MyQnaListDto(Qna qna) {
        this.id = qna.getId();
        this.title = qna.getTitle();
        this.createdAt = DateTimeFormatter.ofPattern("yyyy.MM.dd").format(qna.getCreatedAt());
        this.isReplied = "X";
    }
}
