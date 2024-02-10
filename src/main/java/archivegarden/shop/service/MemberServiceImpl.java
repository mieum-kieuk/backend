package archivegarden.shop.service;

import archivegarden.shop.entity.Member;
import archivegarden.shop.repository.MemberRepository;
import archivegarden.shop.web.form.MemberSaveDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private void encodePassword(MemberSaveDto dto) {
        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        dto.setPassword(encodedPassword);
    }
}
