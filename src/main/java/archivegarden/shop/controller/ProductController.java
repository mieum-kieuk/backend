package archivegarden.shop.controller;

import archivegarden.shop.dto.shop.product.ProductListDto;
import archivegarden.shop.entity.Category;
import archivegarden.shop.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/shop/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping(value = {"", "/{category}"})
    public String products(@PathVariable(value = "category", required = false) String pathVariable,
                           @RequestParam(value = "page", defaultValue = "1") Integer page, Model model) {
        PageRequest pageRequest = PageRequest.of(page - 1, 3, Sort.by("id"));
        Page<ProductListDto> pageProducts = productService.getProducts(Category.of(pathVariable), pageRequest);
        model.addAttribute("category", pathVariable);
        model.addAttribute("products", pageProducts);
        return "shop/product_list";
    }
}
