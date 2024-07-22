package archivegarden.shop.dto.product;

import archivegarden.shop.entity.Discount;
import archivegarden.shop.entity.ImageType;
import archivegarden.shop.entity.Product;
import lombok.Getter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ProductDetailsDto {

    private Long id;
    private String name;
    private String category;
    private String price;
    private boolean isDiscounted;
    private int discountPercent;
    private String salePrice;
    private boolean isSoldOut;
    private int point;
    private String details;
    private String sizeGuide;
    private String shipping;
    private String notice;
    private List<String> displayImages = new ArrayList<>();
    private List<String> detailsImages = new ArrayList<>();

    public ProductDetailsDto(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.category = product.getCategory().getDisplayName();
        this.price = new DecimalFormat("###,###원").format(product.getPrice());

        Discount discount = product.getDiscount();
        if (discount != null) {
            this.isDiscounted = true;
            this.discountPercent = discount.getDiscountPercent();
            double salePriceDouble = product.getPrice() - (double) product.getPrice() * discount.getDiscountPercent() / 100;
            this.salePrice = new DecimalFormat("###,###원").format(Math.round(salePriceDouble));
//            this.point =  Math.round(salePriceDouble));
        } else {
            this.point = Math.round((float)product.getPrice() / 100);
        }

        if(product.getStockQuantity() <= 0) {
            this.isSoldOut = true;
        }

        details = product.getDetails();
        sizeGuide = product.getSizeGuide();
        shipping = product.getShipping();
        notice = product.getNotice();
        product.getProductImages().forEach(image -> {
            if (image.getImageType() == ImageType.DISPLAY) {
                this.displayImages.add(image.getStoreImageName());
            } else {
                this.detailsImages.add(image.getStoreImageName());
            }
        });
    }
}
