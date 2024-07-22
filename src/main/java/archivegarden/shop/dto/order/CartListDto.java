package archivegarden.shop.dto.order;

import archivegarden.shop.entity.Discount;
import archivegarden.shop.entity.ImageType;
import archivegarden.shop.entity.Product;
import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;

@Getter
@Setter
public class CartListDto {

    private Long id;    //productId
    private String name;
    private int count;
    private String displayImage;

    private String price;    //상품 1개 정가

    private boolean isDiscounted;
    private boolean isSoldOut;

    private int discountPercent;    //상품 할인율
    private String salePrice;    //상품 1개 판매가
    private String totalPrice;   //상품 1개 판매가 * 개수

    public CartListDto(Product product, int count) {
        this.id = product.getId();
        this.name = product.getName();
        this.count = count;

        product.getProductImages().stream()
                .filter(image -> image.getImageType() == ImageType.DISPLAY)
                .findFirst()
                .ifPresent(displayImage -> this.displayImage = displayImage.getStoreImageName());

        this.price = new DecimalFormat("###,###원").format(product.getPrice());

        Discount discount = product.getDiscount();
        if (discount == null) { //할인 중인 상품 X
            this.salePrice = this.price;
            this.totalPrice = new DecimalFormat("###,###원").format(product.getPrice() * count);
        } else { //할인 중인 상품 O
            this.isDiscounted = Boolean.TRUE;
            this.discountPercent = discount.getDiscountPercent();
            int discountAmount = product.getPrice() * discountPercent / 100;
            this.salePrice = new DecimalFormat("###,###원").format(product.getPrice() - discountAmount);
            this.totalPrice= new DecimalFormat("###,###원").format((product.getPrice() - discountAmount) * count);
        }

        if (product.getStockQuantity() <= 0) {
            this.isSoldOut = Boolean.TRUE;
        }
    }
}
