package archivegarden.shop.controller;

import archivegarden.shop.dto.mypage.address.AddAddressForm;
import archivegarden.shop.dto.mypage.address.AddressListDto;
import archivegarden.shop.dto.mypage.address.EditAddressForm;
import archivegarden.shop.entity.Member;
import archivegarden.shop.service.mypage.ShippingAddressService;
import archivegarden.shop.web.annotation.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/mypage/address")
@RequiredArgsConstructor
public class ShippingAddressController {

    private final ShippingAddressService shippingAddressService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public String address(@CurrentUser Member loginMember, Model model) {
        List<AddressListDto> addressDtos = shippingAddressService.getAddresses(loginMember);
        model.addAttribute("addresses", addressDtos);
        return "mypage/address/address_list";
    }

    @GetMapping("/add")
    @PreAuthorize("hasRole('ROLE_USER') and #loginMember.loginId == principal.username")
    public String addAddressForm(@ModelAttribute("address") AddAddressForm form, @CurrentUser Member loginMember) {
        return "mypage/address/add_address";
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_USER') and #loginMember.loginId == principal.username")
    public String addAddress(@Valid @ModelAttribute("address") AddAddressForm form, BindingResult bindingResult,
                             @CurrentUser Member loginMember) {

        //주소 검증
        if(form.getZipCode() == null || form.getBasicAddress() == null) {
            bindingResult.rejectValue("zipCode", "addressRequired", "우편번호와 기본주소를 입력해 주세요.");
        }

        //휴대전화번호 검증
        if (bindingResult.hasFieldErrors("phonenumber1") || bindingResult.hasFieldErrors("phonenumber2") || bindingResult.hasFieldErrors("phonenumber3")) {
            bindingResult.rejectValue("phonenumber1", "phonenumberInvalid");
        }

        if(bindingResult.hasErrors()) {
            return "mypage/address/add_address";
        }

        shippingAddressService.saveAddress(form, loginMember.getId());
        return "redirect:/mypage/address";
    }

    @GetMapping("/{addressId}/edit")
    @PreAuthorize("hasRole('ROLE_USER') and #loginMember.loginId == principal.username")
    public String editAddressForm(@PathVariable("addressId") Long addressId, @CurrentUser Member loginMember, Model model) {
        EditAddressForm form = shippingAddressService.getEditAddressForm(addressId);
        model.addAttribute("address", form);
        return "mypage/address/edit_address";
    }

    @PostMapping("/{addressId}/edit")
    @PreAuthorize("hasRole('ROLE_USER') and #loginMember.loginId == principal.username")
    public String editAddress(@Valid @ModelAttribute EditAddressForm form, BindingResult bindingResult,
                              @PathVariable("addressId") Long addressId, @CurrentUser Member loginMember) {
        if(bindingResult.hasErrors()) {
            return "mypage/address/edit_address";
        }

        shippingAddressService.editAddress(form, addressId, loginMember.getId());
        return "redirect:/mypage/address";
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/delete")
    @PreAuthorize("hasRole('ROLE_USER') and #loginMember.loginId == principal.username")
    public void deleteAddresses(@RequestBody List<Long> addressIds) {
        shippingAddressService.deleteAddresses(addressIds);
    }
}
