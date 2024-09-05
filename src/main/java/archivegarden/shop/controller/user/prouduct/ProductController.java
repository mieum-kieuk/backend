package archivegarden.shop.controller.user.prouduct;

import archivegarden.shop.dto.product.ProductPopupResultDto;
import archivegarden.shop.dto.product.ProductPopupSearchCondition;
import archivegarden.shop.dto.product.ProductDetailsDto;
import archivegarden.shop.dto.product.ProductListDto;
import archivegarden.shop.dto.product.ProductSearchCondition;
import archivegarden.shop.entity.Member;
import archivegarden.shop.service.product.ProductService;
import archivegarden.shop.service.product.WishService;
import archivegarden.shop.web.annotation.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final WishService wishService;


    /**
     * 상품 상세 페이지를 조회하는 요청을 처리하는 메서드
     */
    @GetMapping("/{productId}")
    public String product(@PathVariable("productId") Long productId, @CurrentUser Member loginMember, Model model) {
        ProductDetailsDto productDto = productService.getProduct(productId);
        boolean isWish = wishService.isWish(productId, loginMember);
        model.addAttribute("product", productDto);
        model.addAttribute("wish", isWish);
        return "user/products/product_details";
    }

    /**
     * 상품 목록을 조회하는 요청을 처리하는 메서드
     */
    @GetMapping
    public String products(@ModelAttribute("condition") ProductSearchCondition condition,
                           @RequestParam(value = "page", defaultValue = "1") Integer page, Model model) {
        PageRequest pageRequest = PageRequest.of(page - 1, 12);
        Page<ProductListDto> pageProducts = productService.getProducts(condition, pageRequest);
        String pathVariable = condition.getCategory() != null ? condition.getCategory().getPathVariable() : null;
        String sortedCode = condition.getSorted_type() != null ? condition.getSorted_type().getSortedCode() : null;
        model.addAttribute("pathVariable", pathVariable);
        model.addAttribute("sortedCode", sortedCode);
        model.addAttribute("products", pageProducts);
        return "user/products/product_list";
    }

    @GetMapping("/search")
    public String searchPopupProducts(@ModelAttribute("condition") ProductPopupSearchCondition condition, Model model) {
        if (StringUtils.hasText(condition.getKeyword())) {
            PageRequest pageRequest = PageRequest.of(condition.getPage() - 1, condition.getLimit());
            Page<ProductPopupResultDto> productPopupDtos = productService.getPopupProducts(condition.getKeyword(), pageRequest);
            model.addAttribute("products", productPopupDtos);
        } else {
            model.addAttribute("products", null);
        }

        return "community/inquiry/inquiry_popup";
    }
}
