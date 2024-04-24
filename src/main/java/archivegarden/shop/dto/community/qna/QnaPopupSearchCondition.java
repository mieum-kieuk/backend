package archivegarden.shop.dto.community.qna;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QnaPopupSearchCondition {

    private String keyword;
    private Integer page;
    private Integer limit;

    public QnaPopupSearchCondition() {
        this.page = 1;
        this.limit = 1;
    }
}
