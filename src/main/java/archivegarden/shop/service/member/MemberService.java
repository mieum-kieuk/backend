package archivegarden.shop.service.member;

import archivegarden.shop.dto.member.*;

import java.util.Optional;

public interface MemberService {

    Long join(MemberSaveDto dto);

    boolean duplicateLoginId(String loginId);

    boolean duplicateEmail(String email);

    boolean duplicatePhonenumber(String phonenumber);

    void sendVerificationNo(String phonenumber);

    boolean validateVerificationNo(VerificationRequestDto requestDto);

    NewMemberInfo getNewMemberInfo(Long memberId);

    Optional<FindIdResultDto> findId(FindIdForm form);
}
