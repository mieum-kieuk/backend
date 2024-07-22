package archivegarden.shop.controller.admin.product;

import archivegarden.shop.dto.admin.product.product.*;
import archivegarden.shop.dto.community.inquiry.PopupProductSearchCondition;
import archivegarden.shop.dto.community.inquiry.ProductPopupDto;
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

    //상품 저장 폼
    @GetMapping("/add")
    public String addProductForm(@ModelAttribute("product") AddProductForm form, Model model) {
        model.addAttribute("categories", categorySelectBox());
        return "admin/product/products/add_product";
    }

    //상품 저장
    @PostMapping("/add")
    public String addProduct(@Valid @ModelAttribute("product") AddProductForm form, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) throws IOException {

        validateAttachImage(form.getDisplayImage1(), form.getDetailsImages(), bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categorySelectBox());
            return "admin/product/products/add_product";
        }

        Long productId = productService.saveProduct(form);
        redirectAttributes.addAttribute("productId", productId);
        return "redirect:/admin/products/{productId}";
    }

    //상품 단건 조회
    @GetMapping("/{productId}")
    public String productDetails(@PathVariable("productId") Long productId, Model model) {
        ProductDetailsDto productDetailsDto = productService.getProduct(productId);
        model.addAttribute("product", productDetailsDto);
        return "admin/product/products/product_details";
    }

    //상품 여러건 조회
    @GetMapping
    public String products(@ModelAttribute("form") AdminProductSearchForm form, @RequestParam(name = "page", defaultValue = "1") int page, Model model) {
        PageRequest pageRequest = PageRequest.of(page - 1, 10);
        Page<ProductListDto> products = productService.getProducts(form, pageRequest);
        model.addAttribute("products", products);
        return "admin/product/products/product_list";
    }

   //상품 수정 폼
    @GetMapping("/{productId}/edit")
    public String editProductForm(@PathVariable("productId") Long productId, Model model) {
        EditProductForm product = productService.getEditProductForm(productId);
        model.addAttribute("product", product);
        model.addAttribute("categories", categorySelectBox());
        return "admin/product/products/edit_product";
    }
/*
    @PostMapping("/{productId}/edit")
    public String editProduct(@PathVariable("productId") Long productId, @Valid @ModelAttribute("product") EditProductForm form, BindingResult bindingResult, Model model) throws IOException {
        //섬네일 사진1 검증
        if ((form.getDisplayImage() == null || form.getDisplayImage().getOriginalFilename().equals("")) && form.getIsDisplayImageChanged()) {
            bindingResult.rejectValue("displayImage", "imageRequired", "상품 목록에 보일 이미지를 첨부해 주세요.");
        }

        //상세 페이지 사진 검증
        if(form.getDetailsImages().size() > 20) {
            bindingResult.rejectValue("detailsImages", "imageCountLimit", "상세 페이지 사진은 20장까지 첨부가능합니다.");
        }

        if(bindingResult.hasErrors()) {
            model.addAttribute("categories", categorySelectBox());
            return "admin/product/products/edit_product";
        }

        productService.updateProduct(productId, form);
        return "redirect:/admin/products/{productId}";
    }

    @ResponseBody
    @GetMapping("/images/{filename}")
    public Resource downloadImage(@PathVariable String filename) throws MalformedURLException {
        return new UrlResource("file:" + fileStore.getFullPath(filename));
    }*/

    //카테고리 셀렉트 박스
    public List<Category> categorySelectBox() {
        List<Category> categories = new ArrayList<>();
        Collections.addAll(categories, Category.values());
        return categories;
    }

    //섬네일 사진1, 상세페이지 사진 유효성 검사
    private void validateAttachImage(MultipartFile displayImage1, List<MultipartFile> detailsImages, BindingResult bindingResult) {
        //섬네일 사진1 검증
        if (displayImage1 == null || displayImage1.isEmpty()) {
            bindingResult.rejectValue("displayImage1", "imageRequired", "섬네일 사진1을 첨부해 주세요.");
        }

        //상세 페이지 사진 검증
        if(detailsImages.size() > 20) {
            bindingResult.rejectValue("detailsImages", "imageCountLimit", "상세 페이지 사진은 20장까지 첨부가능합니다.");
        }
    }
}

