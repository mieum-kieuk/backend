package archivegarden.shop.service.admin.shop;

import archivegarden.shop.dto.admin.product.product.*;
import archivegarden.shop.entity.Discount;
import archivegarden.shop.entity.ImageType;
import archivegarden.shop.entity.Product;
import archivegarden.shop.entity.ProductImage;
import archivegarden.shop.exception.admin.AdminNotFoundException;
import archivegarden.shop.exception.ajax.AjaxNotFoundException;
import archivegarden.shop.repository.discount.AdminDiscountRepository;
import archivegarden.shop.repository.product.ProductRepository;
import archivegarden.shop.service.upload.AdminFileStore;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminProductService {

    private final ProductRepository productRepository;
    private final AdminDiscountRepository discountRepository;
    private final AdminFileStore fileStore;

    /**
     * 상품 저장
     *
     * @throws AdminNotFoundException
     */
    public Long saveProduct(AddProductForm form) throws IOException {
        //ProductImage 생성
        ProductImage displayImage = fileStore.storeFile(form.getDisplayImage(), ImageType.DISPLAY);
        ProductImage hoverImage = !form.getHoverImage().isEmpty() ? fileStore.storeFile(form.getHoverImage(), ImageType.HOVER) : null;
        List<ProductImage> detailsImages = !form.getDetailsImages().isEmpty() ? fileStore.storeFiles(form.getDetailsImages(), ImageType.DETAILS) :  new ArrayList<>();

        //Discount 조회
        Discount discount = null;
        if (form.getDiscountId() != null) {
            discount = discountRepository.findById(form.getDiscountId()).orElseThrow(() -> new AdminNotFoundException("존재하지 않는 할인 혜택입니다."));
        }

        //Product 생성
        Product product = Product.builder()
                .form(form)
                .displayImage(displayImage)
                .hoverImage(hoverImage)
                .detailsImages(detailsImages)
                .discount(discount)
                .build();

        //Product 저장
        productRepository.save(product);

        return product.getId();
    }

    /**
     * 상품 단건 조회
     *
     * @throws AdminNotFoundException
     */
    @Transactional(readOnly = true)
    public ProductDetailsDto getProduct(Long productId) {
        //Product 조회
        Product product = productRepository.findById(productId).orElseThrow(() -> new AdminNotFoundException("존재하지 않는 상품입니다."));

        return new ProductDetailsDto(product);
    }

    /**
     * 상품 목록 조회
     */
    public Page<ProductListDto> getProducts(ProductSearchForm form, Pageable pageable) {
        return productRepository.findAdminDtoAll(form, pageable);
    }

    /**
     * 상품 수정 폼 조회
     *
     * @throws AdminNotFoundException
     */
    public EditProductForm getEditProductForm(Long productId) {
        //Product 조회
        Product product = productRepository.findById(productId).orElseThrow(() -> new AdminNotFoundException("존재하지 않는 상품입니다."));

        return new EditProductForm(product);
    }

    /**
     * 상품 수정
     *
     * @throws AdminNotFoundException
     */
    public void updateProduct(Long productId, EditProductForm form) throws IOException {
        //Product 조회
        Product product = productRepository.findAllWithImages(productId).orElseThrow(() -> new AdminNotFoundException("존재하지 않는 상품입니다."));

        //Product수정
        product.update(form);

        //ProductImage 수정
        if (!form.getDisplayImage().getOriginalFilename().equals("")) {
            ProductImage displayImage = fileStore.storeFile(form.getDisplayImage(), ImageType.DISPLAY);
            product.updateDisplayImage(displayImage);
        }

        if (!form.getHoverImage().getOriginalFilename().equals("")) {
            ProductImage hoverImage = fileStore.storeFile(form.getHoverImage(), ImageType.HOVER);
            product.updateHoverImage(hoverImage);
        }

        if (!form.getDetailsImages().isEmpty()) {
            List<ProductImage> productImages = fileStore.storeFiles(form.getDetailsImages(), ImageType.DETAILS);
            product.addDetailsImage(productImages);
        }
    }

    /**
     * Ajax: 상품 단건 삭제
     *
     * @throws AjaxNotFoundException
     */
    public void deleteProduct(Long productId) {
        //Product 조회
        Product product = productRepository.findById(productId).orElseThrow(() -> new AjaxNotFoundException("존재하지 않는 상품입니다."));

        //Product 삭제
        productRepository.delete(product);
    }

    /**
     * Ajax: 상품 여러건 삭제
     *
     * @throws AjaxNotFoundException
     */
    public void deleteProducts(List<Long> productIds) {
        productIds.stream().forEach(productId -> {
            //Product 조회
            Product product = productRepository.findById(productId).orElseThrow(() -> new AjaxNotFoundException("존재하지 않는 상품입니다."));

            //Product 삭제
            productRepository.delete(product);
        });
    }
}
