package archivegarden.shop.controller;

import archivegarden.shop.dto.delivery.AddDeliveryForm;
import archivegarden.shop.dto.delivery.DeliveryListDto;
import archivegarden.shop.dto.delivery.EditDeliveryForm;
import archivegarden.shop.dto.delivery.EditPopupDeliveryForm;
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

    @GetMapping("/mypage/delivery")
    @PreAuthorize("hasRole('ROLE_USER')")
    public String deliveries(@CurrentUser Member loginMember, Model model) {
        List<DeliveryListDto> deliveries = deliveryService.getDeliveries(loginMember.getId());
        model.addAttribute("deliveries", deliveries);
        return "mypage/delivery/delivery_list";
    }

    @GetMapping("/mypage/delivery/add")
    @PreAuthorize("hasRole('ROLE_USER')")
    public String addDeliveryForm(@ModelAttribute("delivery") AddDeliveryForm form) {
        return "mypage/delivery/add_delivery";
    }

    @PostMapping("/mypage/delivery/add")
    @PreAuthorize("hasRole('ROLE_USER') and #loginMember.loginId == principal.username")
    public String addAddress(@Valid @ModelAttribute("delivery") AddDeliveryForm form, BindingResult bindingResult, @CurrentUser Member loginMember) {

        validateAddress(form.getZipCode(), form.getBasicAddress(), bindingResult);
        validatePhonenumber(form.getPhonenumber1(), form.getPhonenumber2(), form.getPhonenumber3(), bindingResult);

        if (bindingResult.hasErrors()) {
            return "mypage/delivery/add_delivery";
        }

        deliveryService.saveDelivery(form, loginMember.getId());
        return "redirect:/mypage/delivery";
    }

    @GetMapping("/mypage/delivery/{deliveryId}/edit")
    @PreAuthorize("hasRole('ROLE_USER') and #loginMember.loginId == principal.username")
    public String editAddressForm(@PathVariable("deliveryId") Long deliveryId, @CurrentUser Member loginMember, Model model) {
        EditDeliveryForm form = deliveryService.getEditDeliveryForm(deliveryId);
        model.addAttribute("delivery", form);
        return "mypage/delivery/edit_delivery";
    }

    @PostMapping("/mypage/delivery/{deliverId}/edit")
    @PreAuthorize("hasRole('ROLE_USER') and #loginMember.loginId == principal.username")
    public String editDelivery(@Valid @ModelAttribute("delivery") EditDeliveryForm form, BindingResult bindingResult,
                              @PathVariable("deliverId") Long deliverId, @CurrentUser Member loginMember) {

        validateAddress(form.getZipCode(), form.getBasicAddress(), bindingResult);
        validatePhonenumber(form.getPhonenumber1(), form.getPhonenumber2(), form.getPhonenumber3(), bindingResult);

        if (bindingResult.hasErrors()) {
            return "mypage/delivery/edit_delivery";
        }

        deliveryService.editDelivery(form, deliverId);
        return "redirect:/mypage/delivery";
    }

    @GetMapping("/mypage/delivery/{deliveryId}/delete")
    @PreAuthorize("hasRole('ROLE_USER') and #loginMember.loginId == principal.username")
    public String deleteAddress(@PathVariable("deliveryId") Long deliveryId, @CurrentUser Member loginMember) {
        deliveryService.deleteDelivery(deliveryId);
        return "redirect:/mypage/delivery";
    }

    @GetMapping("/popup/deliveries")
    public String checkoutDeliveries(@CurrentUser Member loginMember, Model model) {
        List<DeliveryListDto> deliveries = deliveryService.getDeliveries(loginMember.getId());
        model.addAttribute("deliveries", deliveries);
        return "order/checkout_delivery_popup";
    }

    @GetMapping("/popup/deliveries/{deliveryId}/edit")
    public String checkoutDeliveryEditForm(@PathVariable("deliveryId") Long deliveryId, Model model) {
        EditPopupDeliveryForm editDeliveryForm = deliveryService.getEditPopupDeliveryForm(deliveryId);
        model.addAttribute("form", editDeliveryForm);
        return "order/checkout_edit_delivery";
    }

    @PostMapping("/popup/deliveries/{deliveryId}/edit")
    public String checkoutDeliveryEdit(@ModelAttribute("form") EditPopupDeliveryForm form, @PathVariable("deliveryId") Long deliveryId) {
        deliveryService.editPopupDelivery(form, deliveryId);
        return "redirect:/popup/deliveries";
    }

    private void validateAddress(String zipCode, String basicAddress, BindingResult bindingResult) {
        //주소 검증
        if (StringUtils.hasText(zipCode) && StringUtils.hasText(basicAddress)) {
            if (!Pattern.matches("^[\\d]{5}$", zipCode) || !Pattern.matches("^[가-힣\\d\\W]{1,40}$", basicAddress)) {
                bindingResult.rejectValue("zipCode", "addressInvalid");
            }
        } else {
            bindingResult.rejectValue("zipCode", "requiredAddress", "주소를 입력해 주세요.");
        }
    }

    private void validatePhonenumber(String phonenumber1,String phonenumber2, String phonenumber3, BindingResult bindingResult) {
        //휴대전화번호 검증
        if (StringUtils.hasText(phonenumber1) && StringUtils.hasText(phonenumber2) && StringUtils.hasText(phonenumber3)) {
            if (!Pattern.matches("^01(0|1|[6-9])$", phonenumber1) || !Pattern.matches("^[\\d]{3,4}$", phonenumber2) || !Pattern.matches("^[\\d]{4}$", phonenumber3)) {
                bindingResult.rejectValue("phonenumber1", "phonenumberInvalid");
            }
        } else {
            bindingResult.rejectValue("phonenumber1", "requiredPhonenumber", "휴대전화번호를 입력해 주세요.");
        }
    }
}
