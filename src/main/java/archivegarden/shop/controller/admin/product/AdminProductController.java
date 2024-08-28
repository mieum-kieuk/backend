package archivegarden.shop.controller.admin.product;

import archivegarden.shop.dto.admin.product.product.*;
import archivegarden.shop.entity.Category;
import archivegarden.shop.service.admin.product.AdminProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/products")
public class AdminProductController {

    private final AdminProductService productService;

    /**
     * 상품 등록 폼을 반환하는 메서드
     */
    @GetMapping("/add")
    public String addProductForm(@ModelAttribute("addProductForm") AddProductForm form, Model model) {
        model.addAttribute("categories", categorySelectBox());
        return "admin/product/product/add_product";
    }

    /**
     * 상품 등록 요청을 처리하는 메서드
     */
    @PostMapping("/add")
    public String addProduct(@Valid @ModelAttribute("addProductForm") AddProductForm form, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) throws IOException {
        validateAttachImage(form.getDisplayImage(), form.getDetailsImages(), bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categorySelectBox());
            return "admin/product/product/add_product";
        }

        Long productId = productService.saveProduct(form);
        redirectAttributes.addAttribute("productId", productId);
        return "redirect:/admin/products/{productId}";
    }

    /**
     * 상품 상세 페이지를 조회하는 요청을 처리하는 메서드
     */
    @GetMapping("/{productId}")
    public String productDetails(@PathVariable("productId") Long productId, Model model) {
        ProductDetailsDto productDetailsDto = productService.getProduct(productId);
        model.addAttribute("product", productDetailsDto);
        return "admin/product/product/product_details";
    }

    /**
     * 상품 목록을 조회하는 요청을 처리하는 메서드
     */
    @GetMapping
    public String products(@ModelAttribute("form") AdminProductSearchCondition condition, @RequestParam(name = "page", defaultValue = "1") int page, Model model) {
        PageRequest pageRequest = PageRequest.of(page - 1, 10);
        Page<ProductListDto> products = productService.getProducts(condition, pageRequest);
        model.addAttribute("products", products);
        return "admin/product/product/product_list";
    }

    /**
     * 상품 수정 폼을 반환하는 메서드
     */
    @GetMapping("/{productId}/edit")
    public String editProductForm(@PathVariable("productId") Long productId, Model model) {
        EditProductForm product = productService.getEditProductForm(productId);
        model.addAttribute("product", product);
        model.addAttribute("categories", categorySelectBox());
        return "admin/product/product/edit_product";
    }

    /**
     * 상품 수정 요청을 처리하는 메서드
     */
    @PostMapping("/{productId}/edit")
    public String editProduct(@PathVariable("productId") Long productId, @Valid @ModelAttribute("product") EditProductForm form,
                              BindingResult bindingResult, Model model) throws IOException {
        if (form.getDetailsImages().size() > 20) {
            bindingResult.rejectValue("detailsImages", "imageCountExceeded");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categorySelectBox());
            return "admin/product/product/edit_product";
        }

        productService.updateProduct(productId, form);
        return "redirect:/admin/products/{productId}";
    }

/*    @GetMapping("/search")
    public String searchPopupProducts(@ModelAttribute("condition") PopupProductSearchCondition condition, Model model) {
        if (StringUtils.hasText(condition.getKeyword())) {
            PageRequest pageRequest = PageRequest.of(condition.getPage() - 1, condition.getLimit());
            Page<ProductPopupDto> productPopupDtos = productService.getPopupProducts(condition.getKeyword(), pageRequest);
            model.addAttribute("products", productPopupDtos);
        } else {
            model.addAttribute("products", null);
        }

        return "admin/product/discounts/discount_popup";
    }*/

    /**
     * 카테고리 조회하는 메서드
     */
    public List<Category> categorySelectBox() {
        List<Category> categories = new ArrayList<>();
        Collections.addAll(categories, Category.values());
        return categories;
    }

    /**
     * 상품 등록 폼의 복합적인 유효성 검증을 수행하는 메서드
     * - 섬네일 사진1: 섬네일 사진1이 존재하는지 검사합니다.
     * - 상세페이지 사진: 상세페이지 사진 개수를 검사합니다.
     */
    private void validateAttachImage(MultipartFile displayImage, List<MultipartFile> detailsImages, BindingResult bindingResult) {
        if (displayImage == null || displayImage.isEmpty()) {
            bindingResult.rejectValue("displayImage", "required");
        }

        if (detailsImages.size() > 20) {
            bindingResult.rejectValue("detailsImages", "imageCountLimit");
        }
    }
}

