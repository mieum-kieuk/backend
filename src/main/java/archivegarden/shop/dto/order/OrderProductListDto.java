package archivegarden.shop.dto.order;

import archivegarden.shop.entity.Cart;
import archivegarden.shop.entity.Discount;
import archivegarden.shop.entity.Product;
import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;
import java.util.stream.Collectors;

@Getter
@Setter
public class OrderProductListDto {

    private Long id;    //productId
    private String name;
    private int count;
    private String displayImage;
    private boolean isDiscounted;
    private String totalPrice;    //상품 정가 합
    private String totalSalePrice;  //상품 판매가 합

    public OrderProductListDto(Cart cart) {
        Product product = cart.getProduct();

        this.id = product.getId();
        this.name = product.getName();
        this.count = cart.getCount();
        this.displayImage = product.getProductImages().stream()
                .map((image) -> image.getStoreImageName())
                .collect(Collectors.toList())
                .get(0);
        this.totalPrice = new DecimalFormat("###,###원").format( product.getPrice() * count);

        Discount discount = product.getDiscount();
        if (discount != null) { //할인 중인 상품 O
            this.isDiscounted = Boolean.TRUE;
            double salePriceDouble = product.getPrice() - (double) product.getPrice() * discount.getDiscountPercent() / 100;
            this.totalSalePrice = new DecimalFormat("###.###원").format(Math.round(salePriceDouble * this.count));
        }
    }
}
