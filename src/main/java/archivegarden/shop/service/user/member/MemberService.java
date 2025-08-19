package archivegarden.shop.service.user.member;

import archivegarden.shop.dto.common.JoinSuccessDto;
import archivegarden.shop.dto.user.member.EditMemberInfoForm;
import archivegarden.shop.dto.user.member.JoinMemberForm;
import archivegarden.shop.entity.Member;
import archivegarden.shop.entity.auth.VerificationCodeResult;

public interface MemberService {

    Long join(JoinMemberForm form);

    void checkMemberDuplicate(JoinMemberForm form);

    JoinSuccessDto joinComplete(Long memberId);

    boolean isAvailableLoginId(String loginId);

    boolean isAvailableEmail(String email);

    boolean isAvailablePhonenumber(String phonenumber);

    void sendVerificationCode(String phonenumber);

    VerificationCodeResult verifyVerificationCode(String phonenumber, String verificationCode);

    boolean validateIdentity(Member loginMember, String password);

    EditMemberInfoForm getMemberInfo(Long id);

    boolean isNewPassword(String newPassword, String password);

    void editMemberInfo(Long memberId, EditMemberInfoForm form);
}
