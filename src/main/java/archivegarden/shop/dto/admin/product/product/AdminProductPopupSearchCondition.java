package archivegarden.shop.dto.admin.product.product;

import archivegarden.shop.entity.Category;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
public class AdminProductPopupSearchCondition {

    private String keyword;
    private Category category;
    private Integer page;
    private Integer limit;
    private List<Long> selectedProductIds;

    public AdminProductPopupSearchCondition() {
        this.page = 1;
        this.limit = 5;
        this.selectedProductIds = new LinkedList<>();
    }
}
