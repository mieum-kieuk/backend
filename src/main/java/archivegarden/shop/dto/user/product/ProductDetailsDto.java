package archivegarden.shop.dto.user.product;

import archivegarden.shop.entity.Discount;
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
    private long point;
    private String details;
    private String sizeGuide;
    private String shipping;
    private String notice;
    private String displayImage;
    private String hoverImage;
    private List<String> detailsImages = new ArrayList<>();

    public ProductDetailsDto(Product product, List<ProductImageDto> productImageDtos) {
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
            this.point = Math.round(salePriceDouble);
        } else {
            this.point = Math.round((float)product.getPrice() / 100);
        }

        if(product.getStockQuantity() <= 0) {
            this.isSoldOut = true;
        }

        details = product.getDetails();
        sizeGuide = product.getSize();
        shipping = product.getShipping();
        notice = product.getNotice();
        for (ProductImageDto image : productImageDtos) {
            switch (image.getImageType()) {
                case DISPLAY -> this.displayImage = image.getImageData();
                case HOVER -> this.hoverImage = image.getImageData();
                case DETAILS -> this.detailsImages.add(image.getImageData());
            }
        }
    }
}
