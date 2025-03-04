package archivegarden.shop.controller.admin.member.membership;

import archivegarden.shop.dto.admin.member.membership.AdminAddMembershipForm;
import archivegarden.shop.dto.admin.member.membership.AdminEditMembershipForm;
import archivegarden.shop.dto.admin.member.membership.AdminMembershipDto;
import archivegarden.shop.service.admin.member.membership.AdminMembershipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/memberships")
public class AdminMembershipController {

    private final AdminMembershipService membershipService;

    /**
     * 회원 등급 등록 폼을 반환하는 메서드
     */
    @GetMapping("/add")
    public String addMembershipForm(@ModelAttribute("addMembershipForm") AdminAddMembershipForm form) {
        return "admin/member/membership/add_membership";
    }

    /**
     * 회원 등급 등록 요청을 처리하는 메서드
     */
    @PostMapping("/add")
    public String addMembership(@Valid @ModelAttribute("addMembershipForm") AdminAddMembershipForm form, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/member/membership/add_membership";
        }

        Short membershipId = membershipService.saveMembership(form);
        redirectAttributes.addAttribute("membershipId", membershipId);
        return "redirect:/admin/memberships/{membershipId}";
    }

    /**
     * 회원 등급 상세 페이지를 조회하는 요청을 처리하는 메서드
     */
    @GetMapping("/{membershipId}")
    public String membershipDetails(@PathVariable("membershipId") Short membershipId, Model model) {
        AdminMembershipDto membershipDetailsDto = membershipService.getMembership(membershipId);
        model.addAttribute("membership", membershipDetailsDto);
        return "admin/member/membership/membership_details";
    }

    /**
     * 회원 등급 목록을 조회하는 요청을 처리하는 메서드
     */
    @GetMapping
    public String memberships(Model model) {
        List<AdminMembershipDto> adminMembershipDtos = membershipService.getMemberShips();
        model.addAttribute("memberships", adminMembershipDtos);
        return "admin/member/membership/membership_list";
    }

    /**
     * 회원 등급 수정 폼을 반환하는 메서드
     */
    @GetMapping("/{membershipId}/edit")
    public String editMembershipForm(@PathVariable("membershipId") Short membershipId, Model model) {
        AdminEditMembershipForm editMembershipForm = membershipService.getEditMembershipForm(membershipId);
        model.addAttribute("form", editMembershipForm);
        return "admin/member/membership/edit_membership";
    }

    /**
     * 회원 등급 수정 요청을 처리하는 메서드
     */
    @PostMapping("/{membershipId}/edit")
    public String editMembership(@PathVariable("membershipId") Short membershipId, @Valid @ModelAttribute("form") AdminEditMembershipForm form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "admin/member/membership/edit_membership";
        }

        membershipService.updateMembership(membershipId, form);
        return "redirect:/admin/memberships/{membershipId}";
    }
}
