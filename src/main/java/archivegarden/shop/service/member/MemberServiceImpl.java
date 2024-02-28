package archivegarden.shop.service;

import archivegarden.shop.entity.Member;
import archivegarden.shop.repository.MemberRepository;
import archivegarden.shop.dto.member.MemberSaveDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Long join(MemberSaveDto dto) {
        encodePassword(dto);    //비밀번호 암호화
        Member member = new Member(dto);
        memberRepository.save(member);
        return member.getId();
    }

    /**
     * 사용 가능한 아이디 -> true
     * 이미 존재하는 아이디 -> false
     */
    @Override
    public boolean duplicateLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId).isEmpty();
    }

    /**
     * 사용 가능한 이메일 -> true
     * 이미 존재하는 이메일 -> false
     */
    @Override
    public boolean duplicateEmail(String email) {
        return memberRepository.findByEmail(email).isEmpty();
    }

    /**
     * 사용 가능한 핸드폰 번호 -> true
     * 이미 존재하는 핸드폰 번호 -> false
     */
    @Override
    public boolean duplicatePhonenumber(String phonenumber) {
        return memberRepository.findByPhonenumber(phonenumber).isEmpty();
    }

    private void encodePassword(MemberSaveDto dto) {
        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        dto.setPassword(encodedPassword);
    }
}
