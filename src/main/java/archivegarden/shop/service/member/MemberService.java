package archivegarden.shop.service;

import archivegarden.shop.dto.member.MemberSaveDto;

public interface MemberService {

    Long join(MemberSaveDto dto);

    boolean duplicateLoginId(String loginId);

    boolean duplicateEmail(String email);

    boolean duplicatePhonenumber(String phonenumber);
}
