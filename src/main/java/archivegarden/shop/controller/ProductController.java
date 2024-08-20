package archivegarden.shop.controller;

import archivegarden.shop.dto.community.inquiry.ProductPopupDto;
import archivegarden.shop.dto.community.inquiry.PopupProductSearchCondition;
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
        return "product/product_list";
    }

    @GetMapping("/{productId}")
    public String product(@PathVariable("productId") Long productId, @CurrentUser Member loginMember, Model model) {
        ProductDetailsDto productDto = productService.getProduct(productId);
        model.addAttribute("product", productDto);


        boolean isWish = wishService.isWish(productId, loginMember);
        model.addAttribute("wish", isWish);
        return "product/product_details";
    }

    @GetMapping("/search")
    public String searchPopupProducts(@ModelAttribute("condition") PopupProductSearchCondition condition, Model model) {
        if (StringUtils.hasText(condition.getKeyword())) {
            PageRequest pageRequest = PageRequest.of(condition.getPage() - 1, condition.getLimit());
            Page<ProductPopupDto> productPopupDtos = productService.getPopupProducts(condition.getKeyword(), pageRequest);
            model.addAttribute("products", productPopupDtos);
        } else {
            model.addAttribute("products", null);
        }

        return "community/inquiry/inquiry_popup";
    }
}
