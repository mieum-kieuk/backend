package archivegarden.shop.controller.user;

import archivegarden.shop.dto.user.product.ProductListDto;
import archivegarden.shop.service.user.product.product.ProductService;
import archivegarden.shop.util.PageRequestUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "메인 페이지", description = "사용자 페이지 홈, 검색, 소개 페이지 관련 API")
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProductService productService;

    @Operation(
            summary = "메인 페이지 조회",
            description = "최신 상품 9개를 조회하여 메인 페이지에 반환합니다."
    )
    @GetMapping
    public String home(Model model) {
        List<ProductListDto> products = productService.getLatestProducts();
        model.addAttribute("products", products);
        return "index";
    }

    @Operation(
            summary = "상품 검색",
            description = "키워드로 상품을 검색하고 검색 결과 페이지를 반환합니다."
    )
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

    @Operation(
            summary = "About 페이지 조회",
            description = "About 페이지를 반환합니다."
    )
    @GetMapping("/about")
    public String about() {
        return "user/about/about";
    }
}
