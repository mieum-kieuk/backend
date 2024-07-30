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
import java.util.Optional;

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
    @JoinColumn(name = "discount_id")
    private Discount discount;  //다대일 단방향

    //==비즈니스 로직==//
    /**
     * 이미지 한장 추가
     */
    private void addProductImage(ProductImage productImage) {
        this.productImages.add(productImage);
        productImage.setProduct(this);
    }

    /**
     * 이미지 한장 삭제
     */
    public void removeImage(ProductImage productImage) {
        this.productImages.remove(productImage);
    }

    /**
     * 이미지 여러장 추가
     */
    public void addProductImages(List<ProductImage> productImages) {
        productImages.stream().forEach(image -> addProductImage(image));
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
     * 사진 한장 수정
     */
    public void updateImage(ProductImage productImage) {
        addProductImage(productImage);
    }

    /**
     * 사진 여러장 수정
     */
    public void updateImages(List<ProductImage> productImages) {
        productImages.forEach(this::addProductImage);
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

    //==생성자==//
    @Builder
    public Product(AddProductForm form, ProductImage displayImage1, ProductImage displayImage2, List<ProductImage> detailsImages) {
        this.name = form.getName();
        this.category = form.getCategory();
        this.price = form.getPrice();
        this.stockQuantity = form.getStockQuantity();
        this.details = form.getDetails();
        this.sizeGuide = form.getSizeGuide();
        this.shipping = form.getShipping();
        this.notice = form.getNotice();
        this.addProductImage(displayImage1);
        Optional.ofNullable(displayImage2).ifPresent(this::addProductImage);
        Optional.ofNullable(detailsImages).ifPresent(this::addProductImages);
    }
}
