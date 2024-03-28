package archivegarden.shop.service.admin.shop;

import archivegarden.shop.dto.admin.shop.product.ProductDto;
import archivegarden.shop.dto.admin.shop.product.ProductSaveForm;
import archivegarden.shop.entity.ImageType;
import archivegarden.shop.entity.Product;
import archivegarden.shop.entity.ProductImage;
import archivegarden.shop.repository.admin.shop.AdminProductRepository;
import archivegarden.shop.service.upload.FileStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminProductService {

    private final AdminProductRepository productRepository;
    private final FileStore fileStore;

    /**
     * 상품 등록
     */
    public Long saveProduct(ProductSaveForm form) throws IOException {
        //상품 이미지 엔티티 생성
        ProductImage displayImage = fileStore.storeFile(form.getDisplayImage1(), ImageType.DISPLAY);
        ProductImage hoverImage = fileStore.storeFile(form.getDisplayImage2(), ImageType.HOVER);
        List<ProductImage> detailsImages = fileStore.storeFiles(form.getDetailsImages(), ImageType.DETAILS);

        //상품 엔티티 생성
        Product product = Product.createProduct(form, displayImage, hoverImage, detailsImages);

        //상품 저장
        productRepository.save(product);

        return product.getId();
    }

    /**
     * 상품 단건 조회
     */
    public ProductDto getProduct(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 상품입니다."));
        return new ProductDto(product);
    }
}
