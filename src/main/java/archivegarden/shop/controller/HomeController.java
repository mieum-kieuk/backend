package archivegarden.shop.controller;

import archivegarden.shop.dto.shop.product.ProductListDto;
import archivegarden.shop.dto.shop.product.ProductSearchCondition;
import archivegarden.shop.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProductService productService;

    @GetMapping
    public String home(Model model) {
        List<ProductListDto> products = productService.getMainProducts();
        model.addAttribute("products", products);
        return "index";
    }

    @GetMapping("/search")
    public String search(@ModelAttribute("condition") ProductSearchCondition condition,
                         @RequestParam(value = "page", defaultValue = "1") Integer page, Model model) {
        PageRequest pageRequest = PageRequest.of(page - 1, 3, Sort.by("id"));

        Page<ProductListDto> products = productService.getProducts(condition, pageRequest);
        model.addAttribute("products", products);
        return "search/search_complete";
    }
}
