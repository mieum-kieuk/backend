package archivegarden.shop.controller.admin.product.discount;

import archivegarden.shop.dto.admin.AdminSearchCondition;
import archivegarden.shop.dto.admin.product.discount.AdminAddDiscountForm;
import archivegarden.shop.dto.admin.product.discount.AdminDiscountDetailsDto;
import archivegarden.shop.dto.admin.product.discount.AdminDiscountListDto;
import archivegarden.shop.dto.admin.product.discount.AdminEditDiscountForm;
import archivegarden.shop.service.admin.product.AdminDiscountService;
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

    /**
     * 할인 등록 폼을 반환하는 메서드
     */
    @GetMapping("/add")
    public String addDiscountForm(@ModelAttribute("form") AdminAddDiscountForm form) {
        return "admin/product/discount/add_discount";
    }

    /**
     * 할인 등록 요청을 처리하는 메서드
     */
    @PostMapping("/add")
    public String addDiscount(@Valid @ModelAttribute("form") AdminAddDiscountForm form, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        validateDateTime(form.getStartDateTime(), form.getExpireDateTime(), bindingResult);
        if (bindingResult.hasErrors()) {
            return "admin/product/discount/add_discount";
        }

        Long discountId = discountService.saveDiscount(form);
        redirectAttributes.addAttribute("discountId", discountId);
        return "redirect:/admin/discounts/{discountId}";
    }

    /**
     * 할인 상세 페이지를 조회하는 요청을 처리하는 메서드
     */
    @GetMapping("/{discountId}")
    public String discountDetails(@PathVariable("discountId") Long discountId, Model model) {
        AdminDiscountDetailsDto discountDetailsDto = discountService.getDiscount(discountId);
        model.addAttribute("discount", discountDetailsDto);
        return "admin/product/discount/discount_details";
    }

    /**
     * 할인 목록을 조회하는 요청을 처리하는 메서드
     */
    @GetMapping
    public String discounts(@ModelAttribute("form") AdminSearchCondition condition, @RequestParam(name = "page", defaultValue = "1") int page, Model model) {
        String errorMessage = validateDiscountPercent(condition.getSearchKey(), condition.getKeyword());
        if (errorMessage != null) {
            model.addAttribute("error", errorMessage);
            return "admin/product/discount/discount_list";
        }

        PageRequest pageRequest = PageRequest.of(page - 1, 10);
        Page<AdminDiscountListDto> discountDtos = discountService.getDiscountList(condition, pageRequest);
        model.addAttribute("discounts", discountDtos);
        return "admin/product/discount/discount_list";
    }

    /**
     * 할인 수정 폼을 반환하는 메서드
     */
    @GetMapping("/{discountId}/edit")
    public String editDiscountForm(@PathVariable("discountId") Long discountId, Model model) {
        AdminEditDiscountForm discountForm = discountService.getEditDiscountForm(discountId);
        model.addAttribute("form", discountForm);
        return "admin/product/discount/edit_discount";
    }

    /**
     * 할인 수정 요청을 처리하는 메서드
     */
    @PostMapping("/{discountId}/edit")
    public String editDiscount(@PathVariable("discountId") Long discountId, @Valid @ModelAttribute("form") AdminEditDiscountForm form, BindingResult bindingResult) {
        validateDateTime(form.getStartDateTime(), form.getExpireDateTime(), bindingResult);
        if (bindingResult.hasErrors()) {
            return "admin/product/discount/edit_discount";
        }

        discountService.updateDiscount(discountId, form);
        return "redirect:/admin/discounts/{discountId}";
    }

    /**
     * 상품 할인 등록 폼의 복합적인 유효성 검증을 수행하는 메서드
     * - 시작일시, 종료일시: 시작일시가 종료일시보다 이전인지 검사합니다.
     */
    private void validateDateTime(LocalDateTime startedAt, LocalDateTime expiredAt, BindingResult bindingResult) {
        if (startedAt != null && expiredAt != null) {
            if (expiredAt.isBefore(startedAt)) {
                bindingResult.rejectValue("expiredAt", "periodInvalid");
            }
        }
    }

    private String validateDiscountPercent(String searchKey, String keyword) {
        if (StringUtils.hasText(searchKey) && searchKey.equals("percent")) {
            if (!keyword.matches("^(?:1|[1-9]\\d?|100)$")) {
                return "할인율으로 검색할 경우 입력칸에 숫자만 입력해 주세요.";
            }
        }

        return null;
    }
}
