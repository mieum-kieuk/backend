//package archivegarden.shop.controller;
//
//import archivegarden.shop.dto.community.inquiry.AddProductInquiryForm;
//import archivegarden.shop.dto.community.inquiry.EditProductInquiryForm;
//import archivegarden.shop.dto.community.inquiry.ProductInquiryDetailsDto;
//import archivegarden.shop.dto.community.inquiry.ProductInquiryListDto;
//import archivegarden.shop.entity.Member;
//import archivegarden.shop.service.community.ProductInquiryService;
//import archivegarden.shop.web.annotation.CurrentUser;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//@Controller
//@RequestMapping("/community/inquiry")
//@RequiredArgsConstructor
//public class ProductInquiryController {
//
//    private final ProductInquiryService productInquiryService;
//
//    @GetMapping
//    public String inquires(@RequestParam(name = "page", defaultValue = "1") int page, Model model) {
//        PageRequest pageRequest = PageRequest.of(page - 1, 10);
//        Page<ProductInquiryListDto> inquiryDtos = productInquiryService.getInquires(pageRequest);
//        model.addAttribute("inquiries", inquiryDtos);
//        return "community/inquiry/inquiry_list";
//    }
//
//    @GetMapping("/add")
//    @PreAuthorize("hasRole('ROLE_USER')")
//    public String addInquiryForm(@ModelAttribute("form") AddProductInquiryForm form) {
//        return "community/inquiry/add_inquiry";
//    }
//
//    @PostMapping("/add")
//    @PreAuthorize("hasRole('ROLE_USER')")
//    public String addInquiry(@Valid @ModelAttribute("form") AddProductInquiryForm form, BindingResult bindingResult,
//                         @CurrentUser Member loginMember, RedirectAttributes redirectAttributes) {
//
//        if (bindingResult.hasErrors()) {
//            return "community/inquiry/add_inquiry";
//        }
//
//        Long inquiryId = productInquiryService.saveInquiry(form, loginMember);
//        redirectAttributes.addAttribute("inquiryId", inquiryId);
//        return "redirect:/community/inquiry/{inquiryId}";
//    }
//
//    @GetMapping("/{inquiryId}")
//    public String inquiry(@PathVariable("inquiryId") Long inquiryId, Model model) {
//        ProductInquiryDetailsDto inquiryDetailsDto = productInquiryService.getInquiry(inquiryId);
//        model.addAttribute("inquiry", inquiryDetailsDto);
//        return "community/inquiry/inquiry_details";
//    }
//
//    @GetMapping("/{inquiryId}/edit")
//    @PreAuthorize("hasRole('ROLE_USER') and #loginMember.loginId == principal.username")
//    public String editInquiryForm(@PathVariable("inquiryId") Long inquiryId, @CurrentUser Member loginMember, Model model) {
//        EditProductInquiryForm form = productInquiryService.getEditForm(inquiryId);
//        model.addAttribute("form", form);
//        return "community/inquiry/edit_inquiry";
//    }
//
//    @PostMapping("/{inquiryId}/edit")
//    @PreAuthorize("hasRole('ROLE_USER') and #loginMember.loginId == principal.username")
//    public String editInquiry(@Valid @ModelAttribute("form") EditProductInquiryForm form, BindingResult bindingResult,
//                              @PathVariable("inquiryId") Long inquiryId, @CurrentUser Member loginMember) {
//
//        if(bindingResult.hasErrors()) {
//            return "community/inquiry/edit_inquiry";
//        }
//
//        productInquiryService.editInquiry(form, inquiryId);
//        return "redirect:/community/inquiry/{inquiryId}";
//    }
//
//    @GetMapping("/{inquiryId}/delete")
//    @PreAuthorize("hasRole('ROLE_USER') and #loginMember.loginId == principal.username")
//    public String deleteQna(@PathVariable("inquiryId") Long inquiryId, @CurrentUser Member loginMember) {
//        productInquiryService.deleteInquiry(inquiryId);
//        return "redirect:/community/inquiry";
//    }
//}
