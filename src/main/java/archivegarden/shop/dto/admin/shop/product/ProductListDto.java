package archivegarden.shop.dto.admin.shop.product;

import archivegarden.shop.entity.Discount;
import archivegarden.shop.entity.ImageType;
import archivegarden.shop.entity.Product;
import lombok.Getter;

import java.text.DecimalFormat;

@Getter
public class ProductListDto {

    private Long id;
    private String name;
    private String categoryName;
    private String price;
    private String salePrice;
    private String stockQuantity;
    private String displayImage1;

    public ProductListDto(Product product) {
        id = product.getId();
        name = product.getName();
        categoryName = product.getCategory().getDisplayName();
        price = new DecimalFormat("###,###").format(product.getPrice());
        salePrice =  new DecimalFormat("###,###").format(discount(product.getPrice(), product.getDiscount()));
        stockQuantity = new DecimalFormat("###,###").format(product.getStockQuantity());
        product.getImages().forEach(img -> {
            if(img.getImageType()  == ImageType.DISPLAY) {
                displayImage1 = img.getStoreImageName();
            }
        });
    }

    private int discount(int price, Discount discount) {
        int discountPercent = discount.getDiscountPercent();
        int discountPrice = price * discountPercent / 100;
        return price - discountPrice;
    }
}
