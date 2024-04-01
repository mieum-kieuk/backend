package archivegarden.shop.controller.admin.promotion;

import archivegarden.shop.dto.admin.promotion.AddDiscountForm;
import archivegarden.shop.dto.admin.promotion.DiscountDetailsDto;
import archivegarden.shop.dto.admin.promotion.EditDiscountForm;
import archivegarden.shop.service.admin.promotion.discount.AdminDiscountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/promotion/discounts")
public class AdminDiscountController {

    private final AdminDiscountService discountService;

    @GetMapping
    public String discounts(@PageableDefault(size = 15, sort = "id") Pageable pageable, Model model) {
        Page<DiscountDetailsDto> discountDtos = discountService.getDiscountList(pageable);
        model.addAttribute("discounts", discountDtos);
        return "admin/promotion/discount/discount_list";
    }

    @GetMapping("/add")
    public String addDiscountForm(@ModelAttribute("discount") AddDiscountForm form) {
        return "admin/promotion/discount/add_discount";
    }

    @PostMapping("/add")
    public String addDiscount(@Valid @ModelAttribute("discount") AddDiscountForm form, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        //할인 기간 검증
        validateDiscountPeriod(form.getStartDatetime(), form.getEndDatetime(), bindingResult);

        if (bindingResult.hasErrors()) {
            return "admin/promotion/discount/add_discount";
        }

        Long discountId = discountService.saveDiscount(form);
        redirectAttributes.addAttribute("discountId", discountId);
        return "redirect:/admin/promotion/discounts/{discountId}";
    }

    @GetMapping("/{discountId}")
    public String discountDetails(@PathVariable("discountId") Long discountId, Model model) {
        DiscountDetailsDto discountDto = discountService.getDiscount(discountId);
        model.addAttribute("discount", discountDto);
        return "admin/promotion/discount/discount_details";
    }

    @GetMapping("/{discountId}/edit")
    public String editDiscountForm(@PathVariable("discountId") Long discountId, Model model) {
        EditDiscountForm discountForm = discountService.getEditDiscountForm(discountId);
        model.addAttribute("discount", discountForm);
        return "admin/promotion/discount/edit_discount";
    }

    @PostMapping("/{discountId}/edit")
    public String editDiscount(@PathVariable("discountId") Long discountId, @Valid @ModelAttribute("discount") EditDiscountForm form, BindingResult bindingResult) {

        //할인 기간 검증
        validateDiscountPeriod(form.getStartDatetime(), form.getEndDatetime(), bindingResult);

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

    private void validateDiscountPeriod(LocalDateTime startDatetime, LocalDateTime endDatetime, BindingResult bindingResult) {
        //시작일시 < 종료일시
        if (startDatetime != null && endDatetime != null) {
            if (!endDatetime.isAfter(startDatetime)) {
                bindingResult.rejectValue("endDatetime", "periodInvalid");
            }
        }
    }
}
