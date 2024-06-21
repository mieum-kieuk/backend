package archivegarden.shop.service.admin.shop;

import archivegarden.shop.dto.admin.product.product.ProductImageDto;
import archivegarden.shop.entity.ImageType;
import archivegarden.shop.entity.Product;
import archivegarden.shop.entity.ProductImage;
import archivegarden.shop.exception.ajax.NoSuchProductAjaxException;
import archivegarden.shop.exception.ajax.NoSuchImageAjaxException;
import archivegarden.shop.repository.product.ProductImageRepository;
import archivegarden.shop.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;

    /**
     * 첨부파일 전체 조회
     */
    public List<ProductImageDto> findProductImages(Long productId) {
        List<ProductImage> productImages = productImageRepository.findByProductId(productId);
        return productImages.stream()
                .map(i -> new ProductImageDto(i))
                .collect(Collectors.toList());
    }

    /**
     * 섬네일 첨부파일 조회
     *
     * @throws NoSuchProductAjaxException
     */
    public ProductImageDto findDisplayImage(Long productId) {
        //엔티티 조회
        Product product = productRepository.findById(productId).orElseThrow(() -> new NoSuchProductAjaxException("존재하지 않는 상품입니다."));

        ProductImage displayImage = productImageRepository.findDisplayImage(ImageType.DISPLAY, product);
        return new ProductImageDto(displayImage);
    }

    /**
     * 섬네일 첨부파일 조회
     *
     * @throws NoSuchProductAjaxException
     */
    public ProductImageDto findHoverImage(Long productId) {
        //엔티티 조회
        Product product = productRepository.findById(productId).orElseThrow(() -> new NoSuchProductAjaxException("존재하지 않는 상품입니다."));

        ProductImage hoverImage = productImageRepository.findHoverImage(ImageType.HOVER, product);
        if(hoverImage != null) {
            return new ProductImageDto(hoverImage);
        } else {
            return null;
        }
    }

    /**
     * 첨부파일 삭제
     *
     * @throws NoSuchImageAjaxException
     */
    public void deleteImage(Long productImageId) {
        //엔티티 조회
        ProductImage productImage = productImageRepository.findById(productImageId).orElseThrow(() -> new NoSuchImageAjaxException("존재하지 않는 이미지입니다."));

        //첨부파일 삭제
        productImageRepository.delete(productImage);
    }
}
