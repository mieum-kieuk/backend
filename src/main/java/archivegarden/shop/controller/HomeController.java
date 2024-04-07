package archivegarden.shop.controller;

import archivegarden.shop.dto.shop.product.ProductListDto;
import archivegarden.shop.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
}
