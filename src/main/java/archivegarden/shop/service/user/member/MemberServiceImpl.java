package archivegarden.shop.service.user.member;

import archivegarden.shop.dto.ResultResponse;
import archivegarden.shop.dto.common.JoinSuccessDto;
import archivegarden.shop.dto.user.member.*;
import archivegarden.shop.entity.Delivery;
import archivegarden.shop.entity.Member;
import archivegarden.shop.entity.Membership;
import archivegarden.shop.entity.SavedPointType;
import archivegarden.shop.exception.global.DuplicateEntityException;
import archivegarden.shop.exception.global.EntityNotFoundException;
import archivegarden.shop.repository.DeliveryRepository;
import archivegarden.shop.repository.member.MemberRepository;
import archivegarden.shop.repository.membership.MembershipRepository;
import archivegarden.shop.service.point.SavedPointService;
import archivegarden.shop.service.user.email.EmailService;
import archivegarden.shop.util.RedisUtil;
import archivegarden.shop.util.SmsUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
    private final SavedPointService savedPointService;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final DeliveryRepository deliveryRepository;
    private final MembershipRepository membershipRepository;

    private static final long VERIFICATION_CODE_EXPIRE_SECONDS = 60 * 3L;

    /**
     * 회원가입
     *
     * @param form 회원가입 폼 DTO
     * @return 저장된 회원 ID
     */
    @Transactional
    @Override
    public Long join(JoinMemberForm form) {
        form.setPassword(encodePassword(form.getPassword()));

        Membership membership = membershipRepository.findDefault();

        Member member = Member.createMember(form, membership);
        memberRepository.save(member);

        Delivery delivery = Delivery.createDeliveryWhenJoin(member, form.getZipCode(), form.getBasicAddress(), form.getDetailAddress());
        deliveryRepository.save(delivery);

        emailService.sendValidationRequestEmail(member.getEmail(), member.getName(), member.getCreatedAt());

        savedPointService.addPoint(member.getId(), SavedPointType.JOIN, 1000);

        return member.getId();
    }

    /**
     * 회원 중복 검사
     *
     * 로그인 아이디, 휴대전화번호, 이메일 중 하나라도 기존 회원과 중복되는지 확인합니다.
     *
     * @param form 회원가입 폼 DTO
     * @throws DuplicateEntityException 존재하지 않는 회원일 경우
     */
    @Override
    public void checkMemberDuplicate(JoinMemberForm form) {
        String phonenumber = form.getFormattedPhonenumber();
        memberRepository.findDuplicateMember(form.getLoginId(), phonenumber, form.getEmail())
                .ifPresent(m -> {
                    throw new DuplicateEntityException("이미 존재하는 회원입니다.");
                });
    }

    /**
     * 회원가입 완료 페이지에서 필요한 회원 정보 조회
     *
     * @param memberId 회원 ID
     * @return 회원가입 완료 정보 DTO
     * @throws EntityNotFoundException 회원이 존재하지 않는 경우
     */
    @Override
    public JoinSuccessDto joinComplete(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));
        return new JoinSuccessDto(member.getLoginId(), member.getName(), member.getEmail());
    }

    /**
     * 로그인 아이디 사용 가능 여부 검사
     *
     * @param loginId 로그인 아이디
     * @return 사용 가능하면 true, 이미 존재하면 false
     */
    @Override
    public boolean isAvailableLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId).isEmpty();
    }

    /**
     * 이메일 사용 가능 여부 검사
     *
     * @param email 이메일
     * @return 사용 가능하면 true, 이미 존재하면 false
     */
    @Override
    public boolean isAvailableEmail(String email) {
        return memberRepository.findByEmail(email).isEmpty();
    }

    /**
     * 휴대전화번호 사용 가능 여부 검사
     *
     * @param phonenumber 휴대전화번호
     * @return 사용 가능하면 true, 이미 존재하면 false
     */
    @Override
    public boolean isAvailablePhonenumber(String phonenumber) {
        return memberRepository.findByPhonenumber(phonenumber).isEmpty();
    }

    /**
     * 휴대전화번호로 인증번호 발송
     *
     * @param to 휴대전화번호
     */
    @Transactional
    @Override
    public void sendVerificationCode(String to) {
        if (redisUtil.existData(to)) {
            redisUtil.deleteData(to);
        }

        String verificationCode = createVerificationCode();
        log.info("휴대전화번호 인증번호: {}", verificationCode);

//        smsUtil.sendVerificationNo(to, verificationCode);

        redisUtil.setDataExpire(to, verificationCode, VERIFICATION_CODE_EXPIRE_SECONDS);
    }

    /**
     * 휴대전화번호 인증번호 검증
     *
     * @param verificationCodeRequest 인증번호 검증 요청 DTO
     * @return API 응답 객체
     */
    @Override
    public ResultResponse verifyVerificationCode(VerificationCodeRequestDto verificationCodeRequest) {
        String phonenumber = verificationCodeRequest.getPhonenumber();

        if (redisUtil.existData(phonenumber)) {
            if(redisUtil.getData(phonenumber).equals(verificationCodeRequest.getVerificationCode())) {
                return new ResultResponse(HttpStatus.OK.value(), "인증번호 확인에 성공하였습니다.");
            } else {
                return new ResultResponse(HttpStatus.BAD_REQUEST.value(), "인증번호가 일치하지 않습니다.");
            }
        } else {
            return new ResultResponse(HttpStatus.BAD_REQUEST.value(), "인증번호가 만료되었습니다.");
        }
    }

    /**
     * 이메일로 아이디 찾기
     *
     * @param name  회원 이름
     * @param email 회원 이메일
     * @return 회원이 존재하면 회원 ID를 담은 Optional, 존재하지 않으면 Optional.empty()
     */
    @Override
    public Optional<Long> checkLoginIdExistsByEmail(String name, String email) {
        return memberRepository.findLoginIdByEmail(name, email);
    }

    /**
     * 휴대전화번호로 아이디 찾기
     *
     * @param name        회원 이름
     * @param phonenumber 회원 휴대전화번호
     * @return 회원이 존재하면 회원 ID를 담은 Optional, 존재하지 않으면 Optional.empty()
     */
    @Override
    public Optional<Long> checkLoginIdExistsByPhonenumber(String name, String phonenumber) {
        return memberRepository.findLoginIdByPhonenumber(name, phonenumber);
    }

    /**
     * 아이디 찾기 결과 페이지에서 필요한 정보 조회
     *
     * @param memberId 회원 ID
     * @return 아이디 찾기 결과 DTO
     * @throws EntityNotFoundException 회원이 존재하지 않는 경우
     */
    @Override
    public FindIdResultDto findIdComplete(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));
        return new FindIdResultDto(member);
    }

    /**
     * 이메일로 비밀번호 찾기
     *
     * @param loginId 회원 로그인 아이디
     * @param name    회원 이름
     * @param email   회원 이메일
     * @return 회원이 존재하면 회원 ID를 담은 Optional, 존재하지 않으면 Optional.empty()
     */
    @Override
    public Optional<String> checkPasswordExistsByEmail(String loginId, String name, String email) {
        return memberRepository.findPasswordByEmail(loginId, name, email);
    }

    /**
     * 휴대전화번호로 비밀번호 찾기
     *
     * @param loginId     회원 로그인 아이디
     * @param name        회원 이름
     * @param phonenumber 회원 휴대전화번호
     * @return 회원이 존재하면 회원 ID를 담은 Optional, 존재하지 않으면 Optional.empty()
     */
    @Override
    public Optional<String> checkPasswordExistsByPhonenumber(String loginId, String name, String phonenumber) {
        return memberRepository.findPasswordByPhonenumber(loginId, name, phonenumber);
    }

    /**
     * 회원 정보 수정 전, 본인 비밀번호가 맞는지 검증
     *
     * @param member   회원
     * @param password 입력받은 평문 비밀번호
     * @return 비밀번호가 일치하면 true, 아니면 false
     */
    @Override
    public boolean validateIdentity(Member member, String password) {
        return passwordEncoder.matches(password, member.getPassword());
    }

    /**
     * 회원 정보 수정 화면에 필요한 회원 정보를 조회합니다.
     *
     * @param memberId 회원 ID
     * @return 회원 정보 수정 폼 DTO
     * @throws EntityNotFoundException 회원이 존재하지 않는 경우
     */
    @Override
    public EditMemberInfoForm getMemberInfo(Long memberId) {
        return memberRepository.fetchEditMemberInfoForm(memberId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));
    }

    /**
     * 비밀번호가 기존과 다른지 검사
     *
     * @param newPassword 새 비밀번호
     * @param nowPassword 기존 비밀번호 (암호화된 값)
     * @return 기존 비밀번호와 다르면 true, 같으면 false
     */
    @Override
    public boolean isNewPassword(String newPassword, String nowPassword) {
        return !passwordEncoder.matches(newPassword, nowPassword);
    }

    /**
     * 회원 정보 수정
     *
     * @param memberId 회원 ID
     * @param form     회원 정보 수정 폼 DTO
     * @throws EntityNotFoundException 회원이 존재하지 않는 경우
     */
    @Override
    public void editMemberInfo(Long memberId, EditMemberInfoForm form) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));

        if (StringUtils.hasText(form.getNewPassword())) {
            String encodedNewPassword = encodePassword(form.getNewPassword());
            member.updatePassword(encodedNewPassword);
        }

        if (!member.getEmail().equals(form.getEmail())) {
            member.updateEmail(form.getEmail());
            member.updateEmailVerificationStatus(false);
            emailService.sendValidationRequestEmailInMyPage(form.getEmail(), member.getName());
        }
    }

    /**
     * 비밀번호 암호화
     *
     * @param password 평문 비밀번호
     * @return 암호화된 비밀번호
     */
    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    /**
     * 랜덤 인증번호를 생성
     *
     * 111111 ~ 999999 범위의 랜덤 인증번호를 생성합니다.
     *
     * @return 인증번호 문자열
     */
    private String createVerificationCode() {
        Random random = new Random();
        int verificationNo = random.nextInt(888888) + 111111;
        return String.valueOf(verificationNo);
    }
}
