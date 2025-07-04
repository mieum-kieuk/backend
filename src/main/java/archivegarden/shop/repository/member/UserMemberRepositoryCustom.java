package archivegarden.shop.repository.member;

import archivegarden.shop.dto.user.member.EditMemberInfoForm;

import java.util.Optional;

public interface UserMemberRepositoryCustom {

    Optional<EditMemberInfoForm> fetchEditMemberInfoForm(Long memberId);
}
