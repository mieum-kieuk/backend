package archivegarden.shop.entity;

import archivegarden.shop.dto.admin.product.product.AddProductForm;
import archivegarden.shop.dto.admin.product.product.EditProductForm;
import archivegarden.shop.exception.ajax.NotEnoughStockAjaxException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(length = 20, nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Category category;

    @Column(nullable = false)
    private int price;

    @Column(name = "stock_quantity", nullable = false)
    private int stockQuantity;

    @Lob
    @Column(nullable = false)
    private String details;

    @Lob
    @Column(name = "size_guide", nullable = false)
    private String sizeGuide;

    @Lob
    @Column(nullable = false)
    private String shipping;

    @Lob
    @Column(nullable = false)
    private String notice;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> productImages = new ArrayList<>();  //다대일 양방향

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Discount discount;  //단방향

    @Builder
    public Product(AddProductForm form, ProductImage displayImage, ProductImage hoverImage, List<ProductImage> detailsImages) {
        this.name = form.getName();
        this.category = form.getCategory();
        this.price = form.getPrice();
        this.stockQuantity = form.getStockQuantity();
        this.details = form.getDetails();
        this.sizeGuide = form.getSizeGuide();
        this.shipping = form.getShipping();
        this.notice = form.getNotice();
        addProductImage(displayImage);
        if(hoverImage != null) addProductImage(hoverImage);
        if(detailsImages.size() > 0) detailsImages.forEach(img -> this.addProductImage(img));
    }

    /**
     * 상품 이미지 추가
     */
    public void addProductImage(ProductImage productImage) {
        this.productImages.add(productImage);
        productImage.setProduct(this);
    }

    /**
     * 상품 이미지 제거
     */
    public void removeImage(ProductImage productImage) {
        this.productImages.remove(productImage);
    }

    /**
     * 상품 수정
     */
    public void update(EditProductForm form) {
        this.name = form.getName();
        this.category = form.getCategory();
        this.price = form.getPrice();
        this.stockQuantity = form.getStockQuantity();
        this.details = form.getDetails();
        this.sizeGuide = form.getSizeGuide();
        this.shipping = form.getShipping();
        this.notice = form.getNotice();
    }

    /**
     * stock 증가
     */
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    /**
     * stock 감소
     */
    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            throw new NotEnoughStockAjaxException("재고가 부족합니다.");
        }
    }

}
