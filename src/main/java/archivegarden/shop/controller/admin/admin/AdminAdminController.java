package archivegarden.shop.controller.admin.admin;

import archivegarden.shop.dto.admin.admin.AdminListDto;
import archivegarden.shop.dto.admin.admin.AdminSearchForm;
import archivegarden.shop.service.admin.admins.AdminAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/admins")
@RequiredArgsConstructor
public class AdminAdminController {

    private final AdminAdminService adminService;

    @GetMapping
    public String admins(@RequestParam(name = "page", defaultValue = "1") int page, @ModelAttribute("form") AdminSearchForm form, Model model) {
        PageRequest pageRequest = PageRequest.of(page - 1, 10);
        Page<AdminListDto> adminListDtos = adminService.getAdmins(form, pageRequest);
        model.addAttribute("admins", adminListDtos);
        return "admin/admins/admin_list";
    }
}
