package archivegarden.shop.service.member;

import archivegarden.shop.dto.member.*;

import java.util.Optional;

public interface MemberService {

    Long join(MemberSaveForm dto);

    boolean isAvailableLoginId(String loginId);

    boolean isAvailableEmail(String email);

    boolean isAvailablePhonenumber(String phonenumber);

    void sendVerificationNo(String phonenumber);

    boolean validateVerificationNo(VerificationRequestDto requestDto);

    NewMemberInfo getNewMemberInfo(Long memberId);

    Optional<FindIdResultDto> findId(FindIdForm form);

    String findPassword(FindPasswordForm form);
}
