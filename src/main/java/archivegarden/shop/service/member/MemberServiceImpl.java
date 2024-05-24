package archivegarden.shop.service.member;

import archivegarden.shop.dto.member.*;
import archivegarden.shop.entity.Delivery;
import archivegarden.shop.entity.FindAccountType;
import archivegarden.shop.entity.Member;
import archivegarden.shop.repository.DeliveryRepository;
import archivegarden.shop.repository.MemberRepository;
import archivegarden.shop.service.email.EmailService;
import archivegarden.shop.util.RedisUtil;
import archivegarden.shop.util.SmsUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final SmsUtil smsUtil;
    private final RedisUtil redisUtil;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final DeliveryRepository deliveryRepository;

    /**
     * 회원가입
     */
    @Transactional
    @Override
    public Long join(MemberSaveForm form) {

        //중복 회원 검증
        validateDuplicateMember(form);

        //비밀번호 암호화
        encodePassword(form);

        //멤버 생성
        Member member = Member.createMember(form);

        //멤버 저장
        memberRepository.save(member);

        //배송지 생성
        Delivery delivery = Delivery.createDeliveryWhenJoin(member, form.getZipCode(), form.getBasicAddress(), form.getDetailAddress());

        //배송지 저장
        deliveryRepository.save(delivery);

        //인증 이메일 전송
        emailService.sendValidationRequestEmail(member.getEmail(), member.getCreatedAt());

        return member.getId();
    }

    /**
     * 비밀번호 암호화
     */
    private void encodePassword(MemberSaveForm form) {
        String encodedPassword = passwordEncoder.encode(form.getPassword());
        form.setPassword(encodedPassword);
    }

    /**
     * 중복 회원 검증
     *
     * @throws IllegalStateException 이미 존재하는 회원일 경우
     */
    private void validateDuplicateMember(MemberSaveForm form) {
        String phonenumber = form.getPhonenumber1() + form.getPhonenumber2() + form.getPhonenumber3();
        memberRepository.findDuplicateMember(form.getLoginId(), phonenumber, form.getEmail())
                .ifPresent(m -> {
                    throw new IllegalStateException("이미 존재하는 회원입니다.");
                });
    }

    /**
     * 사용 가능한 아이디 -> true
     * 이미 존재하는 아이디 -> false
     */
    @Override
    public boolean isAvailableLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId).isEmpty();
    }

    /**
     * 사용 가능한 이메일 -> true
     * 이미 존재하는 이메일 -> false
     */
    @Override
    public boolean isAvailableEmail(String email) {
        return memberRepository.findByEmail(email).isEmpty();
    }

    /**
     * 사용 가능한 핸드폰 번호 -> true
     * 이미 존재하는 핸드폰 번호 -> false
     */
    @Override
    public boolean isAvailablePhonenumber(String phonenumber) {
        return memberRepository.findByPhonenumber(phonenumber).isEmpty();
    }

    /**
     * 인증코드 전송
     */
    @Override
    public void sendVerificationNo(String to) {
        if (redisUtil.existData(to)) {
            redisUtil.deleteData(to);
        }

        String verificationNo = createVerificationNo();
        log.info("verificationNo={}", verificationNo);

//        smsUtil.sendVerificationNo(to, verificationNo);

        redisUtil.setDataExpire(to, verificationNo, 60 * 3L);
    }

    /**
     * 인증코드 검증
     */
    @Override
    public boolean validateVerificationNo(VerificationRequestDto requestDto) {
        String phonenumber = requestDto.getPhonenumber1() + requestDto.getPhonenumber2() + requestDto.getPhonenumber3();

        if (redisUtil.existData(phonenumber)) {
            return redisUtil.getData(phonenumber).equals(requestDto.getVerificationNo());
        }

        return false;
    }

    /**
     * 회원 가입 완료페이지에서 필요한 정보 조회
     */
    @Override
    public NewMemberInfo getNewMemberInfo(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));
        return new NewMemberInfo(member);
    }

    /**
     * 아이디 찾기
     */
    @Override
    public Optional<FindIdResultDto> findId(FindIdForm form) {
        if (form.getFindType() == FindAccountType.EMAIL) {
            return memberRepository.findLoginIdByEmail(form.getName(), form.getEmail()).map(FindIdResultDto::new);
        } else {
            String phonenumber = form.getPhonenumber1() + "-" + form.getPhonenumber2() + "-" + form.getPhonenumber3();
            return memberRepository.findLoginIdByPhonenumber(form.getName(), phonenumber).map(FindIdResultDto::new);
        }
    }

    /**
     * 비밀번호 찾기
     *
     * @return 회원 이메일 또는 휴대전화번호
     */
    @Override
    public String findPassword(FindPasswordForm form) {
        if (form.getFindType() == FindAccountType.EMAIL) {
            return memberRepository.findPasswordByEmail(form.getLoginId(), form.getName(), form.getEmail());
        } else {
            String phonenumber = form.getPhonenumber1() + "-" + form.getPhonenumber2() + "-" + form.getPhonenumber3();
            return memberRepository.findPasswordByPhonenumber(form.getLoginId(), form.getName(), phonenumber);
        }
    }

    /**
     * 111111 ~ 999999 범위의  인증번호 생성
     */
    private String createVerificationNo() {
        Random random = new Random();
        int verificationNo = random.nextInt(888888) + 111111;
        return String.valueOf(verificationNo);
    }
}
