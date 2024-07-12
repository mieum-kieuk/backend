package archivegarden.shop.controller.admin.admin;

import archivegarden.shop.dto.ResultResponse;
import archivegarden.shop.dto.admin.admin.AddAdminForm;
import archivegarden.shop.dto.member.NewMemberInfo;
import archivegarden.shop.service.admin.admins.AdminJoinService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/join")
@RequiredArgsConstructor
public class AdminJoinController {

    private final AdminJoinService adminService;

    //관리자 회원가입 폼
    @GetMapping
    public String addAdminForm(@ModelAttribute("form") AddAdminForm form) {
        return "admin/admins/join";
    }

    //관리자 회원가입
    @PostMapping
    public String join(@Valid @ModelAttribute("form") AddAdminForm form, BindingResult bindingResult, HttpServletRequest request) {

        //비밀번호 == 비밀번호 확인 검증
        if(StringUtils.hasText(form.getPassword())) {
            if(!form.getPassword().equals(form.getPasswordConfirm())) {
                bindingResult.rejectValue("passwordConfirm", "passwordNotMatch");
            }
        }

        if(bindingResult.hasErrors()) {
            return "admin/admins/join";
        }

        //관리자 회원가입
        Long adminId = adminService.join(form);

        //회원가입한 관리자 아이디 세션에 저장
        HttpSession session = request.getSession();
        session.setAttribute("joinAdminId", adminId);
        return "redirect:/admin/join/complete";
    }

    //관리자 회원가입 완료
    @GetMapping("/complete")
    public String joinComplete(HttpServletRequest request, Model model) {

        //세션에서 adminId(관리자 아이디) 조회
        HttpSession session = request.getSession(false);
        Long adminId = (Long) session.getAttribute("joinAdminId");
        if(adminId == null) {
            return "redirect:/admin/join";
        }
        session.invalidate();

        NewMemberInfo newAdminInfo = adminService.getNewAdminInfo(adminId);
        model.addAttribute("admin", newAdminInfo);
        return "admin/admins/join_complete";
    }

    //관리자 로그인 아이디 중복 검사
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @PostMapping("/loginId/check")
    public ResultResponse checkLoginId(@RequestParam(name = "loginId") String loginId) {
        boolean isAvailableLoginId = adminService.isAvailableLoginId(loginId);
        if(isAvailableLoginId) {
            return new ResultResponse(HttpStatus.OK.value(), "사용 가능한 아이디입니다.");
        } else {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), "이미 사용 중인 아이디입니다.");
        }
    }

    //관리자 로그인 아이디 중복 검사
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @PostMapping("/email/check")
    public ResultResponse checkEmail(@RequestParam(name = "email") String email) {
        boolean isAvailableEmail = adminService.isAvailableEmail(email);
        if(isAvailableEmail) {
            return new ResultResponse(HttpStatus.OK.value(), "사용 가능한 이메일입니다.");
        } else {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), "이미 사용 중인 이메일입니다.");
        }
    }
}
