package archivegarden.shop.controller;

import archivegarden.shop.dto.mypage.MyQnaListDto;
import archivegarden.shop.entity.Member;
import archivegarden.shop.service.community.QnaService;
import archivegarden.shop.web.annotation.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final QnaService qnaService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public String myPageHome() {
        return "mypage/mypage_home";
    }

    @GetMapping("/qna")
    @PreAuthorize("hasRole('ROLE_USER') and #loginMember.loginId == principal.username")
    public  String myQnas(@RequestParam(defaultValue = "1") int page, @CurrentUser Member loginMember, Model model) {
        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by("createdAt").descending());
        Page<MyQnaListDto> myQnaList = qnaService.getMyQnas(loginMember.getId(), pageRequest);
        model.addAttribute("qnas", myQnaList);
        return "mypage/board/qna_list";
    }
}
