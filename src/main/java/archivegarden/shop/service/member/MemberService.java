package archivegarden.shop.service.member;

import archivegarden.shop.dto.member.MemberSaveDto;
import archivegarden.shop.dto.member.NewMemberInfo;
import archivegarden.shop.dto.member.VerificationRequestDto;

public interface MemberService {

    Long join(MemberSaveDto dto);

    boolean duplicateLoginId(String loginId);

    boolean duplicateEmail(String email);

    boolean duplicatePhonenumber(String phonenumber);

    void sendVerificationNo(String phonenumber);

    boolean validateVerificationNo(VerificationRequestDto requestDto);

    NewMemberInfo getNewMemberInfo(Long memberId);
}
