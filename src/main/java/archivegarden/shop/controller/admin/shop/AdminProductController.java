package archivegarden.shop.controller.admin.shop;

import archivegarden.shop.dto.admin.shop.product.AddProductForm;
import archivegarden.shop.dto.admin.shop.product.ProductDetailsDto;
import archivegarden.shop.dto.admin.shop.product.ProductListDto;
import archivegarden.shop.dto.admin.shop.product.EditProductForm;
import archivegarden.shop.entity.Category;
import archivegarden.shop.service.admin.promotion.discount.AdminDiscountService;
import archivegarden.shop.service.admin.shop.AdminProductService;
import archivegarden.shop.service.upload.FileStore;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/shop/products")
public class AdminProductController {

    private final AdminProductService productService;
    private final AdminDiscountService discountService;
    private final FileStore fileStore;

    @ModelAttribute("productCategories")
    public List<Category> productCategories() {
        List<Category> productCategories = new ArrayList<>();
        Collections.addAll(productCategories, Category.values());
        return productCategories;
    }

    @GetMapping
    public String products(@PageableDefault(size = 12, sort = "id") Pageable pageable, Model model) {
        Page<ProductListDto> products = productService.getProducts(pageable);
        model.addAttribute("products", products);
        return "admin/shop/product/product_list";
    }

    @GetMapping("/add")
    public String addProductForm(@ModelAttribute("product") AddProductForm form, Model model) {
        initModel(model);
        return "admin/shop/product/add_product";
    }

    @PostMapping("/add")
    public String addProduct(@Valid @ModelAttribute("product") AddProductForm form, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) throws IOException {

        //파일 업로드 검증
        if (form.getDisplayImage1() == null || form.getDisplayImage1().getOriginalFilename().equals("")) {
            bindingResult.rejectValue("displayImage1", "imageRequired", "상품 목록에 보일 이미지를 첨부해 주세요.");
        }

        if (bindingResult.hasErrors()) {
            initModel(model);
            return "admin/shop/product/add_product";
        }

        Long productId = productService.saveProduct(form);
        redirectAttributes.addAttribute("productId", productId);
        return "redirect:/admin/shop/products/{productId}";
    }

    @GetMapping("/{productId}")
    public String productDetails(@PathVariable("productId") Long productId, Model model) {
        ProductDetailsDto productDetailsDto = productService.getProduct(productId);
        model.addAttribute("product", productDetailsDto);
        return "admin/shop/product/product_details";
    }

    @GetMapping("/{productId}/edit")
    public String editProductForm(@PathVariable("productId") Long productId, Model model) {
        initModel(model);

        EditProductForm product = productService.getEditProductForm(productId);
        model.addAttribute("product", product);
        return "admin/shop/product/edit_product";
    }

    @PostMapping("/{productId}/edit")
    public String editProduct(@PathVariable("productId") Long productId, @Valid @ModelAttribute("product") EditProductForm form, BindingResult bindingResult, Model model) throws IOException {
        initModel(model);

        //파일 업로드 검증
        if ((form.getDisplayImage1() == null || form.getDisplayImage1().getOriginalFilename().equals("")) && form.getIsDisplayImageChanged()) {
            bindingResult.rejectValue("displayImage1", "imageRequired", "상품 목록에 보일 이미지를 첨부해 주세요.");
        }

        if(bindingResult.hasErrors()) {
            return "admin/shop/product/edit_product";
        }

        productService.updateProduct(productId, form);
        return "redirect:/admin/shop/products/{productId}";
    }

    @GetMapping("/{productId}/delete")
    public String deleteProduct(@PathVariable("productId") Long productId) {
        productService.deleteProduct(productId);
        return "redirect:/admin/shop/products";
    }

    @ResponseBody
    @PostMapping("/delete")
    public boolean deleteProducts(@RequestBody List<Long> productIds) {
        productService.deleteProducts(productIds);
        return true;
    }

    @ResponseBody
    @GetMapping("/images/{filename}")
    public Resource downloadImage(@PathVariable String filename) throws MalformedURLException {
        return new UrlResource("file:" + fileStore.getFullPath(filename));
    }

    private void initModel(Model model) {
        Map<Long, String> discounts = discountService.getDiscountNames();
        model.addAttribute("discounts", discounts);
    }
}
