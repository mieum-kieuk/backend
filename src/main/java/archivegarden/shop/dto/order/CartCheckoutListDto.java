package archivegarden.shop.dto.order;

import archivegarden.shop.entity.Cart;
import archivegarden.shop.entity.Discount;
import archivegarden.shop.entity.ImageType;
import archivegarden.shop.entity.Product;
import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;

@Getter
@Setter
public class CartCheckoutListDto {

    private Long id;    //productId
    private String name;
    private int count;
    private int stockQuantity;
    private String displayImage;

    private boolean isDiscounted;
    private String totalPrice;    //상품 정가 합
    private String totalSalePrice;  //상품 판매가 합

    public CartCheckoutListDto(Cart cart) {
        Product product = cart.getProduct();

        this.id = product.getId();
        this.name = product.getName();
        this.count = cart.getCount();
        this.stockQuantity = cart.getProduct().getStockQuantity();

        product.getImages().stream().forEach(image -> {
            if (image.getImageType() == ImageType.DISPLAY) {
                this.displayImage = image.getStoreImageName();
            }
        });

        this.totalPrice =new DecimalFormat("###,###원").format( product.getPrice() * count);

        Discount discount = product.getDiscount();
        if (discount != null) { //할인 중인 상품 O
            this.isDiscounted = Boolean.TRUE;
            int discountPercent = discount.getDiscountPercent();
            int discountAmount = product.getPrice() * discountPercent / 100;
            this.totalSalePrice = new DecimalFormat("###,###원").format((product.getPrice() - discountAmount) * count);
        }
    }
}
