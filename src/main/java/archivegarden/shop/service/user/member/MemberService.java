package archivegarden.shop.service.user.member;

import archivegarden.shop.dto.common.JoinSuccessDto;
import archivegarden.shop.dto.user.member.FindIdResultDto;
import archivegarden.shop.dto.user.member.JoinMemberForm;
import archivegarden.shop.dto.user.member.MemberInfo;
import archivegarden.shop.dto.user.member.VerificationRequestDto;
import archivegarden.shop.entity.Member;

public interface MemberService {

    Long join(JoinMemberForm dto);

    void checkMemberDuplicate(JoinMemberForm form);

    JoinSuccessDto joinComplete(Long memberId);

    boolean isAvailableLoginId(String loginId);

    boolean isAvailableEmail(String email);

    boolean isAvailablePhonenumber(String phonenumber);

    void sendVerificationNo(String phonenumber);

    boolean validateVerificationNo(VerificationRequestDto requestDto);

    Long checkLoginIdExistsByEmail(String name, String email);

    Long checkIdExistsByPhonenumber(String name, String phonenumber);

    FindIdResultDto findIdComplete(Long memberId);

    String checkPasswordExistsByEmail(String loginId, String name, String email);

    String checkPasswordExistsByPhonenumber(String loginId, String name, String phonenumber);

    boolean mypageInfoLogin(Member loginMember, String password);

    MemberInfo getMemberInfo(Long id);
}
