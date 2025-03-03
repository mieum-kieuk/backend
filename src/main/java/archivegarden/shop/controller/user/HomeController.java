package archivegarden.shop.controller.user;

import archivegarden.shop.dto.user.product.ProductListDto;
import archivegarden.shop.service.user.product.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProductService productService;

    /**
     * 메인 페이지 조회 요청을 처리하는 메서드
     */
    @GetMapping
    public String home(Model model) {
        List<ProductListDto> products = productService.getLatestProducts();
        model.addAttribute("products", products);
        return "index";
    }

    /**
     * 상품 검색 요청을 처리하는 메서드
     */
    @GetMapping("/search")
    public String search(@RequestParam(name = "keyword") String keyword, @RequestParam(value = "page", defaultValue = "1") Integer page, Model model) {
        PageRequest pageRequest = PageRequest.of(page - 1, 12);
        Page<ProductListDto> products = productService.searchProducts(keyword, pageRequest);
        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);
        return "user/search/search_complete";
    }

    /**
     * About 페이지 조회 요청을 처리하는 메서드
     */
    @GetMapping("/about")
    public String about() {
        return "user/about/about";
    }
}
