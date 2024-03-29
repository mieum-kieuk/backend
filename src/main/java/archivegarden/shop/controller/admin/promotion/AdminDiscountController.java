package archivegarden.shop.controller.admin.promotion;

import archivegarden.shop.dto.admin.promotion.AddDiscountForm;
import archivegarden.shop.dto.admin.promotion.DiscountDto;
import archivegarden.shop.dto.admin.promotion.EditDiscountForm;
import archivegarden.shop.entity.DiscountType;
import archivegarden.shop.service.admin.promotion.discount.AdminDiscountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/promotion/discounts")
public class AdminDiscountController {

    private final AdminDiscountService discountService;

    @ModelAttribute("discountTypes")
    public DiscountType[] discountTypes() {
        return DiscountType.values();
    }

    @GetMapping
    public String discountList(Model model) {
        List<DiscountDto> discounts = discountService.getDiscountList();
        model.addAttribute("discounts", discounts);
        return "admin/promotion/discount/discount_list";
    }

    @GetMapping("/add")
    public String addDiscountForm(@ModelAttribute("discount") AddDiscountForm form) {
        return "admin/promotion/discount/add_discount";
    }

    @PostMapping("/add")
    public String addDiscount(@Valid @ModelAttribute("discount") AddDiscountForm form, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        // value 범위 검증
        validateDiscountValueRange(form.getType(), form.getValue(), bindingResult);

        if(bindingResult.hasErrors()) {
            return "admin/promotion/discount/add_discount";
        }

        Long discountId = discountService.registerDiscount(form);
        redirectAttributes.addAttribute("discountId", discountId);
        return "redirect:/admin/promotion/discounts/{discountId}";
    }

    @GetMapping("/{discountId}")
    public String discountDetails(@PathVariable("discountId") Long discountId, Model model) {
        DiscountDto discountDto = discountService.getDiscount(discountId);
        model.addAttribute("discount", discountDto);
        return "admin/promotion/discount/discount_details";
    }

    @GetMapping("/{discountId}/edit")
    public String editDiscountForm(@PathVariable("discountId") Long discountId, Model model) {
        DiscountDto discount = discountService.getDiscount(discountId);
        model.addAttribute("discount", discount);
        return "admin/promotion/discount/edit_discount";
    }

    @PostMapping("/{discountId}/edit")
    public String editDiscount(@PathVariable("discountId") Long discountId, @Valid @ModelAttribute("discount") EditDiscountForm form, BindingResult bindingResult) {

        // value 범위 검증
        validateDiscountValueRange(form.getType(), form.getValue(), bindingResult);

        if(bindingResult.hasErrors()) {
            return "admin/promotion/discount/edit_discount";
        }

        discountService.updateDiscount(discountId, form);
        return "redirect:/admin/promotion/discounts/{discountId}";
    }

    @GetMapping("/{discountId}/delete")
    public String deleteDiscount(@PathVariable("discountId") Long discountId) {
        discountService.deleteDiscount(discountId);
        return "redirect:/admin/promotion/discounts";
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/delete")
    public void deleteDiscountsByAjax(@RequestBody List<Long> discountIds) {
        discountService.deleteDiscounts(discountIds);
    }

    private void validateDiscountValueRange(DiscountType type, Integer value, BindingResult bindingResult) {
        if(type != null && value != null) {
            //정액 할인
            if(type.equals(DiscountType.FIX) && value < 1) {
                bindingResult.rejectValue("value", "invalid", "정액 할인의 경우, 1원 이상의 금액을 입력해 주세요.");
            }
            //정률 할인
            if(type.equals(DiscountType.RATE) && (value < 1 || value > 100)) {
                bindingResult.rejectValue("value", "invalid", "정률 할인의 경우, 1부터 100사이의 값을 입력해 주세요.");
            }
        }
    }
}
