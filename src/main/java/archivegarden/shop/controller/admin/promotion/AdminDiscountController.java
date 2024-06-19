package archivegarden.shop.controller.admin.promotion;

import archivegarden.shop.dto.admin.AdminSearchForm;
import archivegarden.shop.dto.admin.product.discount.AddDiscountForm;
import archivegarden.shop.dto.admin.product.discount.DiscountDto;
import archivegarden.shop.dto.admin.product.discount.EditDiscountForm;
import archivegarden.shop.service.admin.promotion.discount.AdminDiscountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/discounts")
public class AdminDiscountController {

    private final AdminDiscountService discountService;

    //상품 할인 등록 폼
    @GetMapping("/add")
    public String addDiscountForm(@ModelAttribute("form") AddDiscountForm form) {
        return "admin/product/discounts/add_discount";
    }

    //상품 할인 등록
    @PostMapping("/add")
    public String addDiscount(@Valid @ModelAttribute("form") AddDiscountForm form, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        //할인 기간 검증
        validateDateTime(form.getStartedAt(), form.getExpiredAt(), bindingResult);

        if (bindingResult.hasErrors()) {
            return "admin/product/discounts/add_discount";
        }

        Long discountId = discountService.saveDiscount(form);
        redirectAttributes.addAttribute("discountId", discountId);
        return "redirect:/admin/discounts/{discountId}";
    }

    //상품 할인 단건 조회
    @GetMapping("/{discountId}")
    public String discountDetails(@PathVariable("discountId") Long discountId, Model model) {
        DiscountDto discountDto = discountService.getDiscount(discountId);
        model.addAttribute("discount", discountDto);
        return "admin/product/discounts/discount_details";
    }

    //상품 할인 목록 조회
    @GetMapping
    public String discounts(@ModelAttribute("form") AdminSearchForm form, @RequestParam(name = "page", defaultValue = "1") int page, Model model) {

        String errorMessage = validateDiscountPercent(form.getSearchKey(), form.getKeyword());
        if(errorMessage != null) {
            model.addAttribute("error", errorMessage);
            return "admin/product/discounts/discount_list";
        }

        PageRequest pageRequest = PageRequest.of(page - 1, 10);
        Page<DiscountDto> discountDtos = discountService.getDiscountList(form, pageRequest);
        model.addAttribute("discounts", discountDtos);
        return "admin/product/discounts/discount_list";
    }

    //상품 할인 수정 폼
    @GetMapping("/{discountId}/edit")
    public String editDiscountForm(@PathVariable("discountId") Long discountId, Model model) {
        EditDiscountForm discountForm = discountService.getEditDiscountForm(discountId);
        model.addAttribute("form", discountForm);
        return "admin/product/discounts/edit_discount";
    }

    //상품 할인 수정
    @PostMapping("/{discountId}/edit")
    public String editDiscount(@PathVariable("discountId") Long discountId, @Valid @ModelAttribute("form") EditDiscountForm form, BindingResult bindingResult) {

        //할인 기간 검증
        validateDateTime(form.getStartedAt(), form.getExpiredAt(), bindingResult);

        if(bindingResult.hasErrors()) {
            return "admin/product/discounts/edit_discount";
        }

        discountService.updateDiscount(discountId, form);
        return "redirect:/admin/discounts/{discountId}";
    }

    private void validateDateTime(LocalDateTime createdAt, LocalDateTime expiredAt, BindingResult bindingResult) {
        //시작일시 < 종료일시
        if (createdAt != null && expiredAt != null) {
            if(expiredAt.isBefore(createdAt)) {
                bindingResult.rejectValue("expiredAt", "periodInvalid");
            }
        }
    }

    private String validateDiscountPercent(String searchKey, String keyword) {
        if(StringUtils.hasText(searchKey) && searchKey.equals("percent")) {
            if(!keyword.matches("^(?:1|[1-9]\\d?|100)$")) {
                return "할인율으로 검색할 경우 입력칸에 숫자만 입력해 주세요.";
            }
        }

        return null;
    }
}
