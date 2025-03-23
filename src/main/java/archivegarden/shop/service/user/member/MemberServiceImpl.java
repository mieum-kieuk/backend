package archivegarden.shop.service.user.member;

import archivegarden.shop.dto.common.JoinSuccessDto;
import archivegarden.shop.dto.user.member.FindIdResultDto;
import archivegarden.shop.dto.user.member.JoinMemberForm;
import archivegarden.shop.dto.user.member.EditMemberInfoForm;
import archivegarden.shop.dto.user.member.VerificationRequestDto;
import archivegarden.shop.entity.Delivery;
import archivegarden.shop.entity.Member;
import archivegarden.shop.entity.Membership;
import archivegarden.shop.entity.SavedPointType;
import archivegarden.shop.exception.NotFoundException;
import archivegarden.shop.exception.common.DuplicateEntityException;
import archivegarden.shop.exception.common.EntityNotFoundException;
import archivegarden.shop.repository.DeliveryRepository;
import archivegarden.shop.repository.member.MemberRepository;
import archivegarden.shop.repository.membership.MembershipRepository;
import archivegarden.shop.service.point.SavedPointService;
import archivegarden.shop.service.user.email.EmailService;
import archivegarden.shop.util.RedisUtil;
import archivegarden.shop.util.SmsUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
    public Long join(JoinMemberForm form) {
        form.setPassword(encodePassword(form.getPassword()));

        Membership membership = membershipRepository.findDefaultLevel();
        Member member = Member.createMember(form, membership);
        memberRepository.save(member);

        Delivery delivery = Delivery.createDeliveryWhenJoin(member, form.getZipCode(), form.getBasicAddress(), form.getDetailAddress());
        deliveryRepository.save(delivery);

        emailService.sendValidationRequestEmail(member.getEmail(), member.getName(), member.getCreatedAt());

        savedPointService.addPoint(member.getId(), SavedPointType.JOIN, 1000);  //회원가입 축하 적립금 지급

        return member.getId();
    }

    /**
     * 중복 회원 검사
     *
     * @throws DuplicateEntityException
     */
    @Override
    public void checkMemberDuplicate(JoinMemberForm form) {
        String phonenumber = form.getPhonenumber1() + form.getPhonenumber2() + form.getPhonenumber3();
        memberRepository.findDuplicateMember(form.getLoginId(), phonenumber, form.getEmail())
                .ifPresent(m -> {
                    throw new DuplicateEntityException("이미 존재하는 회원입니다.");
                });
    }

    /**
     * 회원가입 완료 페이지에서 필요한 정보 조회
     *
     * @throws EntityNotFoundException
     */
    @Override
    public JoinSuccessDto joinComplete(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));
        return new JoinSuccessDto(member.getLoginId(), member.getName(), member.getEmail());
    }

    /**
     * 로그인 아이디 중복 검사
     */
    @Override
    public boolean isAvailableLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId).isEmpty();
    }

    /**
     * 이메일 중복 검사
     */
    @Override
    public boolean isAvailableEmail(String email) {
        return memberRepository.findByEmail(email).isEmpty();
    }

    /**
     * 핸드폰 중복 검사
     */
    @Override
    public boolean isAvailablePhonenumber(String phonenumber) {
        return memberRepository.findByPhonenumber(phonenumber).isEmpty();
    }

    /**
     * 비밀번호 중복 검사
     */
    @Override
    public boolean isNewPassword (String newPassword, String nowPassword) {
        return !passwordEncoder.matches(newPassword, nowPassword);
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
     * 회원 정보 수정 전 본인 확인
     */
    @Override
    public boolean validateIdentity(Member member, String password) {
        return passwordEncoder.matches(password, member.getPassword());
    }

    /**
     * 회원 정보 수정 폼 조회
     */
    @Override
    public EditMemberInfoForm getMemberInfo(Long memberId) {
        return memberRepository.findByIdWithDefaultDelivery(memberId);
    }

    /**
     * 회원 정보 수정
     */
    @Override
    public void editMemberInfo(Long memberId, EditMemberInfoForm form) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));

        if(StringUtils.hasText(form.getNewPassword())) {
            String encodedNewPassword = encodePassword(form.getNewPassword());
            member.updatePassword(encodedNewPassword);
        }

        if(!member.getEmail().equals(form.getEmail())) {
            member.updateEmail(form.getEmail());
            member.updateEmailVerificationStatus(false);
            emailService.sendValidationRequestEmailInMyPage(form.getEmail(), member.getName());
        }
    }

    /**
     * 비밀번호 암호화
     */
    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
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
