package archivegarden.shop.controller;

import archivegarden.shop.dto.ResultResponse;
import archivegarden.shop.entity.Member;
import archivegarden.shop.service.order.CartService;
import archivegarden.shop.service.user.member.MemberService;
import archivegarden.shop.web.annotation.CurrentUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ajax")
@RequiredArgsConstructor
public class AjaxController {

    private final MemberService memberService;
    private final CartService cartService;

    //카트 -> 주문서
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/checkout")
    @PreAuthorize("#loginMember.loginId == principal.username")
    public ResultResponse checkout(@RequestBody List<Long> productIds, @CurrentUser Member loginMember, HttpServletRequest request) {
        cartService.validateStockQuantity(productIds, loginMember);

        //구매할 상품 목록 세션에 저장
        HttpSession session = request.getSession();
        session.setAttribute("checkout:products", productIds);
        return new ResultResponse(HttpStatus.OK.value(), "주문서로 이동합니다.");
    }

    //마이페이지: 개인 정보 수정 전 본인 확인
    @PostMapping("/mypage/validate/member")
    @PreAuthorize("#loginMember.loginId == principal.username")
    public ResultResponse validateMember(@RequestParam("password") String password,  @CurrentUser Member loginMember) {
        boolean isCurrentMember = memberService.mypageInfoLogin(loginMember, password);
        if(isCurrentMember) {
            return new ResultResponse(HttpStatus.OK.value(), "본인 확인이 완료되었습니다.");
        } else {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), "비밀번호가 일치하지 않습니다.");
        }
    }
}
