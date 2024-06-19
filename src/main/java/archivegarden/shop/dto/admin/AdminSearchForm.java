package archivegarden.shop.dto.admin;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class AdminSearchForm {

    private String searchKey;
    private String keyword;
    private LocalDate startDate;
    private LocalDate endDate;
}
