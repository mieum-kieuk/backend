package archivegarden.shop.service;

import archivegarden.shop.web.form.MemberSaveDto;

public interface MemberService {

    Long join(MemberSaveDto dto);
}
