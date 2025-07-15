package archivegarden.shop.controller.admin.product.product;

import archivegarden.shop.dto.ResultResponse;
import archivegarden.shop.dto.admin.product.product.AdminProductPopupSearchCondition;
import archivegarden.shop.dto.admin.product.product.AdminProductSummaryDto;
import archivegarden.shop.service.admin.product.AdminProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "상품", description = "관리자 페이지에서 상품 관련 AJAX API")
@RestController
@RequestMapping("/ajax/admin")
@RequiredArgsConstructor
public class AdminProductAjaxController {

    private final AdminProductService productService;

    @Operation(
            summary = "상품명 중복 검사",
            description = "상품명 사용 가능 여부를 확인합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "사용 가능한 상품명"),
                    @ApiResponse(responseCode = "400", description = "이미 사용 중인 상품명")
            }
    )
    @PostMapping("/product/check/name")
    public ResultResponse checkProductName(@RequestParam("name") String name) {
        boolean isAvailable = productService.isAvailableName(name);
        if(isAvailable) {
            return new ResultResponse(HttpStatus.OK.value(), "사용 가능한 상품명입니다.");
        } else {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), "이미 존재하는 상품명입니다.");
        }
    }


    @Operation(
            summary = "상품 삭제 요청",
            description = "상품을 삭제합니다. 상품 ID가 존재하지 않을 경우 오류를 반환합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "상품 삭제 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 상품"),
            }
    )
    @DeleteMapping("/product")
    public ResultResponse deleteProduct(@RequestParam("productId") Long productId) {
        productService.deleteProduct(productId);
        return new ResultResponse(HttpStatus.OK.value(), "상품이 삭제되었습니다.");
    }

    @Operation(
            summary = "상품 여러개 삭제 요청",
            description = "여러 개 상품을 삭제합니다. 존재하지 않는 상품 ID가 포함되면 오류를 반환합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "상품 다건 삭제 성공"),
                    @ApiResponse(responseCode = "400", description = "존재하지 않는 상품 포함"),
            }
    )
    @DeleteMapping("/products")
    public ResultResponse deleteProducts(@RequestBody List<Long> productIds) {
        productService.deleteProducts(productIds);
        return new ResultResponse(HttpStatus.OK.value(), "상품이 삭제되었습니다.");
    }


    @Operation(
            summary = "팝업창 내 상품 검색",
            description = "상품 할인 적용을 위한 상품을 팝업창에서 검색 조건에 따라 조회합니다."
    )
    @GetMapping("/products/search")
    public Page<AdminProductSummaryDto> searchProductsInPopup(@ModelAttribute("cond") AdminProductPopupSearchCondition cond) {
        PageRequest pageRequest = PageRequest.of(cond.getPage() - 1, cond.getLimit());
        Page<AdminProductSummaryDto> productPopupDtos = productService.searchProductsInPopup(cond, pageRequest);
        return productPopupDtos;
    }
}
