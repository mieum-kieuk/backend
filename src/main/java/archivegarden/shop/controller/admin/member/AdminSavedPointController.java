package archivegarden.shop.controller.admin.member;

import archivegarden.shop.dto.admin.admin.AdminSearchForm;
import archivegarden.shop.dto.admin.member.SavedPointListDto;
import archivegarden.shop.service.admin.member.AdminSavedPointService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin/points")
@RequiredArgsConstructor
public class AdminSavedPointController {

    private final AdminSavedPointService savedPointService;

    @GetMapping
    public String savedPoints(@ModelAttribute("form")AdminSearchForm form, @RequestParam(name = "page", defaultValue = "1") int page, Model model) {

        String errorMessage = validateDate(form.getStartDate(), form.getEndDate());
        if(errorMessage != null) {
            model.addAttribute("error", errorMessage);
            return "admin/member/point_list";
        }

        PageRequest pageRequest = PageRequest.of(page - 1, 10);
        Page<SavedPointListDto> savedPointListDtos = savedPointService.getSavedPoints(form, pageRequest);
        model.addAttribute("points", savedPointListDtos);
        return "admin/member/point_list";
    }

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
