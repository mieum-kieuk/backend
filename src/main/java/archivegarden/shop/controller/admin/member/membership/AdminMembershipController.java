package archivegarden.shop.controller.admin.member.membership;

import archivegarden.shop.dto.admin.member.membership.AdminAddMembershipForm;
import archivegarden.shop.dto.admin.member.membership.AdminEditMembershipForm;
import archivegarden.shop.dto.admin.member.membership.AdminMembershipDto;
import archivegarden.shop.service.admin.member.membership.AdminMembershipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Tag(name = "Membership 관리", description = "관리자 페이지에서 회원 등급 관련 API")
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/memberships")
public class AdminMembershipController {

    private final AdminMembershipService membershipService;

    @Operation(
            summary = "회원 멤버십 등록 폼 표시",
            description = "새로운 회원 멤버십 등록을 위한 페이지를 반환합니다.",
            responses = { @ApiResponse(responseCode = "200", description = "성공적으로 회원 멤버십 등록 폼 반환") }
    )
    @GetMapping("/add")
    public String addMembershipForm(@ModelAttribute("addForm") AdminAddMembershipForm form) {
        return "admin/member/membership/add_membership";
    }

    @Operation(
            summary = "회원 멤버십 등록 요청",
            description = "새로운 회원 멤버십을 등록하고 상세 페이지로 리다이렉트합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "유효성 검증 실패 시 등록 폼으로 다시 이동"),
                    @ApiResponse(responseCode = "302", description = "등록 성공 후 상세 페이지로 리다이렉트")
            }
    )
    @PostMapping("/add")
    public String addMembership(
            @Valid @ModelAttribute("addForm") AdminAddMembershipForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "admin/member/membership/add_membership";
        }

        Long membershipId = membershipService.saveMembership(form);
        redirectAttributes.addAttribute("membershipId", membershipId);
        return "redirect:/admin/memberships/{membershipId}";
    }

    @Operation(
            summary = "회원 멤버십 상세 조회",
            description = "특정 회원 멤버십의 상세 정보를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원 멤버쉽 상세 페이지 반환"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 멤버십")
            }
    )
    @GetMapping("/{membershipId}")
    public String membershipDetails(@PathVariable("membershipId") Long membershipId, Model model) {
        AdminMembershipDto membershipDetailsDto = membershipService.getMembership(membershipId);
        model.addAttribute("membership", membershipDetailsDto);
        return "admin/member/membership/membership_details";
    }

    @Operation(
            summary = "회원 멤버십 목록 조회",
            description = "회원 멤버십 목록을 조회합니다",
            responses = { @ApiResponse(responseCode = "200", description = "성공적으로 회원 멤버십 목록 반환") }
    )
    @GetMapping
    public String memberships(Model model) {
        List<AdminMembershipDto> adminMembershipDtos = membershipService.getMemberShips();
        model.addAttribute("memberships", adminMembershipDtos);
        return "admin/member/membership/membership_list";
    }

    @Operation(
            summary = "회원 멤버십 수정 폼 표시",
            description = "기존 회원 멤버십을 수정하기 위한 페이지를 반환합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공적으로 회원 멤버십 수정 폼 반환"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 멤버십")
            }
    )
    @GetMapping("/{membershipId}/edit")
    public String editMembershipForm(@PathVariable("membershipId") Long membershipId, Model model) {
        AdminEditMembershipForm editMembershipForm = membershipService.getEditMembershipForm(membershipId);
        model.addAttribute("editForm", editMembershipForm);
        return "admin/member/membership/edit_membership";
    }

    @Operation(
            summary = "회원 멤버십 수정 요청",
            description = "기존 회원 멤버십을 수정하고 상세 페이지로 리다이렉트합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "유효성 검증 실패 시 수정 폼으로 다시 이동"),
                    @ApiResponse(responseCode = "302", description = "수정 성공 후 상세 페이지로 리다이렉트"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 멤버십")
            }
    )
    @PostMapping("/{membershipId}/edit")
    public String editMembership(
            @PathVariable("membershipId") Long membershipId,
            @Valid @ModelAttribute("editForm") AdminEditMembershipForm form,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "admin/member/membership/edit_membership";
        }

        membershipService.updateMembership(membershipId, form);
        return "redirect:/admin/memberships/{membershipId}";
    }
}
