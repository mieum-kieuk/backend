package archivegarden.shop.service.admin.product;

import archivegarden.shop.dto.admin.product.product.*;
import archivegarden.shop.dto.community.inquiry.ProductPopupDto;
import archivegarden.shop.entity.ImageType;
import archivegarden.shop.entity.Product;
import archivegarden.shop.entity.ProductImage;
import archivegarden.shop.exception.admin.AdminNotFoundException;
import archivegarden.shop.exception.ajax.AjaxNotFoundException;
import archivegarden.shop.repository.product.ProductImageRepository;
import archivegarden.shop.repository.product.ProductRepository;
import archivegarden.shop.service.upload.ProductFileStore;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminProductService {

    private final ProductFileStore fileStore;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;

    /**
     * 상품 저장
     *
     * @throws AdminNotFoundException
     */
    public Long saveProduct(AddProductForm form) throws IOException {

        //ProductImage 생성
        ProductImage displayImage = fileStore.storeFile(form.getDisplayImage(), ImageType.DISPLAY);
        ProductImage hoverImage = !form.getHoverImage().isEmpty() ? fileStore.storeFile(form.getHoverImage(), ImageType.HOVER) : null;
        List<ProductImage> detailsImages = !form.getDetailsImages().isEmpty() ? fileStore.storeFiles(form.getDetailsImages(), ImageType.DETAILS) : null;

        //Product 생성
        Product product = Product.builder()
                .form(form)
                .displayImage1(displayImage)
                .displayImage2(hoverImage)
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
    @Transactional(readOnly = true)
    public Page<ProductListDto> getProducts(AdminProductSearchForm form, Pageable pageable) {
        return productRepository.findProductAll(form, pageable)
                .map(ProductListDto::new);
    }

    /**
     * 상품 수정 폼 조회
     *
     * @throws AdminNotFoundException
     */
    @Transactional(readOnly = true)
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
        Product product = productRepository.findById(productId).orElseThrow(() -> new AdminNotFoundException("존재하지 않는 상품입니다."));

        //Product 수정
        product.update(form);

        //==DISPLAY ProductImage 수정==//
        if (!form.getDisplayImage().isEmpty()) {
            //ProductImage 삭제
            ProductImage displayImage = product.getProductImages()
                    .stream()
                    .filter(productImage -> productImage.getImageType() == ImageType.DISPLAY)
                    .findFirst()
                    .orElseThrow(() -> new AdminNotFoundException("존재하지 않는 상품 이미지 입니다."));

            product.removeImage(displayImage);

            //ProductImage 생성
            ProductImage newDisplayImage = fileStore.storeFile(form.getDisplayImage(), ImageType.DISPLAY);
            product.addProductImage(newDisplayImage);
        }

        //HOVER ProductImage 수정==//
        //ProductImage 삭제
        if (form.isHoverImageDeleted()) {
            ProductImage hoverImage = product.getProductImages()
                    .stream()
                    .filter(productImage -> productImage.getImageType() == ImageType.HOVER)
                    .findFirst()
                    .orElseThrow(() -> new AdminNotFoundException("존재하지 않는 상품 이미지 입니다."));

            product.removeImage(hoverImage);
        }

        //ProductImage 생성
        if(!form.getHoverImage().isEmpty()) {
            ProductImage newHoverImage = fileStore.storeFile(form.getHoverImage(), ImageType.HOVER);
            product.addProductImage(newHoverImage);
        }

        //DETAILS ProductImage 수정==//
        //ProductImage 삭제
        List<Long> detailsImageIds = product.getProductImages()
                .stream()
                .filter(productImage -> productImage.getImageType() == ImageType.DETAILS)
                .map(productImage -> productImage.getId())
                .collect(Collectors.toList());

        List<String> deleteDetailsImages = form.getDeleteDetailsImages();
        for(int i = 0; i < detailsImageIds.size(); i++) {
            String idx = "FILE_" + detailsImageIds.get(i);
            if(!deleteDetailsImages.contains(idx)) {
                ProductImage productImage = productImageRepository.findById(detailsImageIds.get(i)).orElseThrow(() -> new AdminNotFoundException("존재하지 않는 상품 이미지 입니다."));
                product.removeImage(productImage);
            }
        }

        //ProductImage 생성
        if (!form.getDetailsImages().isEmpty()) {
            List<ProductImage> productImages = fileStore.storeFiles(form.getDetailsImages(), ImageType.DETAILS);
            product.addProductImages(productImages);
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

    /**
     * Ajax 상품명 중복 검사
     */
    public boolean isAvailableName(String name) {
        return productRepository.findByName(name).isEmpty();
    }

    /**
     * 팝업창
     * 상품 목록 조회 + 페이지네이션
     */
    public Page<ProductPopupDto> getPopupProducts(String keyword, Pageable pageable) {
        return productRepository.findDtoAllPopup(keyword, pageable);
    }
}
