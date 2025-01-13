package archivegarden.shop.controller.admin.admin;

import archivegarden.shop.dto.admin.AdminSearchCondition;
import archivegarden.shop.dto.admin.admin.AdminListDto;
import archivegarden.shop.dto.admin.admin.JoinAdminForm;
import archivegarden.shop.dto.common.JoinSuccessDto;
import archivegarden.shop.exception.common.DuplicateEntityException;
import archivegarden.shop.service.admin.admin.AdminAdminService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminAdminController {

    private final AdminAdminService adminService;

    /**
     * 관리자 로그인 처리하는 메서드
     */
    @GetMapping("/login")
    public String login(@RequestParam(name = "error", required = false) boolean error,
                        @RequestParam(name = "exception", required = false) String errorMessage, Model model) {
        model.addAttribute("error", error);
        model.addAttribute("errorMessage", errorMessage);

        return "admin/login";
    }

    /**
     * 관리자 회원가입 폼을 반환하는 메서드
     */
    @GetMapping("/join")
    public String addAdminForm(@ModelAttribute("joinForm") JoinAdminForm form) {
        return "admin/admin/join";
    }

    /**
     * 관리자 회원가입 요청을 처리하는 메서드
     */
    @PostMapping("/join")
    public String join(@Valid @ModelAttribute("joinForm") JoinAdminForm form, BindingResult bindingResult, HttpSession session) {
        validateJoin(form, bindingResult);
        if(bindingResult.hasErrors()) {
            return "admin/admin/join";
        }

        try {
            adminService.checkAdminDuplicate(form);
        } catch (DuplicateEntityException e) {
            return "redirect:/admin/join";
        }

        Long adminId = adminService.join(form);
        session.setAttribute("join:adminId", adminId);
        return "redirect:/admin/join/complete";
    }

    /**
     * 관리자 회원가입 완료 후 결과 페이지를 반환하는 메서드
     */
    @GetMapping("/join/complete")
    public String joinComplete(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        Long adminId = (Long) session.getAttribute("join:adminId");
        if(session == null || adminId == null) {
            return "redirect:/admin/join";
        }
        session.removeAttribute("join:adminId");

        JoinSuccessDto joinSuccessDto = adminService.getJoinSuccessInfo(adminId);
        model.addAttribute("adminInfo", joinSuccessDto);
        return "admin/admin/join_complete";
    }

    /**
     * 전체 관리자 조회하는 메서드
     */
    @GetMapping("/admins")
    public String admins(@ModelAttribute("form") AdminSearchCondition form, @RequestParam(name = "page", defaultValue = "1") int page, Model model) {
        String errorMessage = validateDate(form.getStartDate(), form.getEndDate());
        if(errorMessage != null) {
            model.addAttribute("error", errorMessage);
            return "admin/admin/admin_list";
        }

        PageRequest pageRequest = PageRequest.of(page - 1, 10);
        Page<AdminListDto> adminListDtos = adminService.getAdmins(form, pageRequest);
        model.addAttribute("admins", adminListDtos);
        return "admin/admin/admin_list";
    }

    /**
     * 관리자 회원가입 폼의 복합적인 유효성 검증을 수행하는 메서드
     * - 비밀번호 확인: 비밀번호와 비밀번호 확인이 일치하는지 검사합니다.
     */
    private void validateJoin(JoinAdminForm form, BindingResult bindingResult) {
        if(StringUtils.hasText(form.getPassword())) {
            if(!form.getPassword().equals(form.getPasswordConfirm())) {
                bindingResult.rejectValue("passwordConfirm", "mismatch");
            }
        }
    }

    /**
     * 기간 검색 조건 유효성 검증을 수행하는 메서드
     * - 시작일시, 종료일시: 유효한 날짜인지 검사합니다.
     */
    private String validateDate(LocalDate startDate, LocalDate endDate) {
        if(startDate == null && endDate != null) {
            return "시작일시를 입력해 주세요.";
        } else if(startDate != null && endDate == null) {
            return "종료일시를 입력해 주세요.";
        }

        if(startDate != null && endDate != null) {
            if(startDate.isAfter(LocalDate.now())) {
                return "시작일시는 과거 또는 현재 날짜여야 합니다.";
            } else if (endDate.isAfter(LocalDate.now())) {
                return "종료일시는 과거 또는 현재 날짜여야 합니다.";
            } else  if(endDate.isBefore(startDate)) {
                return "시작일시가 종료일시보다 과거 날짜여야 합니다.";
            }
        }

        return null;
    }
}
