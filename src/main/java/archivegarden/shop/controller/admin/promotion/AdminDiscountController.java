package archivegarden.shop.controller.admin.promotion;

import archivegarden.shop.dto.admin.discount.AddDiscountForm;
import archivegarden.shop.entity.DiscountType;
import archivegarden.shop.service.admin.promotion.discount.AdminDiscountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/promotion/discount")
public class AdminDiscountController {

    private final AdminDiscountService discountService;

    @ModelAttribute("discountTypes")
    public DiscountType[] discountTypes() {
        return DiscountType.values();
    }

    @GetMapping("/add")
    public String addDiscountForm(@ModelAttribute("form") AddDiscountForm form) {
        return "admin/promotion/discount/add_discount";
    }

    @PostMapping("/add")
    public String addDiscount(@Valid @ModelAttribute("form") AddDiscountForm form, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        // value 범위 검증
        if(form.getType() != null && form.getValue() != null) {
            //정액 할인
            if(form.getType().equals(DiscountType.FIX) && form.getValue() < 1) {
                bindingResult.rejectValue("value", "invalid", "1 이상의 값을 입력해 주세요.");
            }
            //정률 할인
            if(form.getType().equals(DiscountType.RATE) && (form.getValue() < 1 || form.getValue() > 100)) {
                bindingResult.rejectValue("value", "invalid", "1부터 100사이의 값을 입력해 주세요.");
            }
        }

        if(bindingResult.hasErrors()) {
            return "admin/promotion/discount/add_discount";
        }

        Long discountId = discountService.registerDiscount(form);
        redirectAttributes.addAttribute("discountId", discountId);
        return "redirect:/admin/promotion/discount/{discountId}";
    }

    @GetMapping("/{discountId}")
    public String discountDetails(@PathVariable("discountId") Long discountId) {
        return "admin/promotion/discount_details";
    }
}
