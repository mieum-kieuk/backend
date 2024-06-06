package archivegarden.shop.controller.admin.shop;

import archivegarden.shop.dto.admin.shop.product.ProductImageDto;
import archivegarden.shop.service.admin.shop.AdminProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/productImages")
@RequiredArgsConstructor
public class AdminProductImageController {

    private final AdminProductImageService productImageService;

    @GetMapping("/{productId}")
    public List<ProductImageDto> productImages(@PathVariable("productId") Long productId) {
        return productImageService.findProductImages(productId);
    }

    @PostMapping("/{productImageId}/delete")
    public void deleteImage(@PathVariable("productImageId") Long productImageId) {
        productImageService.deleteImage(productImageId);
    }
}
