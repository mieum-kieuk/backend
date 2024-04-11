package archivegarden.shop.controller;

import archivegarden.shop.dto.shop.product.ProductDetailsDto;
import archivegarden.shop.dto.shop.product.ProductListDto;
import archivegarden.shop.dto.shop.product.ProductSearchCondition;
import archivegarden.shop.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/shop/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping(value = {"", "/{category}"})
    public String products(@ModelAttribute("condition") ProductSearchCondition condition,
                           @RequestParam(value = "page", defaultValue = "1") Integer page, Model model) {
        PageRequest pageRequest = PageRequest.of(page - 1, 3, Sort.by("id"));

        Page<ProductListDto> pageProducts = productService.getProducts(condition, pageRequest);
        model.addAttribute("products", pageProducts);
        return "shop/product_list";
    }

    @GetMapping("/details/{productId}")
    public String product(@PathVariable("productId") Long productId, Model model) {
        ProductDetailsDto productDto = productService.getProduct(productId);
        model.addAttribute("product", productDto);
        return "shop/product_details";
    }
}
