package archivegarden.shop.service.point;

import archivegarden.shop.entity.Member;
import archivegarden.shop.entity.SavedPoint;
import archivegarden.shop.entity.SavedPointType;
import archivegarden.shop.exception.NoSuchMemberException;
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
     */
    public Long addPoint(Long memberId, SavedPointType type, int amount) {
        //Member 조회
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchMemberException("존재하지 않는 회원입니다."));

        //SavedPoint 생성
        SavedPoint savedPoint = SavedPoint.createSavedPoint(amount, type, member);

        //SavedPoint 저장
        savedPointRepository.save(savedPoint);

        return savedPoint.getId();
    }

    /**
     * 적립금 조회
     */
    public int getPoint(Long memberId) {
        return Optional.ofNullable(savedPointRepository.findBalance(memberId)).orElse(0).intValue();
    }
}
