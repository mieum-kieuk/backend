package archivegarden.shop.service.admin.shop;

import archivegarden.shop.dto.admin.shop.product.AddProductForm;
import archivegarden.shop.dto.admin.shop.product.ProductDetailsDto;
import archivegarden.shop.dto.admin.shop.product.ProductListDto;
import archivegarden.shop.dto.admin.shop.product.EditProductForm;
import archivegarden.shop.entity.Discount;
import archivegarden.shop.entity.ImageType;
import archivegarden.shop.entity.Product;
import archivegarden.shop.entity.ProductImage;
import archivegarden.shop.exception.NoSuchDiscountException;
import archivegarden.shop.exception.NoSuchProductException;
import archivegarden.shop.exception.ajax.NoSuchProductAjaxException;
import archivegarden.shop.repository.admin.promotion.AdminDiscountRepository;
import archivegarden.shop.repository.admin.shop.AdminProductRepository;
import archivegarden.shop.service.upload.FileStore;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminProductService {

    private final AdminProductRepository productRepository;
    private final AdminDiscountRepository discountRepository;
    private final FileStore fileStore;

    /**
     * 상품 저장
     */
    public Long saveProduct(AddProductForm form) throws IOException {
        //상품 이미지 엔티티 생성
        ProductImage displayImage = fileStore.storeFile(form.getDisplayImage1(), ImageType.DISPLAY);
        ProductImage hoverImage = null;
        List<ProductImage> detailsImages = new ArrayList<>();

        if(!form.getDisplayImage2().isEmpty()) {
            hoverImage = fileStore.storeFile(form.getDisplayImage2(), ImageType.HOVER);
        }
        if(!form.getDetailsImages().isEmpty()) {
            detailsImages = fileStore.storeFiles(form.getDetailsImages(), ImageType.DETAILS);
        }

        //할인 엔티티 조회
        Discount discount = discountRepository.findById(form.getDiscountId()).orElseThrow(() -> new NoSuchDiscountException("존재하지 않는 할인 혜택입니다."));

        //상품 엔티티 생성
        Product product = Product.createProduct(form, displayImage, hoverImage, detailsImages, discount);

        //상품 저장
        productRepository.save(product);

        return product.getId();
    }

    /**
     * 상품 단건 조회
     *
     * @throws NoSuchProductException productId로 DB에서 데이터 찾을 수 없을 때
     */
    @Transactional(readOnly = true)
    public ProductDetailsDto getProduct(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new NoSuchProductException("존재하지 않는 상품입니다."));
        return new ProductDetailsDto(product);
    }

    /**
     * 상품 목록 조회
     */
    public Page<ProductListDto> getProducts(Pageable pageable) {
        return productRepository.findAll(pageable).map(ProductListDto::new);
    }

    /**
     * 상품 수정 폼 조회
     *
     * @throws NoSuchProductException productId로 DB에서 데이터 찾을 수 없을 때
     */
    public EditProductForm getEditProductForm(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new NoSuchProductException("존재하지 않는 상품입니다."));
        return new EditProductForm(product);
    }

    /**
     * 상품 단건 삭제
     *
     * @throws NoSuchProductException productId로 DB에서 데이터 찾을 수 없을 때
     */
    public void deleteProduct(Long productId) {
        //엔티티 조회
        Product product = productRepository.findById(productId).orElseThrow(() -> new NoSuchProductException("존재하지 않는 상품입니다."));

        //상품 삭제
        productRepository.delete(product);
    }

    /**
     * Ajax
     * 상품 여러개 삭제
     *
     * @throws NoSuchProductAjaxException productId로 DB에서 데이터 찾을 수 없을 때
     */
    public void deleteProducts(List<Long> productIds) {
        productIds.stream().forEach(productId -> {
            Product product = productRepository.findById(productId).orElseThrow(() -> new NoSuchProductAjaxException("존재하지 않는 상품입니다."));
            productRepository.delete(product);
        });
    }
}
