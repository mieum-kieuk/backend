package archivegarden.shop.controller.admin.admin;

import archivegarden.shop.dto.admin.member.AddAdminForm;
import archivegarden.shop.dto.member.NewMemberInfo;
import archivegarden.shop.service.admin.member.AdminAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/admins")
@RequiredArgsConstructor
public class AdminAdminController {

    private final AdminAdminService adminService;

    @GetMapping("/join")
    public String addAdminForm(@ModelAttribute("form") AddAdminForm form) {
        return "admin/admins/join";
    }

    @PostMapping("/join")
    public String join(@Valid @ModelAttribute("form") AddAdminForm form, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        //비밀번호 == 비밀번호 확인 검증
        if(StringUtils.hasText(form.getPassword())) {
            if(!form.getPassword().equals(form.getPasswordConfirm())) {
                bindingResult.rejectValue("passwordConfirm", "passwordNotMatch");
            }
        }

        if(bindingResult.hasErrors()) {
            return "admin/admins/join";
        }

        Integer adminId = adminService.join(form);
        redirectAttributes.addFlashAttribute("adminId", adminId);
        return "redirect:/admin/admins/join/complete";
    }

    @GetMapping("/join/complete")
    public String joinComplete(@ModelAttribute(name = "adminId") Integer adminId, Model model) {
        NewMemberInfo newAdminInfo = adminService.getNewAdminInfo(adminId);
        model.addAttribute("admin", newAdminInfo);
        return "admin/admins/join_complete";
    }

    @ResponseBody
    @PostMapping("/verification/loginId")
    public boolean checkLoginId(@RequestParam(name = "loginId") String loginId) {
        return adminService.isAvailableLoginId(loginId);
    }

    @ResponseBody
    @PostMapping("/verification/email")
    public boolean checkEmail(@RequestParam(name = "email") String email) {
        return adminService.isAvailableEmail(email);
    }
}
