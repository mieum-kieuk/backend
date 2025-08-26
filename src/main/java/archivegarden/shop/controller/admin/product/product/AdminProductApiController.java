package archivegarden.shop.controller.admin.product.product;

import archivegarden.shop.dto.admin.product.product.AdminProductPopupSearchCondition;
import archivegarden.shop.dto.admin.product.product.AdminProductSummaryDto;
import archivegarden.shop.dto.common.ApiResponseDto;
import archivegarden.shop.dto.common.AvailabilityResponseDto;
import archivegarden.shop.service.admin.product.AdminProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "상품-관리자-API", description = "관리자 페이지에서 상품 관련 데이터를 처리하는 API입니다.")
@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class AdminProductApiController {

    private final AdminProductService productService;

    @Operation(
            summary = "상품명 중복 검사",
            description = "상품명 사용 가능 여부를 확인합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청을 정상 처리하고 사용 가능 여부 결과를 반환합니다."),
                    @ApiResponse(responseCode = "400", description = "요청 형식이 올바르지 않습니다."),
                    @ApiResponse(responseCode = "403", description = "인증된 사용자는 해당 기능을 이용할 수 없습니다.")
            }
    )
    @GetMapping("/name/exists")
    public ResponseEntity<AvailabilityResponseDto> existsName(@RequestParam("name") String name) {
        if (!StringUtils.hasText(name)) {
            return ResponseEntity.badRequest()
                    .body(new AvailabilityResponseDto(false, "INVALID_FORMAT", "상품명 형식이 올바르지 않습니다."));
        }

        boolean isAvailable = productService.isAvailableName(name);
        String code = isAvailable ? "AVAILABLE" : "DUPLICATED";
        String message = isAvailable ? "사용 가능한 상품명입니다." : "이미 사용 중인 상품명입니다.";

        return ResponseEntity.ok()
                .body(new AvailabilityResponseDto(isAvailable, code, message));
    }

    @Operation(
            summary = "상품 1개 삭제 요청",
            description = "상품을 1개를 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "상품을 삭제했습니다."),
                    @ApiResponse(responseCode = "403", description = "관리자 권한이 있는 사용자만 이용 가능합니다."),
                    @ApiResponse(responseCode = "404", description = "카테고리가 존재하지 않습니다.")
            }
    )
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponseDto> deleteProduct(@PathVariable("productId") Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok(new ApiResponseDto("OK", "상품이 삭제되었습니다."));
    }

    @Operation(
            summary = "상품 여러개 삭제 요청",
            description = "상품 여러개를 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "상품을 삭제했습니다."),
                    @ApiResponse(responseCode = "403", description = "관리자 권한이 있는 사용자만 이용 가능합니다."),
                    @ApiResponse(responseCode = "404", description = "카테고리가 존재하지 않습니다.")
            }
    )
    @DeleteMapping("/products")
    public ResponseEntity<ApiResponseDto> deleteProducts(@RequestBody List<Long> productIds) {
        productService.deleteProducts(productIds);
        return ResponseEntity.ok(new ApiResponseDto("OK", "상품들이 삭제되었습니다."));
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
