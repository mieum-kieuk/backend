package archivegarden.shop.dto.admin.product.discount;

import archivegarden.shop.dto.admin.product.product.AdminProductSummaryDto;
import archivegarden.shop.entity.Discount;
import archivegarden.shop.entity.Product;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

@Getter
public class AdminDiscountDetailsDto {

    private Long id;
    private String name;
    private String discountPercent;
    private String startedAt;
    private String expiredAt;
    private boolean editable;
    private List<AdminProductSummaryDto> products = new LinkedList<>();

    public AdminDiscountDetailsDto(Discount discount) {
        this.id = discount.getId();
        this.name = discount.getName();
        this.discountPercent = discount.getDiscountPercent() + "%";
        this.startedAt = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분").format(discount.getStartedAt());
        this.expiredAt = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분").format(discount.getExpiredAt());
        this.editable = discount.getStartedAt().isBefore(LocalDateTime.now()) ? false : true;
        List<Product> products = discount.getProducts();
        for (Product p : products) {
            this.products.add(new AdminProductSummaryDto(p.getId(), p.getName(), p.getPrice(), p.getProductImages().get(0).getImageUrl()));
        }
    }
}
