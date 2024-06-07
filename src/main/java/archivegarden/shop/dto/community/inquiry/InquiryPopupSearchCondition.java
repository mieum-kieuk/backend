package archivegarden.shop.dto.community.inquiry;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InquiryPopupSearchCondition {

    private String keyword;
    private Integer page;
    private Integer limit;

    public InquiryPopupSearchCondition() {
        this.page = 1;
        this.limit = 1;
    }
}
