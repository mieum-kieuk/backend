package archivegarden.shop.dto.product;

import archivegarden.shop.entity.Category;
import archivegarden.shop.entity.SortedType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductSearchCondition {

    private Category category;
    private SortedType sorted_type;

    public void setCategory(String category) {
        this.category = Category.of(category);
    }

    public void setSorted_type(String sortedCode) {
        this.sorted_type = SortedType.of(sortedCode);
    }
}
