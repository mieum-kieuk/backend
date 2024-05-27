package archivegarden.shop.entity;

import archivegarden.shop.dto.admin.shop.product.AddProductForm;
import archivegarden.shop.dto.admin.shop.product.EditProductForm;
import archivegarden.shop.exception.ajax.NotEnoughStockAjaxException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    private List<ProductImage> images = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_id")
    private Discount discount;  //다대일 단방향

    //==비즈니스 로직==//
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
     * 섬네일 사진 1 변경
     * <p>
     * 1. 수정: 기존에 존재하던 이미지 삭제 -> 추가
     * 2. 삭제 -> 추가: Ajax 통신 통해 삭제 이미 이루어짐 -> 추가
     */
    public void updateDisplayImage(ProductImage displayImage) {
        //1. 수정의 경우 기존에 존재하던 사진 제거
        this.images.stream()
                .filter(image -> image.getImageType() == ImageType.DISPLAY)
                .collect(Collectors.toList())
                .forEach(image -> images.remove(image));


        addProductImage(displayImage);
    }

    /**
     * 섬네일 사진 2 변경
     * <p>
     * 1. 수정: 기존에 존재하던 이미지 삭제 -> 추가
     * 2. 삭제 -> 추가: Ajax 통신 통해 삭제 이미 이루어짐 -> 추가
     */
    public void updateHoverImage(ProductImage hoverImage) {
        this.images.stream()
                .filter(image -> image.getImageType() == ImageType.HOVER)
                .collect(Collectors.toList())
                .forEach(image -> images.remove(image));

        addProductImage(hoverImage);
    }

    /**
     * 상세 페이지 사진 추가
     */
    public void addDetailsImage(List<ProductImage> productImages) {
        productImages.stream().forEach(image -> addProductImage(image));
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

    /**
     * 이미지 추가
     */
    private void addProductImage(ProductImage image) {
        images.add(image);
        image.setProduct(this);
    }

    //==생성자==//
    @Builder
    public Product(AddProductForm form, ProductImage displayImage, ProductImage hoverImage, List<ProductImage> detailsImages, Discount discount) {
        this.name = form.getName();
        this.category = form.getCategory();
        this.price = form.getPrice();
        this.stockQuantity = form.getStockQuantity();
        this.details = form.getDetails();
        this.sizeGuide = form.getSizeGuide();
        this.shipping = form.getShipping();
        this.notice = form.getNotice();
        this.addProductImage(displayImage);

        if(hoverImage != null) {
            this.addProductImage(hoverImage);
        }

        for (ProductImage detailsImage : detailsImages) {
            this.addProductImage(detailsImage);
        }
        this.discount = discount;
    }
}
