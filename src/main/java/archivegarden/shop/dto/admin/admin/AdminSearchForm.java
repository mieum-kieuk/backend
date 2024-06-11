package archivegarden.shop.dto.admin.admin;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdminSearchForm {

    private String searchKey;
    private String keyword;
    private String startDate;
    private String endDate;
}
