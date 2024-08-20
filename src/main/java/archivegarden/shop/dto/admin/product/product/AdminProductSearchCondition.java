package archivegarden.shop.dto.admin.product.product;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdminProductSearchCondition {

    private String searchKey;
    private String keyword;
    private String category;
}
