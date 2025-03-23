package archivegarden.shop.repository.member;

import archivegarden.shop.dto.user.member.EditMemberInfoForm;

public interface UserMemberRepositoryCustom {

    EditMemberInfoForm findByIdWithDefaultDelivery(Long memberId);
}
