package archivegarden.shop.dto.shop.product;

import archivegarden.shop.entity.Discount;
import archivegarden.shop.entity.ImageType;
import archivegarden.shop.entity.Product;
import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;

@Getter
@Setter
public class ProductListDto {

    private Long id;
    private String name;
    private String displayImage;
    private String hoverImage;
    private String price;
    private boolean isDiscounted;
    private int discountPercent;
    private String salePrice;
    private boolean isSoldOut;
    private String order;


    public ProductListDto(Long productId, String productName, int productPrice, int discountPercent, int discountPrice, ImageType imageType, String imageName) {
        this.id = productId;
        this.name = productName;

//        product.getImages().stream().forEach(image -> {
//            if (image.getImageType() == ImageType.DISPLAY) {
//                this.displayImage = image.getStoreImageName();
//            } else if (image.getImageType() == ImageType.HOVER) {
//                this.hoverImage = image.getStoreImageName();
//            }
//        });

        this.price = new DecimalFormat("###,###").format(productPrice);

//        Discount discount = product.getDiscount();
//        if (discount != null) {
//            this.isDiscounted = Boolean.TRUE;
//            this.discountPercent = discount.getDiscountPercent();
//            int discountAmount = productPrice * discountPercent / 100;
//            this.salePrice = new DecimalFormat("###,###").format(productPrice - discountAmount);
//        }

//        if (productStockQuantity <= 0) {
//            this.isSoldOut = true;
//        }
    }

    public ProductListDto(Product product) {
        this.id = product.getId();
        this.name = product.getName();

        product.getImages().stream().forEach(image -> {
            if (image.getImageType() == ImageType.DISPLAY) {
                this.displayImage = image.getStoreImageName();
            } else if (image.getImageType() == ImageType.HOVER) {
                this.hoverImage = image.getStoreImageName();
            }
        });

        this.price = new DecimalFormat("###,###").format(product.getPrice());

        Discount discount = product.getDiscount();
        if (discount != null) {
            this.isDiscounted = Boolean.TRUE;
            this.discountPercent = discount.getDiscountPercent();
            int discountAmount = product.getPrice() * discountPercent / 100;
            this.salePrice = new DecimalFormat("###,###").format(product.getPrice() - discountAmount);
        }

        if (product.getStockQuantity() <= 0) {
            this.isSoldOut = true;
        }
    }
}
