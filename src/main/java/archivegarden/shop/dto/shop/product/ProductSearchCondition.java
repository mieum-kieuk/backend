package archivegarden.shop.dto.shop.product;

import archivegarden.shop.entity.Category;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductSearchCondition {

    public String name;
    private Category category;

    public ProductSearchCondition(Category category) {
        this.category = category;
    }
}
