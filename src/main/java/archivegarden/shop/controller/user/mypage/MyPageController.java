package archivegarden.shop.controller.user.mypage;

import archivegarden.shop.dto.user.community.inquiry.MyInquiryListDto;
import archivegarden.shop.dto.user.member.EditMemberInfoForm;
import archivegarden.shop.entity.Member;
import archivegarden.shop.service.user.community.InquiryService;
import archivegarden.shop.service.user.member.MemberService;
import archivegarden.shop.util.PageRequestUtil;
import archivegarden.shop.web.annotation.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MemberService memberService;
    private final InquiryService inquiryService;

    /**
     * 개인 정보 수정을 위한 본인확인(로그인) 페이지를 반환하는 메서드
     */
    @GetMapping("/info")
    @PreAuthorize("hasRole('ROLE_USER')")
    public String validateIdentity(@CurrentUser Member loginMember, Model model) {
        model.addAttribute("loginId", loginMember.getLoginId());
        return "user/mypage/member/validate_identity";
    }

    /**
     * 개인 정보 수정 폼을 반환하는 메서드
     */
    @GetMapping("/info/edit")
    @PreAuthorize("hasRole('ROLE_USER')")
    public String editMemberInfoForm(@CurrentUser Member loginMember, Model model) {
        EditMemberInfoForm editMemberInfoForm = memberService.getMemberInfo(loginMember.getId());
        model.addAttribute("editForm", editMemberInfoForm);
        return "user/mypage/member/edit_info";
    }

    /**
     * 개인 정보 수정 요청을 처리하는 메서드
     */
    @PostMapping("/info/edit")
    @PreAuthorize("hasRole('ROLE_USER') and #loginMember.loginId == principal.username")
    public String editMemberInfo(@Valid @ModelAttribute("form") EditMemberInfoForm form, BindingResult bindingResult, @CurrentUser Member loginMember) {
        if(bindingResult.hasErrors()) {
            return "user/mypage/member/edit_info";
        }

        memberService.editMemberInfo(loginMember.getId(), form);
        return "redirect:/mypage/info";
    }

    @Operation(
            summary = "내 상품 문의 목록 조회",
            description = "사용자가 작성한 상품 문의 목록을 페이징하여 조회합니다."
    )
    @GetMapping("/inquiries")
    @PreAuthorize("hasRole('ROLE_USER')")
    public  String myInquiries(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @CurrentUser Member loginMember,
            Model model
    ) {
        PageRequest pageRequest = PageRequestUtil.of(page, 1);
        Page<MyInquiryListDto> inquiryListDtos = inquiryService.getMyInquires(loginMember.getId(), pageRequest);
        model.addAttribute("inquiries", inquiryListDtos);
        return "user/mypage/inquiry/inquiry_list";
    }
}
