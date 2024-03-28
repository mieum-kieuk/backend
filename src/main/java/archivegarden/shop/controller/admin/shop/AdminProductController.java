package archivegarden.shop.controller.admin.shop;

import archivegarden.shop.dto.admin.shop.product.ProductDto;
import archivegarden.shop.dto.admin.shop.product.ProductSaveForm;
import archivegarden.shop.entity.Category;
import archivegarden.shop.service.admin.shop.AdminProductService;
import archivegarden.shop.service.upload.FileStore;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/shop/products")
public class AdminProductController {

    private final AdminProductService productService;
    private final FileStore fileStore;

    @ModelAttribute("productCategories")
    public List<Category> productCategories() {
        List<Category> productCategories = new ArrayList<>();
        Collections.addAll(productCategories, Category.values());
        return productCategories;
    }

    @GetMapping("/add")
    public String addProductForm(@ModelAttribute("product") ProductSaveForm form) {
        return "admin/shop/product/add_product";
    }

    @PostMapping("/add")
    public String addProduct(@Valid @ModelAttribute("product") ProductSaveForm form, BindingResult bindingResult, RedirectAttributes redirectAttributes) throws IOException {

        System.out.println("form = " + form.getDisplayImage1().getName());
        if (bindingResult.hasErrors()) {
            return "admin/shop/product/add_product";
        }

        Long productId = productService.saveProduct(form);
        redirectAttributes.addAttribute("productId", productId);
        return "redirect:/admin/shop/products/{productId}";
    }

    @GetMapping("/{productId}")
    public String productDetails(@PathVariable("productId") Long productId, Model model) {
        ProductDto productDto = productService.getProduct(productId);
        model.addAttribute("product", productDto);
        return "admin/shop/product/product_details";
    }
}
