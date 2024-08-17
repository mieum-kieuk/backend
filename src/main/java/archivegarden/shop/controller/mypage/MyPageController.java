package archivegarden.shop.controller.mypage;

import archivegarden.shop.entity.Member;
import archivegarden.shop.service.member.MemberService;
import archivegarden.shop.service.point.SavedPointService;
import archivegarden.shop.web.annotation.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {

//    private final QnaService qnaService;
    private final MemberService memberService;
    private final SavedPointService savedPointService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public String myPageHome() {
        return "mypage/mypage_home";
    }

    //적립금 내역
//    @GetMapping("/point")
//    @PreAuthorize("hasRole('ROLE_USER') and #loginMember.loginId == principal.username")
//    public String myPoints(@CurrentUser Member loginMember) {
//        savedPointService.getPoints(loginMember.getId());
//    }

//    @GetMapping("/qna")
//    @PreAuthorize("hasRole('ROLE_USER') and #loginMember.loginId == principal.username")
//    public  String myQnas(@RequestParam(defaultValue = "1") int page, @CurrentUser Member loginMember, Model model) {
//        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by("createdAt").descending());
//        Page<MyQnaListDto> myQnaList = qnaService.getMyQnas(loginMember.getId(), pageRequest);
//        model.addAttribute("qnas", myQnaList);
//        return "mypage/board/qna_list";
//    }

    @GetMapping("/info")
    @PreAuthorize("hasRole('ROLE_USER') and #loginMember.loginId == principal.username")
    public String infoModifyLogin(@CurrentUser Member loginMember, Model model) {
        model.addAttribute("loginId", loginMember.getLoginId());
        return "mypage/member/member_info_login";
    }

    @GetMapping("/info/edit")
    @PreAuthorize("hasRole('ROLE_USER') and #loginMember.loginId == principal.username")
    public String infoModify(@CurrentUser Member loginMember, Model model) {
        model.addAttribute("member", loginMember);
        return "mypage/member/member_info_modify";
    }
}
