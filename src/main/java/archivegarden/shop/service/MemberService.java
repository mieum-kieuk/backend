package archivegarden.shop.service;

import archivegarden.shop.web.form.MemberSaveDto;

public interface MemberService {

    Long join(MemberSaveDto dto);

    boolean duplicateLoginId(String loginId);

    boolean duplicateEmail(String email);
}
