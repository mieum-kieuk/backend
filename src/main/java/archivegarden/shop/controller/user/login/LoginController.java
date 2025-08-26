package archivegarden.shop.controller.user.login;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import static archivegarden.shop.constant.SessionConstants.MEMBER_LOGIN_ERROR;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            String errorMessage = (String) session.getAttribute(MEMBER_LOGIN_ERROR);

            if (errorMessage != null) {
                model.addAttribute("error", errorMessage);
                session.removeAttribute(MEMBER_LOGIN_ERROR);
            }
        }

        return "login";
    }
}
