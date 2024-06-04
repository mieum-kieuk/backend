package archivegarden.shop.controller.admin.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminLoginController {

    @GetMapping("/admin/login")
    public String login(@RequestParam(name = "error", required = false) boolean error,
                        @RequestParam(name = "exception", required = false) String errorMessage,
                        Model model) {

        model.addAttribute("error", error);
        model.addAttribute("errorMessage", errorMessage);

        return "admin/login";
    }
}
