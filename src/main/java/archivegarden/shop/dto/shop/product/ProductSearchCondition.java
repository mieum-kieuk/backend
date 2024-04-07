package archivegarden.shop.dto.shop.product;

import archivegarden.shop.entity.Category;
import lombok.Getter;

@Getter
public class ProductSearchCondition {

    public String keyword;
    private Category category;

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setCategory(String category) {
        this.category = Category.of(category);
    }
}
