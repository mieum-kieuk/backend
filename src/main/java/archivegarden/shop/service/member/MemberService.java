package archivegarden.shop.service.member;

import archivegarden.shop.dto.admin.member.MemberListDto;
import archivegarden.shop.dto.user.member.*;
import archivegarden.shop.entity.Member;

import java.util.List;

public interface MemberService {

    Long join(AddMemberForm dto);

    MemberJoinInfoDto joinComplete(Long memberId);

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

    List<MemberListDto> getLatestJoinMembers();

    MemberInfo getMemberInfo(Long id);
}
