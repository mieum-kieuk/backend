package archivegarden.shop.controller;

import archivegarden.shop.dto.community.qna.QnaPopupDto;
import archivegarden.shop.dto.community.qna.QnaPopupSearchCondition;
import archivegarden.shop.dto.shop.product.ProductDetailsDto;
import archivegarden.shop.dto.shop.product.ProductListDto;
import archivegarden.shop.dto.shop.product.ProductSearchCondition;
import archivegarden.shop.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/shop/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public String products(@ModelAttribute("condition") ProductSearchCondition condition,
                           @RequestParam(value = "page", defaultValue = "1") Integer page, Model model) {

        PageRequest pageRequest = PageRequest.of(page - 1, 3);
        Page<ProductListDto> pageProducts = productService.getProducts(condition, pageRequest);

        String pathVariable = condition.getCategory() != null ? condition.getCategory().getPathVariable() : null;
        String sortedCode = condition.getSorted_type() != null ? condition.getSorted_type().getSortedCode() : null;
        model.addAttribute("pathVariable", pathVariable);
        model.addAttribute("sortedCode", sortedCode);
        model.addAttribute("products", pageProducts);
        return "shop/product_list";
    }

    @GetMapping("/{productId}")
    public String product(@PathVariable("productId") Long productId, Model model) {
        ProductDetailsDto productDto = productService.getProduct(productId);
        model.addAttribute("product", productDto);
        return "shop/product_details";
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ROLE_USER')")
    public String searchPopupProducts(@ModelAttribute("condition") QnaPopupSearchCondition condition, Model model) {
        if (condition.getKeyword() != null) {
            PageRequest pageRequest = PageRequest.of(condition.getPage() - 1, condition.getLimit());
            Page<QnaPopupDto> qnaPopupDtos = productService.getPopupProducts(pageRequest, condition.getKeyword());
            model.addAttribute("products", qnaPopupDtos);
        } else {
            model.addAttribute("products", null);
        }

        return "community/qna/qna_popup";
    }
}
