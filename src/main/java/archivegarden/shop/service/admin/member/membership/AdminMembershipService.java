package archivegarden.shop.service.admin.member.membership;

import archivegarden.shop.dto.admin.member.membership.AdminAddMembershipForm;
import archivegarden.shop.dto.admin.member.membership.AdminEditMembershipForm;
import archivegarden.shop.dto.admin.member.membership.AdminMembershipDto;
import archivegarden.shop.entity.Membership;
import archivegarden.shop.exception.global.EntityNotFoundException;
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
     * 회원 멤버십 등록
     *
     * @param form 등록할 회원 멤버십 정보 폼 DTO
     * @return 저장된 회원 멤버십 엔티티의 ID
     */
    public Long saveMembership(AdminAddMembershipForm form) {
        Integer maxLevel = membershipRepository.findMaxLevelMembership();
        int level = maxLevel + 1;

        Membership membership = Membership.createMembership(form, level);
        membershipRepository.save(membership);
        return membership.getId();
    }

    /**
     * 회원 멤버십 상세 조회
     *
     * @param membershipId 조회할 회원 멤버십의 ID
     * @return 회원 멤버십 상세 정보 DTO
     * @throws EntityNotFoundException 해당 ID를 가진 회원 멤버십이 존재하지 않을 경우
     */
    @Transactional(readOnly = true)
    public AdminMembershipDto getMembership(Long membershipId) {
        Membership membership = membershipRepository.findById(membershipId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원 등급입니다."));
        return new AdminMembershipDto(membership);
    }

    /**
     * 회원 멤버십 목록 조회
     *
     * @return 회원 멤버십 목록을 담은 List 객체
     */
    @Transactional(readOnly = true)
    public List<AdminMembershipDto> getMemberShips() {
        return membershipRepository.findAllOrderByLevelAsc()
                .stream().map(AdminMembershipDto::new)
                .collect(Collectors.toList());
    }

    /**
     * 회원 멤버십 수정 폼 조회
     *
     * @param membershipId 수정할 회원 멤버십의 ID
     * @return 회원 멤버십 수정 폼 DTO
     * @throws EntityNotFoundException 해당 ID를 가진 회원 멤버십이 존재하지 않을 경우
     */
    @Transactional(readOnly = true)
    public AdminEditMembershipForm getEditMembershipForm(Long membershipId) {
        Membership membership = membershipRepository.findById(membershipId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원 등급입니다."));
        return new AdminEditMembershipForm(membership);
    }

    /**
     * 회원 등급 수정
     *
     * @param membershipId 수정할 회원 멤버십의 ID
     * @param form         수정할 회원 멤버십 정보 폼 DTO
     * @throws EntityNotFoundException 해당 ID를 가진 회원 멤버십이 존재하지 않을 경우
     */
    public void updateMembership(Long membershipId, AdminEditMembershipForm form) {
        Membership membership = membershipRepository.findById(membershipId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원 등급입니다."));
        membership.update(form);
    }


    /**
     * 회원 멤버십명 중복 검사
     *
     * @param name 중복 검사할 회원 등급명
     * @return 사용할 수 있으면 true, 이미 사용 중이면 false
     */
    public boolean isAvailableName(String name) {
        return membershipRepository.findByName(name).isEmpty();
    }

    /**
     * 회원 멤버십 삭제
     *
     * @param membershipId 삭제할 회원 멤버십의 ID
     * @throws EntityNotFoundException 해당 ID를 가진 회원 멤버십이 존재하지 않을 경우
     * @throws UnsupportedOperationException   기본 등급 삭제하려는 경우
     */
    public boolean deleteMembership(Long membershipId) {
        Membership membership = membershipRepository.findById(membershipId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원 등급입니다."));
        if (membership.isDefault()) {
            throw new UnsupportedOperationException("기본 등급은 삭제할 수 없습니다.");
        }

        membershipRepository.delete(membership);
        return true;
    }
}
