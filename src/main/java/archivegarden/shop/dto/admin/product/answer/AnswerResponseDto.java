package archivegarden.shop.dto.admin.product.answer;

import archivegarden.shop.exception.Answer;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
public class AnswerResponseDto {

    private Long id;
    private String name;
    private String content;
    private String createdAt;

    public AnswerResponseDto(Answer answer) {
        this.id = answer.getId();
        this.name = answer.getAdmin().getName() + " (관리자)";
        this.content = answer.getContent();
        this.createdAt = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss").format(answer.getCreatedAt());
    }
}
