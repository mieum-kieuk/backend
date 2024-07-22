package archivegarden.shop.dto.community.inquiry;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PopupProductSearchCondition {

    private String keyword;
    private Integer page;
    private Integer limit;

    public PopupProductSearchCondition() {
        this.page = 1;
        this.limit = 5;
    }
}
