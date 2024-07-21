package archivegarden.shop.service.admin.product;

import archivegarden.shop.dto.admin.product.product.*;
import archivegarden.shop.entity.ImageType;
import archivegarden.shop.entity.Product;
import archivegarden.shop.entity.ProductImage;
import archivegarden.shop.exception.admin.AdminNotFoundException;
import archivegarden.shop.exception.ajax.AjaxNotFoundException;
import archivegarden.shop.repository.product.ProductRepository;
import archivegarden.shop.service.upload.ProductFileStore;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminProductService {

    private final ProductFileStore fileStore;
    private final ProductRepository productRepository;

    /**
     * 상품 저장
     *
     * @throws AdminNotFoundException
     */
    public Long saveProduct(AddProductForm form) throws IOException {

        //ProductImage 생성
        ProductImage displayImage1 = fileStore.storeFile(form.getDisplayImage1(), ImageType.DISPLAY);
        ProductImage displayImage2 = !form.getDisplayImage2().isEmpty() ? fileStore.storeFile(form.getDisplayImage2(), ImageType.DISPLAY) : null;
        List<ProductImage> detailsImages = !form.getDetailsImages().isEmpty() ? fileStore.storeFiles(form.getDetailsImages(), ImageType.DETAILS) :  null;

        //Product 생성
        Product product = Product.builder()
                .form(form)
                .displayImage1(displayImage1)
                .displayImage2(displayImage2)
                .detailsImages(detailsImages)
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
        Product product = productRepository.findByIdFetchJoin(productId).orElseThrow(() -> new AdminNotFoundException("존재하지 않는 상품입니다."));

        return new ProductDetailsDto(product);
    }

    /**
     * 상품 목록 조회
     */
    public Page<ProductListDto> getProducts(AdminProductSearchForm form, Pageable pageable) {
        return productRepository.findProductAll(form, pageable)
                .map(ProductListDto::new);
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

//    /**
//     * 상품 수정
//     *
//     * @throws AdminNotFoundException
//     */
//    public void updateProduct(Long productId, EditProductForm form) throws IOException {
//        //Product 조회
//        Product product = productRepository.findAllWithImages(productId).orElseThrow(() -> new AdminNotFoundException("존재하지 않는 상품입니다."));
//
//        //Product수정
//        product.update(form);
//
//        //ProductImage 수정
//        if (!form.getDisplayImage().getOriginalFilename().equals("")) {
//            ProductImage displayImage = fileStore.storeFile(form.getDisplayImage(), ImageType.DISPLAY);
//            product.updateDisplayImage(displayImage);
//        }
//
//        if (!form.getHoverImage().getOriginalFilename().equals("")) {
//            ProductImage hoverImage = fileStore.storeFile(form.getHoverImage(), ImageType.HOVER);
//            product.updateHoverImage(hoverImage);
//        }
//
//        if (!form.getDetailsImages().isEmpty()) {
//            List<ProductImage> productImages = fileStore.storeFiles(form.getDetailsImages(), ImageType.DETAILS);
//            product.addDetailsImage(productImages);
//        }
//    }


    /**
     * Ajax 상품명 중복 검사
     */
    public boolean isAvailableName(String name) {
        return productRepository.findByName(name).isEmpty();
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
