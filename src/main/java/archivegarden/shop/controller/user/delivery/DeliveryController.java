package archivegarden.shop.controller.user.delivery;

import archivegarden.shop.dto.delivery.*;
import archivegarden.shop.entity.Member;
import archivegarden.shop.service.mypage.DeliveryService;
import archivegarden.shop.web.annotation.CurrentUser;
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

@Controller
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    /**
     * 배송지 등록 폼을 반환하는 메서드
     */
    @GetMapping("/mypage/delivery/add")
    public String addDeliveryForm(@ModelAttribute("form") AddDeliveryForm form) {
        return "user/mypage/delivery/add_delivery";
    }

    /**
     * 배송지 등록 요청을 처리하는 메서드
     */
    @PostMapping("/mypage/delivery/add")
    @PreAuthorize("#loginMember.loginId == principal.username")
    public String addAddress(@Valid @ModelAttribute("form") AddDeliveryForm form, BindingResult bindingResult, @CurrentUser Member loginMember) {
        validateAddress(form.getZipCode(), form.getBasicAddress(), bindingResult);
        validatePhonenumber(form.getPhonenumber1(), form.getPhonenumber2(), form.getPhonenumber3(), bindingResult);
        if (bindingResult.hasErrors()) {
            return "user/mypage/delivery/add_delivery";
        }

        deliveryService.saveDelivery(form, loginMember.getId());
        return "redirect:/mypage/delivery";
    }

    /**
     * 배송지 목록을 조회하는 요청을 처리하는 메서드
     */
    @GetMapping("/mypage/delivery")
    public String deliveries(@CurrentUser Member loginMember, Model model) {
        List<DeliveryListDto> deliveries = deliveryService.getDeliveries(loginMember.getId());
        model.addAttribute("deliveries", deliveries);
        return "user/mypage/delivery/delivery_list";
    }

    /**
     * 배송지 수정 폼을 반환하는 메서드
     */
    @GetMapping("/mypage/delivery/{deliveryId}/edit")
    @PreAuthorize("#loginMember.loginId == principal.username")
    public String editAddressForm(@PathVariable("deliveryId") Long deliveryId, @CurrentUser Member loginMember, Model model) {
        EditDeliveryForm form = deliveryService.getEditDeliveryForm(deliveryId);
        model.addAttribute("form", form);
        return "user/mypage/delivery/edit_delivery";
    }

    /**
     * 배송지 수정 요청을 처리하는 메서드
     */
    @PostMapping("/mypage/delivery/{deliverId}/edit")
    @PreAuthorize("#loginMember.loginId == principal.username")
    public String editDelivery(@Valid @ModelAttribute("form") EditDeliveryForm form, BindingResult bindingResult,
                               @PathVariable("deliverId") Long deliverId, @CurrentUser Member loginMember) {
        validateAddress(form.getZipCode(), form.getBasicAddress(), bindingResult);
        validatePhonenumber(form.getPhonenumber1(), form.getPhonenumber2(), form.getPhonenumber3(), bindingResult);

        if (bindingResult.hasErrors()) {
            return "user/mypage/delivery/edit_delivery";
        }

        deliveryService.editDelivery(form, deliverId, loginMember.getId());
        return "redirect:/mypage/delivery";
    }

    /**
     * 주문서 페이지에서 이전 배송지 목록을 조회하는 요청을 처리하는 메서드
     */
    @GetMapping("/popup/deliveries")
    public String popupDeliveries(@CurrentUser Member loginMember, Model model) {
        List<DeliveryPopupDto> deliveries = deliveryService.getDeliveriesInPopup(loginMember.getId());
        model.addAttribute("deliveries", deliveries);
        return "user/order/checkout_delivery_popup";
    }

    /**
     * 주문서 페이지에서 배송지 수정 폼을 반환하는 메서드
     */
    @GetMapping("/popup/deliveries/{deliveryId}/edit")
    public String popupDeliveryEditForm(@PathVariable("deliveryId") Long deliveryId, Model model) {
        EditPopupDeliveryForm editDeliveryForm = deliveryService.getEditPopupDeliveryForm(deliveryId);
        model.addAttribute("form", editDeliveryForm);
        return "user/order/checkout_edit_delivery";
    }

    /**
     * 주문서 페이지에서 배송지 배송지 수정 요청을 처리하는 메서드
     */
    @PostMapping("/popup/deliveries/{deliveryId}/edit")
    public String popupDeliveryEdit(@ModelAttribute("form") EditPopupDeliveryForm form, @PathVariable("deliveryId") Long deliveryId) {
        deliveryService.editPopupDelivery(form, deliveryId);
        return "redirect:/popup/deliveries";
    }

    /**
     * 주소 검증을 수행하는 메서드
     */
    private void validateAddress(String zipCode, String basicAddress, BindingResult bindingResult) {
        if (StringUtils.hasText(zipCode) && StringUtils.hasText(basicAddress)) {
            if (!Pattern.matches("^[\\d]{5}$", zipCode) || !Pattern.matches("^[가-힣\\d\\W]{1,40}$", basicAddress)) {
                bindingResult.rejectValue("zipCode", "addressInvalid");
            }
        } else {
            bindingResult.rejectValue("zipCode", "requiredAddress", "주소를 입력해 주세요.");
        }
    }

    /**
     * 휴대전화번호 검증을 수행하는 메서드
     */
    private void validatePhonenumber(String phonenumber1,String phonenumber2, String phonenumber3, BindingResult bindingResult) {
        if (StringUtils.hasText(phonenumber1) && StringUtils.hasText(phonenumber2) && StringUtils.hasText(phonenumber3)) {
            if (!Pattern.matches("^01(0|1|[6-9])$", phonenumber1) || !Pattern.matches("^[\\d]{3,4}$", phonenumber2) || !Pattern.matches("^[\\d]{4}$", phonenumber3)) {
                bindingResult.rejectValue("phonenumber1", "phonenumberInvalid");
            }
        } else {
            bindingResult.rejectValue("phonenumber1", "requiredPhonenumber", "휴대전화번호를 입력해 주세요.");
        }
    }
}
