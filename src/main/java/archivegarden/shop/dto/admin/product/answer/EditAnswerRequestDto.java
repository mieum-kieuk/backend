package archivegarden.shop.dto.admin.product.answer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EditAnswerRequestDto {

    private Long answerId;
    private String content;
}
