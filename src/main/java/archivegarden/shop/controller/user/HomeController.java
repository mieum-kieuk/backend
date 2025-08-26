package archivegarden.shop.controller.user;

import archivegarden.shop.dto.user.product.ProductListDto;
import archivegarden.shop.service.user.product.product.ProductService;
import archivegarden.shop.util.PageRequestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProductService productService;

    @GetMapping
    public String home() {
        return "index";
    }

    @GetMapping("/search")
    public String search(
            @RequestParam(name = "keyword") String keyword,
            @RequestParam(value = "page", defaultValue = "1") int page,
            Model model
    ) {
        if(!StringUtils.hasText(keyword))  return "user/search/search_complete";

        PageRequest pageRequest = PageRequestUtil.of(page, 12);
        Page<ProductListDto> products = productService.searchProducts(keyword, pageRequest);
        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);
        return "user/search/search_complete";
    }

    @GetMapping("/about")
    public String about() {
        return "user/about/about";
    }
}
