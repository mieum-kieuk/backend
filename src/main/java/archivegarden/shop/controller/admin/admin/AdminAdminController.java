package archivegarden.shop.controller.admin.admin;

import archivegarden.shop.constant.AdminSessionConstants;
import archivegarden.shop.dto.admin.AdminSearchCondition;
import archivegarden.shop.dto.admin.admin.AdminListDto;
import archivegarden.shop.dto.admin.admin.JoinAdminForm;
import archivegarden.shop.dto.common.JoinSuccessDto;
import archivegarden.shop.exception.global.DuplicateEntityException;
import archivegarden.shop.service.admin.admin.AdminAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Admin 관리", description = "관리자 페이지에서 관리자 정보를 관리하는 웹 페이지 API")
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminAdminController {

    private final AdminAdminService adminService;

    @Operation(
            summary = "관리자 로그인 페이지 표시",
            description = "관리자 로그인 폼을 반환합니다. 로그인 실패 시 원인 에러 메시지를 포함합니다.",
            responses = {@ApiResponse(responseCode = "200", description = "성공적으로 로그인 페이지 반환")}
    )
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

    @Operation(
            summary = "로그아웃 후 관리자 홈으로 리다이렉트",
            description = "직접 로그아웃 경로 접근 시 관리자 홈 페이지로 이동합니다.",
            responses = {@ApiResponse(responseCode = "302", description = "관리자 홈 페이지로 리다이렉트")}
    )
    @GetMapping("/logout")
    public String logout() {
        return "redirect:/admin";
    }

    @Operation(
            summary = "관리자 회원가입 폼 표시",
            description = "관리자 회원가입 화면을 반환합니다.",
            responses = {@ApiResponse(responseCode = "200", description = "성공적으로 관리자 회원가입 폼 반환")}
    )
    @GetMapping("/join")
    public String joinAdminForm(@ModelAttribute("joinForm") JoinAdminForm form) {
        return "admin/admin/join";
    }

    @Operation(
            summary = "관리자 회원가입 요청",
            description = "새로운 관리자를 등록합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "유효성 검증 실패 또는 중복 오류 발생 시 회원가입 폼으로 다시 이동"),
                    @ApiResponse(responseCode = "302", description = "회원가입 성공 시 완료 페이지로 리다이렉트")
            }
    )
    @PostMapping("/join")
    public String join(
            @Valid @ModelAttribute("joinForm") JoinAdminForm form,
            BindingResult bindingResult,
            HttpSession session
    ) {
        validateJoinForm(form, bindingResult);
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
        session.setAttribute(AdminSessionConstants.JOIN_ADMIN_ID_KEY, adminId);
        return "redirect:/admin/join/complete";
    }

    @Operation(
            summary = "관리자 회원가입 완료 페이지 표시",
            description = "성공적으로 회원가입이 완료된 후 결과 페이지를 반환합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원가입 완료 페이지 반환"),
                    @ApiResponse(responseCode = "302", description = "세션에 정보가 없거나 유효하지 않으면 회원가입 폼으로 리다이렉트")
            }
    )
    @GetMapping("/join/complete")
    public String joinComplete(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        if (session == null) return "redirect:/admin/join";
        Long adminId = (Long) session.getAttribute(AdminSessionConstants.JOIN_ADMIN_ID_KEY);
        if (adminId == null) return "redirect:/admin/join";

        session.removeAttribute(AdminSessionConstants.JOIN_ADMIN_ID_KEY);

        JoinSuccessDto joinSuccessDto = adminService.getJoinSuccessInfo(adminId);
        model.addAttribute("adminInfo", joinSuccessDto);
        return "admin/admin/join_complete";
    }

    @Operation(
            summary = "관리자 목록 조회",
            description = "검색 조건에 따라 관리자 목록을 페이징하여 조회합니다.",
            responses = {@ApiResponse(responseCode = "200", description = "성공적으로 조건에 맞는 관리자 목록을 반환하거나 날짜 유효성 검증 실패 시 에러 메시지를 포함하여 전체 목록 페이지 반환"),}
    )
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
     * @param form          회원가입 폼 데이터를 담은 DTO
     * @param bindingResult 유효성 검증 결과를 담는 객체
     */
    private void validateJoinForm(JoinAdminForm form, BindingResult bindingResult) {
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
