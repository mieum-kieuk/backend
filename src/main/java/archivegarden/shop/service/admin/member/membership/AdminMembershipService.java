package archivegarden.shop.service.admin.member.membership;

import archivegarden.shop.dto.admin.member.membership.AdminAddMembershipForm;
import archivegarden.shop.dto.admin.member.membership.AdminEditMembershipForm;
import archivegarden.shop.dto.admin.member.membership.AdminMembershipDto;
import archivegarden.shop.entity.Membership;
import archivegarden.shop.exception.common.EntityNotFoundException;
import archivegarden.shop.repository.membership.MembershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminMembershipService {

    private final MembershipRepository membershipRepository;

    /**
     * 회원 등급 저장
     */
    public Short saveMembership(AdminAddMembershipForm form) {
        Integer maxLevel = membershipRepository.findMaxLevelMembership();
        int level = maxLevel == null ? 1 : maxLevel + 1;
        Membership membership = Membership.createMembership(form, level);
        membershipRepository.save(membership);
        return membership.getId();
    }

    /**
     * 회원 등급 단건 조회
     */
    @Transactional(readOnly = true)
    public AdminMembershipDto getMembership(Short membershipId) {
        Membership membership = membershipRepository.findById(membershipId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원 등급입니다."));
        return new AdminMembershipDto(membership);
    }

    /**
     * 회원 등급 목록 조회
     */
    @Transactional(readOnly = true)
    public List<AdminMembershipDto> getMemberShips() {
        return membershipRepository.findAllLevelDesc()
                .stream().map(AdminMembershipDto::new)
                .collect(Collectors.toList());
    }

    /**
     * 회원 등급 수정 폼 조회
     *
     * @throws EntityNotFoundException
     */
    @Transactional(readOnly = true)
    public AdminEditMembershipForm getEditMembershipForm(Short membershipId) {
        Membership membership = membershipRepository.findById(membershipId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원 등급입니다."));
        return new AdminEditMembershipForm(membership);
    }

    /**
     * 회원 등급 수정
     *
     * @throws EntityNotFoundException
     */
    public void updateMembership(Short membershipId, AdminEditMembershipForm form) {
        Membership membership = membershipRepository.findById(membershipId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원 등급입니다."));
        membership.update(form);
    }


    /**
     * Ajax: 회원 등급명 중복 검사
     */
    public boolean isAvailableName(String name) {
        return membershipRepository.findByName(name).isEmpty();
    }

    /**
     * Ajax : 회원 등급 단건 삭제
     *
     * @throws EntityNotFoundException, IllegalStateException
     */
    public boolean deleteMembership(Short membershipId) {
        Membership membership = membershipRepository.findById(membershipId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원 등급입니다."));
        if (membership.getLevel() == 1) {
            throw new IllegalStateException("기본 등급은 삭제할 수 없습니다.");
        }

        membershipRepository.delete(membership);
        return true;
    }
}
