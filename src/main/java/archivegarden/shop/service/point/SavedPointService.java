package archivegarden.shop.service.point;

import archivegarden.shop.entity.Member;
import archivegarden.shop.entity.SavedPoint;
import archivegarden.shop.entity.SavedPointType;
import archivegarden.shop.exception.global.EntityNotFoundException;
import archivegarden.shop.repository.member.MemberRepository;
import archivegarden.shop.repository.point.SavedPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class SavedPointService {

    private final SavedPointRepository savedPointRepository;
    private final MemberRepository memberRepository;

    /**
     * 적립금 지급
     *
     * @throws EntityNotFoundException 회원이 존재하지 않을 때
     */
    public Long addPoint(Long memberId, SavedPointType type, int amount) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));

        SavedPoint savedPoint = SavedPoint.createSavedPoint(amount, type, member);
        savedPointRepository.save(savedPoint);
        return savedPoint.getId();
    }

    /**
     * 적립금 조회
     */
    public int getPoint(Long memberId) {
        return Optional.ofNullable(savedPointRepository.findBalance(memberId)).orElse(0).intValue();
    }

    /**
     * 마이페이지에서 적립금 내역 조회
     */
    public void getPoints(Long memberId) {
//        savedPointRepository.findByMemberId(memberId);
    }
}
