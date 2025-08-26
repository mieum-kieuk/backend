package archivegarden.shop.service.user.account;

import archivegarden.shop.dto.user.member.FindIdResultDto;
import archivegarden.shop.entity.Member;
import archivegarden.shop.event.TempPasswordIssuedEvent;
import archivegarden.shop.exception.global.EntityNotFoundException;
import archivegarden.shop.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;

    private final PasswordEncoder passwordEncoder;

    private static final char[] PASSWORD_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static final java.security.SecureRandom SECURE_RANDOM = new java.security.SecureRandom();

    /**
     * 이름 + 이메일로 회원 찾기
     *
     * @param name  회원 이름
     * @param email 회원 이메일
     * @return 회원 ID(Optional) - 존재하지 않으면 Optional.empty()
     */
    @Transactional(readOnly = true)
    public Optional<Long> findLoginIdByEmail(String name, String email) {
        return memberRepository.findMemberIdByNameAndEmail(name, email);
    }

    /**
     * 이름 + 휴대전화번호로 회원 찾기
     *
     * @param name        회원 이름
     * @param phonenumber 회원 이메일
     * @return 회원 ID(Optional) - 존재하지 않으면 Optional.empty()
     */
    @Transactional(readOnly = true)
    public Optional<Long> findLoginIdByPhone(String name, String phonenumber) {
        return memberRepository.findMemberIdByNameAndPhonenumber(name, phonenumber);
    }

    /**
     * 로그인 아이디 + 이름 + 이메일로 회원 찾기
     *
     * @param loginId 회원 로그인 아이디
     * @param name    회원 이름
     * @param email   회원 이메일
     * @return 회원 ID(Optional) - 존재하지 않으면 Optional.empty()
     */
    public Optional<Long> findPasswordByEmail(String loginId, String name, String email) {
        return memberRepository.findMemberIdByLoginIdAndNameAndEmail(loginId, name, email);
    }

    /**
     * 로그인 아이디 + 이름 + 휴대전화번호로 회원 찾기
     *
     * @param loginId     회원 로그인 아이디
     * @param name        회원 이름
     * @param phonenumber 회원 휴대전화번호
     * @return 회원 ID(Optional) - 존재하지 않으면 Optional.empty()
     */
    public Optional<Long> findPasswordByPhone(String loginId, String name, String phonenumber) {
        return memberRepository.findMemberIdByLoginIdAndNameAndPhonenumber(loginId, name, phonenumber);
    }

    /**
     * 아이디 찾기 완료 페이지에서 화면에 필요한 정보 조회
     *
     * @param memberId 회원 ID
     * @return 아이디 찾기 결과 DTO
     * @throws EntityNotFoundException 회원이 존재하지 않는 경우
     */
    @Transactional(readOnly = true)
    public FindIdResultDto findIdResult(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));
        return new FindIdResultDto(member);
    }


    /**
     * 임시 비밀번호 발급 후 메일 전송
     *
     * @param memberId 회원 ID
     * @return 임시 비밀번호 발급받은 회원 이메일
     * @throws EntityNotFoundException 회원이 존재하지 않는 경우
     */
    @Transactional
    public String issueTempPassword(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));

        String tempPassword = createTempPassword();
        member.updatePassword(passwordEncoder.encode(tempPassword));

        eventPublisher.publishEvent(new TempPasswordIssuedEvent(member.getEmail(), member.getName(), tempPassword));

        return member.getEmail();
    }


    /**
     * 임시 비밀번호 생성
     * <p>
     * 영문 대문자와 숫자로 구성된 10자리 임시 비밀번호를 생성합니다.
     *
     * @return 임시 비밀번호
     */
    private String createTempPassword() {
        int len = 10;
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(PASSWORD_CHARS[SECURE_RANDOM.nextInt(PASSWORD_CHARS.length)]);
        }
        return sb.toString();
    }
}
