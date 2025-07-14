package archivegarden.shop.dto.admin.product.product;

import archivegarden.shop.entity.Discount;
import archivegarden.shop.entity.Product;
import lombok.Getter;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class AdminProductDetailsDto {

    private Long id;
    private String name;
    private String categoryName;
    private String price;
    private String salePrice;
    private String discountName;
    private String stockQuantity;
    private String details;
    private String size;
    private String shipping;
    private String notice;
    private String displayImage;
    private String hoverImage;
    private List<String> detailImages = new ArrayList<>();

    public AdminProductDetailsDto(Product product, List<AdminProductImageDto> productImageDtos) {
        this.id = product.getId();
        this.name = product.getName();
        this.categoryName = product.getCategory().getDisplayName();
        this.price = new DecimalFormat("###,###원").format(product.getPrice());

        Discount discount = product.getDiscount();
        if (discount != null && isDateBetween(discount.getStartedAt(), discount.getExpiredAt())) {
            this.discountName = "[" + product.getDiscount().getDiscountPercent() + "%] " + product.getDiscount().getName();
            double salePriceDouble = product.getPrice() - (double) product.getPrice() * discount.getDiscountPercent() / 100;
            this.salePrice = new DecimalFormat("###,###원").format(Math.round(salePriceDouble));
        } else {
            this.discountName = "적용된 할인 혜택이 없습니다";
            this.salePrice = this.price;
        }

        this.stockQuantity = new DecimalFormat("###,###개").format(product.getStockQuantity());
        this.details = product.getDetails();
        this.size = product.getSize();
        this.shipping = product.getShipping();
        this.notice = product.getNotice();
        for (AdminProductImageDto image : productImageDtos) {
            switch (image.getImageType()) {
                case DISPLAY -> this.displayImage = image.getImageData();
                case HOVER -> this.hoverImage = image.getImageData();
                case DETAILS -> this.detailImages.add(image.getImageData());
            }
        }
    }

    private boolean isDateBetween(LocalDateTime startedAt, LocalDateTime expiredAt) {
        LocalDateTime now = LocalDateTime.now();
        return (now.isAfter(startedAt) || now.isEqual(startedAt)) && (now.isBefore(expiredAt) || now.isEqual(expiredAt));
    }
}
