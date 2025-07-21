package archivegarden.shop.controller.user.mypage;

import archivegarden.shop.dto.delivery.AddDeliveryForm;
import archivegarden.shop.dto.delivery.DeliveryListDto;
import archivegarden.shop.dto.delivery.EditDeliveryForm;
import archivegarden.shop.entity.Member;
import archivegarden.shop.service.mypage.DeliveryService;
import archivegarden.shop.web.annotation.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.regex.Pattern;

@Tag(name = "배송지", description = "사용자 페이지에서 마이페이지 배송지 관련 API")
@Controller
@RequestMapping("/mypage/deliveries")
@RequiredArgsConstructor
public class MyDeliveryController {

    private final DeliveryService deliveryService;

    @Operation(
            summary = "배송지 등록 폼 표시",
            description = "배송지 등록을 위한 화면을 반환합니다."
    )
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/add")
    public String addDeliveryForm(@ModelAttribute("addForm") AddDeliveryForm form) {
        return "user/mypage/delivery/add_delivery";
    }

    @Operation(
            summary = "배송지 등록 요청",
            description = "배송지를 등록하고 배송지 목록 페이지로 리다이렉트합니다."
    )
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/add")
    public String addAddress(
            @Valid @ModelAttribute("addForm") AddDeliveryForm form,
            BindingResult bindingResult,
            @CurrentUser Member loginMember
    ) {
        validateAddress(form.getZipCode(), form.getBasicAddress(), bindingResult);
        validatePhonenumber(form.getPhonenumber1(), form.getPhonenumber2(), form.getPhonenumber3(), bindingResult);
        if (bindingResult.hasErrors()) {
            return "user/mypage/delivery/add_delivery";
        }

        deliveryService.saveDelivery(form, loginMember.getId());
        return "redirect:/mypage/deliveries";
    }

    @Operation(
            summary = "내 배송지 목록 조회",
            description = "내 배송지 목록을 조회합니다"
    )
    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public String deliveries(@CurrentUser Member loginMember, Model model) {
        List<DeliveryListDto> deliveries = deliveryService.getDeliveries(loginMember.getId());
        model.addAttribute("deliveries", deliveries);
        return "user/mypage/delivery/delivery_list";
    }

    @Operation(
            summary = "배송지 수정 폼 표시",
            description = "배송지를 수정하기 위한 화면을 반환합니다."
    )
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{deliveryId}/edit")
    public String editAddressForm(@PathVariable("deliveryId") Long deliveryId, Model model) {
        EditDeliveryForm form = deliveryService.getEditDeliveryForm(deliveryId);
        model.addAttribute("editForm", form);
        return "user/mypage/delivery/edit_delivery";
    }

    @Operation(
            summary = "배송지 수정 요청",
            description = "배송지를 수정하고 배송지 목록 페이지로 리다이렉트합니다."
    )
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{deliveryId}/edit")
    public String editDelivery(
            @Valid @ModelAttribute("editForm") EditDeliveryForm form,
            BindingResult bindingResult,
            @PathVariable("deliveryId") Long deliveryId,
            @CurrentUser Member loginMember
    ) {
        validateAddress(form.getZipCode(), form.getBasicAddress(), bindingResult);
        validatePhonenumber(form.getPhonenumber1(), form.getPhonenumber2(), form.getPhonenumber3(), bindingResult);

        if (bindingResult.hasErrors()) {
            return "user/mypage/delivery/edit_delivery";
        }

        deliveryService.editDelivery(form, deliveryId, loginMember.getId());
        return "redirect:/mypage/deliveries";
    }

    /**
     * 주소 유효성 검증
     *
     * 우편번호와 기본주소가 모두 입력되었는지, 형식이 올바른지 검사합니다.
     */
    private void validateAddress(String zipCode, String basicAddress, BindingResult bindingResult) {
        if (StringUtils.hasText(zipCode) && StringUtils.hasText(basicAddress)) {
            if (!Pattern.matches("^[\\d]{5}$", zipCode) || !Pattern.matches("^[가-힣\\d\\W]{1,40}$", basicAddress)) {
                bindingResult.rejectValue("zipCode", "error.field.address.invalidFormat");
            }
        } else {
            bindingResult.rejectValue("zipCode", "error.field.address.required");
        }
    }

    /**
     * 휴대전화번호 유효성 검증
     *
     * 휴대전화번호 각 필드가 입력되었는지, 형식이 올바른지 검사합니다.
     */
    private void validatePhonenumber(String phonenumber1,String phonenumber2, String phonenumber3, BindingResult bindingResult) {
        if (StringUtils.hasText(phonenumber1) && StringUtils.hasText(phonenumber2) && StringUtils.hasText(phonenumber3)) {
            if (!Pattern.matches("^01(0|1|[6-9])$", phonenumber1) || !Pattern.matches("^[\\d]{3,4}$", phonenumber2) || !Pattern.matches("^[\\d]{4}$", phonenumber3)) {
                bindingResult.rejectValue("phonenumber1", "error.field.phonenumber.invalidFormat");
            }
        } else {
            bindingResult.rejectValue("phonenumber1", "error.field.phonenumber.required");
        }
    }
}
