package archivegarden.shop.dto.user.community.notice;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NoticeSearchForm {

    private String searchDate;
    private String searchKey;
    private String keyword;
}
