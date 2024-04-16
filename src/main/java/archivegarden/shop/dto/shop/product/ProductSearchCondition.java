package archivegarden.shop.dto.shop.product;

import archivegarden.shop.entity.Category;
import archivegarden.shop.entity.SortedType;
import lombok.Getter;

@Getter
public class ProductSearchCondition {

    private String keyword;
    private Category category;
    private SortedType sorted_type;

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setCategory(String category) {
        this.category = Category.of(category);
    }

    public void setSorted_type(String sortedCode) {
        this.sorted_type = SortedType.of(sortedCode);
    }
}
