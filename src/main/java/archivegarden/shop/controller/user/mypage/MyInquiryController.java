package archivegarden.shop.controller.user.mypage;

import archivegarden.shop.dto.user.community.inquiry.MyInquiryListDto;
import archivegarden.shop.entity.Member;
import archivegarden.shop.service.user.community.InquiryService;
import archivegarden.shop.util.PageRequestUtil;
import archivegarden.shop.web.annotation.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "상품 문의", description = "사용자 페이지에서 마이페이지 상품 문의 관련 API")
@Controller
@RequestMapping("/mypage/inquiries")
@RequiredArgsConstructor
public class MyInquiryController {

    private final InquiryService inquiryService;

    @Operation(
            summary = "내 상품 문의 목록 조회",
            description = "사용자가 작성한 상품 문의 목록을 페이징하여 조회합니다."
    )
    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public  String myInquiries(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @CurrentUser Member loginMember,
            Model model
    ) {
        PageRequest pageRequest = PageRequestUtil.of(page);
        Page<MyInquiryListDto> inquiryListDtos = inquiryService.getMyInquires(loginMember.getId(), pageRequest);
        model.addAttribute("inquiries", inquiryListDtos);
        return "user/mypage/inquiry/inquiry_list";
    }
}
