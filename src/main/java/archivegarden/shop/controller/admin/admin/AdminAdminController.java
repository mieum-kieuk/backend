package archivegarden.shop.controller.admin.admin;

import archivegarden.shop.dto.admin.AdminSearchCondition;
import archivegarden.shop.dto.admin.admin.AdminListDto;
import archivegarden.shop.dto.admin.admin.JoinAdminForm;
import archivegarden.shop.dto.common.JoinSuccessDto;
import archivegarden.shop.exception.global.DuplicateEntityException;
import archivegarden.shop.service.admin.admin.AdminAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminAdminController {

    private final AdminAdminService adminService;

    @GetMapping("/login")
    public String login(
            @RequestParam(name = "error", required = false) boolean error,
            @RequestParam(name = "exception", required = false) String errorMessage,
            Model model
    ) {
        model.addAttribute("error", error);
        model.addAttribute("errorMessage", errorMessage);

        return "admin/login";
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/admin";
    }

    @GetMapping("/join")
    public String joinAdminForm(@ModelAttribute("joinForm") JoinAdminForm form) {
        return "admin/admin/join";
    }

    @PostMapping("/join")
    public String join(
            @Validated @ModelAttribute("joinForm") JoinAdminForm form,
            BindingResult bindingResult
    ) {
        validatePasswordConfirm(form, bindingResult);
        if (bindingResult.hasErrors()) {
            return "admin/admin/join";
        }

        try {
            adminService.checkAdminDuplicate(form);
        } catch (DuplicateEntityException e) {
            bindingResult.reject("error.global.admin.duplicate", e.getMessage());
            return "admin/admin/join";
        }

        Long adminId = adminService.join(form);
        return "redirect:/admin/join/" + adminId + "/complete";
    }

    @GetMapping("/join/{adminId}/complete")
    public String joinComplete(@PathVariable("adminId") Long adminId, Model model) {
        if(adminId == null) return "redirect:/admin/join";
        JoinSuccessDto joinSuccessDto = adminService.getJoinSuccessInfo(adminId);
        model.addAttribute("adminInfo", joinSuccessDto);
        return "admin/admin/join_complete";
    }

    @GetMapping("/admins")
    public String admins(
            @ModelAttribute("cond") AdminSearchCondition cond,
            @RequestParam(name = "page", defaultValue = "1") int page,
            Model model
    ) {
        String errorMessage = validateDate(cond.getStartDate(), cond.getEndDate());
        if (errorMessage != null) {
            model.addAttribute("error", errorMessage);
            return "admin/admin/admin_list";
        }

        PageRequest pageRequest = PageRequest.of(page - 1, 10);
        Page<AdminListDto> adminListDtos = adminService.getAdmins(cond, pageRequest);
        model.addAttribute("admins", adminListDtos);
        return "admin/admin/admin_list";
    }

    /**
     * 비밀번호와 비밀번호 확인이 일치하는지 검증합니다.
     *
     * @param form          관리자 회원가입 폼 DTO
     * @param bindingResult 유효성 검증 결과를 담는 객체
     */
    private void validatePasswordConfirm(JoinAdminForm form, BindingResult bindingResult) {
        if (StringUtils.hasText(form.getPassword())) {
            if (!form.getPassword().equals(form.getPasswordConfirm())) {
                bindingResult.rejectValue("passwordConfirm", "error.field.passwordConfirm.mismatch");
            }
        }
    }

    /**
     * 기간 검색 조건(시작일시, 종료일시)의 유효성을 검증합니다.
     *
     * 다음 조건을 검사합니다.
     * - 시작일시 또는 종료일시 중 하나만 입력된 경우
     * - 시작일시 또는 종료일시가 현재 날짜보다 미래인 경우
     * - 시작일시가 종료일시보다 미래인 경우
     *
     * @param startDate 검색 시작 날짜
     * @param endDate   검색 종료 날짜
     * @return 유효성 검증 실패 시 에러 메시지, 성공 시 null
     */
    private String validateDate(LocalDate startDate, LocalDate endDate) {
        if (startDate == null && endDate != null) {
            return "시작일시를 입력해 주세요.";
        } else if (startDate != null && endDate == null) {
            return "종료일시를 입력해 주세요.";
        }

        if (startDate != null && endDate != null) {
            if (startDate.isAfter(LocalDate.now())) {
                return "시작일시는 과거 또는 현재 날짜여야 합니다.";
            } else if (endDate.isAfter(LocalDate.now())) {
                return "종료일시는 과거 또는 현재 날짜여야 합니다.";
            } else if (endDate.isBefore(startDate)) {
                return "시작일시가 종료일시보다 과거 날짜여야 합니다.";
            }
        }

        return null;
    }
}
