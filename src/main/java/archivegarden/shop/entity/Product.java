package archivegarden.shop.entity;

import archivegarden.shop.dto.admin.shop.product.AddProductForm;
import archivegarden.shop.exception.NotEnoughStockException;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(nullable = false)
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

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductImage> images = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_id")
    private Discount discount;

    //==비즈니스 로직==//
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
            throw new NotEnoughStockException("재고가 부족합니다.");
        }
    }

    /**
     * 이미지 추가
     */
    private void addProductImage(ProductImage image){
        images.add(image);
        image.setProduct(this);
    }

    //==생성자 메서드==//
    public static Product createProduct(AddProductForm form, ProductImage displayImage, ProductImage hoverImage, List<ProductImage> detailsImages, Discount discount) {
        Product product = new Product();
        product.name = form.getName();
        product.category = form.getCategory();
        product.price = form.getPrice();
        product.stockQuantity = form.getStockQuantity();
        product.details = form.getDetails();
        product.sizeGuide = form.getSizeGuide();
        product.shipping = form.getShipping();
        product.notice = form.getShipping();
        product.addProductImage(displayImage);
        product.addProductImage(hoverImage);
        for (ProductImage detailsImage : detailsImages) {
            product.addProductImage(detailsImage);
        }
        product.discount = discount;
        return product;
    }
}
