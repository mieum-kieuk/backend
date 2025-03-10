package archivegarden.shop.dto.user.product;

import archivegarden.shop.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProductPopupSearchCondition {

    private String keyword;
    private Category category;
    private Integer page;
    private Integer limit;

    public ProductPopupSearchCondition() {
        this.page = 1;
        this.limit = 5;
    }
}
