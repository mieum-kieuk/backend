package archivegarden.shop.service.member;

import archivegarden.shop.dto.admin.member.MemberListDto;
import archivegarden.shop.dto.user.member.*;
import archivegarden.shop.entity.Delivery;
import archivegarden.shop.entity.Member;
import archivegarden.shop.entity.Membership;
import archivegarden.shop.entity.SavedPointType;
import archivegarden.shop.exception.NotFoundException;
import archivegarden.shop.repository.DeliveryRepository;
import archivegarden.shop.repository.member.MemberRepository;
import archivegarden.shop.repository.member.MembershipRepository;
import archivegarden.shop.service.email.EmailService;
import archivegarden.shop.service.point.SavedPointService;
import archivegarden.shop.util.RedisUtil;
import archivegarden.shop.util.SmsUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final SmsUtil smsUtil;
    private final RedisUtil redisUtil;
    private final EmailService emailService;
    private final SavedPointService savedPointService;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final DeliveryRepository deliveryRepository;
    private final MembershipRepository membershipRepository;

    /**
     * 회원가입
     */
    @Transactional
    @Override
    public Long join(AddMemberForm form) {

        //중복 회원 검증
        validateDuplicateMember(form);

        //비밀번호 암호화
        encodePassword(form);

        //Member 생성
        Membership membership = membershipRepository.findByLevel("WHITE");
        Member member = Member.createMember(form, membership);
        memberRepository.save(member);

        //Delivery 생성
        Delivery delivery = Delivery.createDeliveryWhenJoin(member, form.getZipCode(), form.getBasicAddress(), form.getDetailAddress());
        deliveryRepository.save(delivery);

        //인증 이메일 전송
        emailService.sendValidationRequestEmail(member.getEmail(), member.getCreatedAt());

        //1000원 회원가입 축하 적립금 지급
        savedPointService.addPoint(member.getId(), SavedPointType.JOIN, 1000);

        return member.getId();
    }

    /**
     * 회원 가입 완료페이지에서 필요한 정보 조회
     *
     * @throws NotFoundException
     */
    @Override
    public MemberJoinInfoDto joinComplete(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));
        return new MemberJoinInfoDto(member.getLoginId(), member.getName(), member.getEmail());
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
    @Transactional
    @Override
    public void sendVerificationNo(String to) {
        if (redisUtil.existData(to)) {
            redisUtil.deleteData(to);
        }

        String verificationNo = createVerificationNo();
        log.info("휴대전화번호 인증번호: {}", verificationNo);

//        smsUtil.sendVerificationNo(to, verificationNo);

        redisUtil.setDataExpire(to, verificationNo, 60 * 3L);
    }

    /**
     * 인증코드 검증
     */
    @Override
    public boolean validateVerificationNo(VerificationRequestDto requestDto) {
        String phonenumber = requestDto.getPhonenumber();

        if (redisUtil.existData(phonenumber)) {
            return redisUtil.getData(phonenumber).equals(requestDto.getVerificationNo());
        }

        return false;
    }

    /**
     * 이메일 통해 아이디 존재하는지 확인
     */
    @Override
    public Long checkLoginIdExistsByEmail(String name, String email) {
        return memberRepository.findLoginIdByEmail(name, email);
    }

    /**
     * 휴대전화번호 통해 아이디 존재하는지 확인
     */
    @Override
    public Long checkIdExistsByPhonenumber(String name, String phonenumber) {
        return memberRepository.findLoginIdByPhonenumber(name, phonenumber);
    }

    /**
     * 아이디 찾기 결과 페이지에서 필요한 정보 조회
     *
     * @throws NotFoundException
     */
    @Override
    public FindIdResultDto findIdComplete(Long memberId) {
        //Member 조회
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));

        return new FindIdResultDto(member);
    }

    /**
     * 이메일 통해 비밀번호 존재하는지 확인
     */
    @Override
    public String checkPasswordExistsByEmail(String loginId, String name, String email) {
        return memberRepository.findPasswordByEmail(loginId, name, email);
    }

    /**
     * 휴대전화번호를 통해 비밀번호 존재하는지 확인
     */
    @Override
    public String checkPasswordExistsByPhonenumber(String loginId, String name, String phonenumber) {
        return memberRepository.findPasswordByPhonenumber(loginId, name, phonenumber);
    }

    /**
     * 마이페이지 - 회원 정보 수정 로그인
     */
    @Override
    public boolean mypageInfoLogin(Member member, String password) {
        return passwordEncoder.matches(password, member.getPassword());
    }

    /**
     * 마이페이지 - 회원 정보 수정 폼 조회
     *
     * @throws NotFoundException
     */
    @Override
    public MemberInfo getMemberInfo(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));
        return new MemberInfo(member);
    }

    /**
     * 관리자 페이지 홈
     *
     * @return
     */
    @Override
    public List<MemberListDto> getLatestJoinMembers() {
//        memberRepository.findLatestJoinMemberDtos()
        return null;
    }

    /**
     * 중복 회원 검증
     *
     * @throws IllegalStateException 이미 존재하는 회원일 경우
     */
    private void validateDuplicateMember(AddMemberForm form) {
        String phonenumber = form.getPhonenumber1() + form.getPhonenumber2() + form.getPhonenumber3();
        memberRepository.findDuplicateMember(form.getLoginId(), phonenumber, form.getEmail())
                .ifPresent(m -> {
                    throw new IllegalStateException("이미 존재하는 회원입니다.");
                });
    }

    /**
     * 비밀번호 암호화
     */
    private void encodePassword(AddMemberForm form) {
        String encodedPassword = passwordEncoder.encode(form.getPassword());
        form.setPassword(encodedPassword);
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
