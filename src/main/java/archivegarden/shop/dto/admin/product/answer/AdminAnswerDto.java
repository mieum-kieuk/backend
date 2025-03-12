package archivegarden.shop.dto.admin.product.answer;

import archivegarden.shop.entity.Answer;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
public class AdminAnswerDto {

    private Long id;
    private String content;
    private String createdAt;

    public AdminAnswerDto(Answer answer) {
        this.id = answer.getId();
        this.content = answer.getContent();
        this.createdAt = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss").format(answer.getCreatedAt());
    }
}
