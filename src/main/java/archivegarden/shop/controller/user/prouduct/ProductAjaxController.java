package archivegarden.shop.controller.user.prouduct;

import archivegarden.shop.dto.user.product.ProductPopupSearchCondition;
import archivegarden.shop.dto.user.product.ProductSummaryDto;
import archivegarden.shop.service.user.product.product.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@Tag(name = "상품", description = "사용자 페이지에서 상품 관련 API")
@RestController
@RequestMapping("/ajax")
@RequiredArgsConstructor
public class ProductAjaxController {

    private final ProductService productService;

    @Operation(
            summary = "팝업창에서 상품 검색",
            description = "문의할 상품을 팝업창에서 검색하고 상품 요약 정보를 반환합니다.",
            responses = {@ApiResponse(responseCode = "200", description = "상품 검색 성공")}
    )
    @ResponseBody
    @GetMapping("/products/search")
    public Page<ProductSummaryDto> searchProductsInPopup(@ModelAttribute("cond") ProductPopupSearchCondition cond) {
        PageRequest pageRequest = PageRequest.of(cond.getPage() - 1, cond.getLimit());
        Page<ProductSummaryDto> productPopupDtos = productService.searchProductsInPopup(cond, pageRequest);
        return productPopupDtos;
    }
}
