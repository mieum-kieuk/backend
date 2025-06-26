package archivegarden.shop.controller.user.prouduct;

import archivegarden.shop.dto.user.product.ProductDetailsDto;
import archivegarden.shop.dto.user.product.ProductListDto;
import archivegarden.shop.dto.user.product.ProductSearchCondition;
import archivegarden.shop.entity.Member;
import archivegarden.shop.security.service.AccountContext;
import archivegarden.shop.service.product.WishService;
import archivegarden.shop.service.user.product.product.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Tag(name = "상품", description = "사용자 페이지에서 상품 관련 API")
@Controller
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final WishService wishService;

    @Operation(
            summary = "상품 상세 조회",
            description = "상품 상세 정보를 조회합니다."
    )
    @GetMapping("/{productId}")
    public String product(
            @PathVariable("productId") Long productId,
            @AuthenticationPrincipal Object principal,
            Model model
    ) {
        ProductDetailsDto productDto = productService.getProduct(productId);

        boolean isWish = false;
        if (principal instanceof AccountContext accountContext) {
            Member member = accountContext.getMember();
            isWish = wishService.isWish(productId, member);
        }

        model.addAttribute("product", productDto);
        model.addAttribute("wish", isWish);
        return "user/product/product_details";
    }

    @Operation(
            summary = "상품 목록 조회",
            description = "상품 목록을 페이징하여 조회합니다"
    )
    @GetMapping
    public String products(
            @ModelAttribute("cond") ProductSearchCondition cond,
            @RequestParam(value = "page", defaultValue = "1") int page,
            Model model
    ) {
        PageRequest pageRequest = PageRequest.of(page - 1, 12);
        Page<ProductListDto> pageProducts = productService.getProducts(cond, pageRequest);
        String pathVariable = cond.getCategory() != null ? cond.getCategory().getPathVariable() : null;
        String sortedCode = cond.getSorted_type() != null ? cond.getSorted_type().getSortedCode() : null;
        model.addAttribute("pathVariable", pathVariable);
        model.addAttribute("sortedCode", sortedCode);
        model.addAttribute("products", pageProducts);
        return "user/product/product_list";
    }

    @Operation(
            summary = "상품 선택 팝업창 화면 반환",
            description = "문의할 상품을 선택하기 위한 팝업창 화면을 반환합니다."
    )
    @GetMapping("/search")
    public String showPopup() {
        return "user/community/inquiry/product_popup";
    }
}
