package archivegarden.shop.controller.admin.product.product;

import archivegarden.shop.dto.admin.product.product.*;
import archivegarden.shop.entity.Category;
import archivegarden.shop.exception.global.FileUploadException;
import archivegarden.shop.service.admin.product.AdminProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Tag(name = "Product 관리", description = "관리자 페이지에서 상품 관련 API")
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/products")
public class AdminProductController {

    private final AdminProductService productService;

    @Operation(
            summary = "상품 등록 폼 표시",
            description = "새로운 상품 등록을 위한 화면을 반환합니다."
    )
    @GetMapping("/add")
    public String addProductForm(@ModelAttribute("addForm") AdminAddProductForm form, Model model) {
        model.addAttribute("categories", categorySelectBox());
        return "admin/product/product/add_product";
    }

    @Operation(
            summary = "상품 등록 요청",
            description = "새로운 상품을 등록하고 상세 페이지로 리다이렉트합니다."
    )
    @PostMapping("/add")
    public String addProduct(
            @Valid @ModelAttribute("addForm") AdminAddProductForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        validateAttachImage(form.getDisplayImage(), form.getDetailImages(), bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categorySelectBox());
            return "admin/product/product/add_product";
        }

        try {
            Long productId = productService.addProduct(form);
            redirectAttributes.addAttribute("productId", productId);
            return "redirect:/admin/products/{productId}";
        } catch (FileUploadException e) {
            bindingResult.reject("error.global.fileUploadException", e.getMessage());
            model.addAttribute("categories", categorySelectBox());
            return "admin/product/product/add_product";
        }
    }

    @Operation(
            summary = "상세 상세 조회",
            description = "특정 상세의 상세 정보를 조회합니다."
    )
    @GetMapping("/{productId}")
    public String productDetails(@PathVariable("productId") Long productId, Model model) {
        AdminProductDetailsDto productDetailsDto = productService.getProduct(productId);
        model.addAttribute("product", productDetailsDto);
        return "admin/product/product/product_details";
    }

    @Operation(
            summary = "상품 목록 조회",
            description = "검색 조건에 따라 상품 목록을 페이징하여 조회합니다"
    )
    @GetMapping
    public String products(
            @ModelAttribute("cond") AdminProductSearchCondition cond,
            @RequestParam(name = "page", defaultValue = "1") int page,
            Model model
    ) {
        PageRequest pageRequest = PageRequest.of(page - 1, 10);
        Page<AdminProductListDto> products = productService.getProducts(cond, pageRequest);
        model.addAttribute("products", products);
        return "admin/product/product/product_list";
    }

    @Operation(
            summary = "상품 수정 폼 표시",
            description = "기존 상품을 수정하기 위한 화면을 반환합니다."
    )
    @GetMapping("/{productId}/edit")
    public String editProductForm(@PathVariable("productId") Long productId, Model model) {
        AdminEditProductForm editForm = productService.getEditProductForm(productId);
        model.addAttribute("editForm", editForm);
        model.addAttribute("categories", categorySelectBox());
        return "admin/product/product/edit_product";
    }

    @Operation(
            summary = "상품 수정 요청",
            description = "상품을 수정하고 상세 페이지로 리다이렉트합니다."
    )
    @PostMapping("/{productId}/edit")
    public String editProduct(
            @PathVariable("productId") Long productId,
            @Valid @ModelAttribute("editForm") AdminEditProductForm form,
            BindingResult bindingResult,
            Model model
    ) {
        if (form.getDetailImages().size() > 20) {
            bindingResult.rejectValue("detailsImages", "error.filed.detailsImages.imageCountExceeded");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categorySelectBox());
            return "admin/product/product/edit_product";
        }

        productService.updateProduct(productId, form);
        return "redirect:/admin/products/{productId}";
    }

    @Operation(
            summary = "상품 선택 팝업창 화면 반환",
            description = "상품 할인을 적용할 상품을 선택하기 위한 팝업창 화면을 반환합니다."
    )
    @GetMapping("/search")
    public String showPopup() {
        return "admin/product/discount/product_popup";
    }

    /**
     * 카테고리 목록 조회
     *
     * 상품 등록 및 수정 시, 카테고리 필드에 표시할 카테고리 목록을 조회합니다.
     */
    public List<Category> categorySelectBox() {
        List<Category> categories = new ArrayList<>();
        Collections.addAll(categories, Category.values());
        return categories;
    }

    /**
     * 상품 사진 등록 조건의 유효성 검증
     *
     * 다음 조건을 검사합니다:
     * - 섬네일 사진 1: 존재 여부
     * - 상세페이지 사진: 최대 20장 이하인지 여부
     *
     * @param displayImage 섬네일 사진 1
     * @param detailsImages 상세페이지 사진 목록
     * @param bindingResult 에러 발생 시 메시지를 등록할 객체
     */
    private void validateAttachImage(
            MultipartFile displayImage,
            List<MultipartFile> detailsImages,
            BindingResult bindingResult
    ) {
        if (displayImage == null || displayImage.isEmpty()) {
            bindingResult.rejectValue("displayImage", "error.field.displayImage.required");
        }

        if (detailsImages.size() > 20) {
            bindingResult.rejectValue("detailsImages", "error.field.detailsImages.imageCountExceeded");
        }
    }
}

