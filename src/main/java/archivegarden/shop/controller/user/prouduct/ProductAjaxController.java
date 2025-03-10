package archivegarden.shop.controller.user.prouduct;

import archivegarden.shop.dto.user.product.ProductPopupSearchCondition;
import archivegarden.shop.dto.user.product.ProductSummaryDto;
import archivegarden.shop.service.user.product.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ajax")
@RequiredArgsConstructor
public class ProductAjaxController {

    private final ProductService productService;

    /**
     * 상품 문의할 상품을 팝업창에서 검색하는 메서드
     */
    @ResponseBody
    @GetMapping("/products/search")
    public Page<ProductSummaryDto> searchProductsInPopup(@ModelAttribute("condition") ProductPopupSearchCondition condition) {
        PageRequest pageRequest = PageRequest.of(condition.getPage() - 1, condition.getLimit());
        Page<ProductSummaryDto> productPopupDtos = productService.searchProductsInPopup(condition, pageRequest);
        return productPopupDtos;
    }
}
