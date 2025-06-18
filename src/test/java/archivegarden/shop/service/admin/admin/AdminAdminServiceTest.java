package archivegarden.shop.service.admin.admin;

import archivegarden.shop.dto.admin.AdminSearchCondition;
import archivegarden.shop.dto.admin.admin.AdminListDto;
import archivegarden.shop.dto.admin.admin.JoinAdminForm;
import archivegarden.shop.dto.common.JoinSuccessDto;
import archivegarden.shop.entity.Admin;
import archivegarden.shop.exception.ajax.EntityNotFoundAjaxException;
import archivegarden.shop.exception.global.DuplicateEntityException;
import archivegarden.shop.exception.global.EmailSendFailedException;
import archivegarden.shop.exception.global.EntityNotFoundException;
import archivegarden.shop.repository.admin.AdminAdminRepository;
import archivegarden.shop.service.admin.email.AdminEmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@DisplayName("AdminAdminService 단위 테스트")
@ExtendWith(MockitoExtension.class)
class AdminAdminServiceTest {

    @InjectMocks
    private AdminAdminService adminService;

    @Mock
    private AdminEmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AdminAdminRepository adminRepository;

    private JoinAdminForm createValidJoinForm() {
        JoinAdminForm form = new JoinAdminForm();
        form.setLoginId("test1");
        form.setPassword("test1234!");
        form.setPasswordConfirm("test1234!");
        form.setName("테스터");
        form.setEmail("test@example.com");
        return form;
    }

    @DisplayName("회원가입 - 유효한 정보로 가입 시 관리자 저장")
    @Test
    void 회원가입_성공_관리자저장() {
        //given
        JoinAdminForm form = createValidJoinForm();

        given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");

        doAnswer(invocation -> {
            Admin arg = invocation.getArgument(0);
            ReflectionTestUtils.setField(arg, "id", 1L);
            return arg;

        }).when(adminRepository).save(any(Admin.class));

        //when
        Long savedAdminId = adminService.join(form);

        //then
        assertThat(savedAdminId).isEqualTo(1L);
        verify(passwordEncoder, times(1)).encode("test1234!");
        verify(adminRepository, times(1)).save(any(Admin.class));
    }

    @DisplayName("회원가입 완료 정보 조회 - 성공")
    @Test
    void 회원가입완료정보조회_성공() {
        //given
        JoinAdminForm form = createValidJoinForm();
        Admin admin = Admin.createAdmin(form);

        given(adminRepository.findById(1L)).willReturn(Optional.of(admin));

        //when
        JoinSuccessDto dto = adminService.getJoinSuccessInfo(1L);

        //then
        assertThat(dto.getLoginId()).isEqualTo("test1");
        assertThat(dto.getName()).isEqualTo("테스터");
        assertThat(dto.getEmail()).isEqualTo("test@example.com");
        verify(adminRepository, times(1)).findById(1L);
    }

    @DisplayName("회원가입 완료 정보 조회 - 존재하지 않는 관리자ID로 조회 시 예외 발생")
    @Test
    void 회원가입완료정보조회_실패_관리자없음() {
        //given
        given(adminRepository.findById(anyLong())).willReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> adminService.getJoinSuccessInfo(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("존재하지 않는 관리자입니다.");

        verify(adminRepository, times(1)).findById(1L);
    }

    @DisplayName("회원가입 중복 검사 - 중복 없음")
    @Test
    void 중복검사_성공_중복없음() {
        //given
        JoinAdminForm form = createValidJoinForm();
        given(adminRepository.findDuplicateAdmin(anyString(), anyString())).willReturn(Optional.empty());

        //when
        adminService.checkAdminDuplicate(form);

        //then
        verify(adminRepository, times(1)).findDuplicateAdmin("test1", "test@example.com");
    }

    @DisplayName("회원가입 중복 검사 - 중복된 관리자 존재할 경우 예외 발생")
    @Test
    void 중복검사_실패_중복발생() {
        //given
        JoinAdminForm form = createValidJoinForm();
        given(adminRepository.findDuplicateAdmin(anyString(), anyString())).willReturn(Optional.of(mock(Admin.class)));

        //when & then
        assertThatThrownBy(() -> adminService.checkAdminDuplicate(form))
                .isInstanceOf(DuplicateEntityException.class)
                .hasMessage("이미 존재하는 관리자입니다.");

        verify(adminRepository, times(1)).findDuplicateAdmin("test1", "test@example.com");
    }

    @DisplayName("로그인 아이디 사용 가능 여부 확인 - 사용 가능한 로그인 아이디")
    @Test
    void 로그인아이디사용가능여부_성공() {
        //given
        given(adminRepository.findByLoginId("availableId")).willReturn(Optional.empty());

        //when
        boolean result = adminService.isLoginIdAvailable("availableId");

        //then
        assertThat(result).isTrue();
        verify(adminRepository, times(1)).findByLoginId("availableId");
    }

    @DisplayName("로그인 아이디 사용 가능 여부 확인 - 중복된 로그인 아이디")
    @Test
    void 로그인아이디사용가능여부_실() {
        //given
        given(adminRepository.findByLoginId("duplicateId")).willReturn(Optional.of(mock(Admin.class)));

        //when
        boolean result = adminService.isLoginIdAvailable("duplicateId");

        //then
        assertThat(result).isFalse();
        verify(adminRepository, times(1)).findByLoginId("duplicateId");
    }

    @DisplayName("이메일 사용 가능 여부 확인 - 사용 가능한 이메일")
    @Test
    void 이메일사용가능여부_성공() {
        //given
        given(adminRepository.findByEmail("available@example.com")).willReturn(Optional.empty());

        //when
        boolean result = adminService.isEmailAvailable("available@example.com");

        //then
        assertThat(result).isTrue();
        verify(adminRepository, times(1)).findByEmail("available@example.com");
    }

    @DisplayName("이메일 사용 가능 여부 확인 - 중복된 이메일")
    @Test
    void 이메일사용가능여부_실패() {
        //given
        given(adminRepository.findByEmail("duplicate@example.com")).willReturn(Optional.of(mock(Admin.class)));

        //when
        boolean result = adminService.isEmailAvailable("duplicate@example.com");

        //then
        assertThat(result).isFalse();
        verify(adminRepository, times(1)).findByEmail("duplicate@example.com");
    }

    @DisplayName("관리자 목록 조회 - 유효한 검색 조건으로 조회 시 검색 결과 반환")
    @Test
    void 관리자목록조회_성공() {
        AdminSearchCondition condition = new AdminSearchCondition();
        PageRequest pageable = PageRequest.of(0, 10);
        AdminListDto admin1 = new AdminListDto(1L, "관리자1", "admin1", "admin1@mail.com", true, LocalDateTime.now());
        PageImpl<AdminListDto> adminPage = new PageImpl<>(List.of(admin1), pageable, 1);

        given(adminRepository.findAdmins(condition, pageable)).willReturn(adminPage);

        //when
        Page<AdminListDto> resultPage = adminService.getAdmins(condition, pageable);

        //then
        assertThat(resultPage).isEqualTo(adminPage);
        verify(adminRepository, times(1)).findAdmins(condition, pageable);
    }

    @DisplayName("관리자 삭제 - 성공")
    @Test
    void 관리자삭제_성공() {
        //given
        JoinAdminForm form = createValidJoinForm();
        Admin admin = Admin.createAdmin(form);

        given(adminRepository.findById(1L)).willReturn(Optional.of(admin));
        doNothing().when(adminRepository).delete(admin);

        //when
        adminService.deleteAdmin(1L);

        //then
        verify(adminRepository, times(1)).findById(1L);
        verify(adminRepository, times(1)).delete(admin);
    }

    @DisplayName("관리자 삭제 - 존재하지 않는 관리자 삭제 시 예외 발생")
    @Test
    void 관리자삭제_실패_존재하지않음() {
        //given
        given(adminRepository.findById(anyLong())).willReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> adminService.deleteAdmin(1L))
                .isInstanceOf(EntityNotFoundAjaxException.class)
                .hasMessage("존재하지 않는 관리자입니다.");

        verify(adminRepository, times(1)).findById(1L);
        verify(adminRepository, never()).delete(any(Admin.class));
    }

    @DisplayName("관리자 권한 부여 - 성공")
    @Test
    void 관리자권한부여_성공() {
        //given
        JoinAdminForm form = createValidJoinForm();
        Admin admin = Admin.createAdmin(form);

        given(adminRepository.findById(1L)).willReturn(Optional.of(admin));
        doNothing().when(emailService).sendAdminAuthComplete(anyString(), anyString());

        //when
        adminService.authorizeAdmin(1L);

        //then
        assertThat(admin.isAuthorized()).isTrue();
        verify(adminRepository, times(1)).findById(1L);
        verify(emailService, times(1)).sendAdminAuthComplete("test@example.com", "테스터");
    }

    @DisplayName("관리자 권한 부여 - 존재하지 않는 관리자ID일 경우 예외 발생")
    @Test
    void 관리자권한부여_실패_존재하지않음() {
        //given
        given(adminRepository.findById(anyLong())).willReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> adminService.authorizeAdmin(1L))
                .isInstanceOf(EntityNotFoundAjaxException.class)
                .hasMessage("존재하지 않는 관리자입니다.");

        verify(adminRepository, times(1)).findById(1L);
        verify(emailService, never()).sendAdminAuthComplete(anyString(), anyString());
    }

    @DisplayName("관리자 권한 부여 - 이메일 전송 실패 시 EmailSendFailedException 발생")
    @Test
    void 관리자권한부여_이메일전송실패_예외발생() {
        // given
        Long adminId = 1L;
        Admin admin = mock(Admin.class);
        given(admin.getEmail()).willReturn("test@example.com");
        given(admin.getName()).willReturn("테스터");

        given(adminRepository.findById(adminId)).willReturn(Optional.of(admin));

        doThrow(new EmailSendFailedException("관리자 인증 메일 전송에 실패했습니다.")).when(emailService).sendAdminAuthComplete(anyString(), anyString());

        // when & then
        assertThatThrownBy(() -> adminService.authorizeAdmin(adminId))
                .isInstanceOf(EmailSendFailedException.class)
                .hasMessage("관리자 인증 메일 전송에 실패했습니다.");

        verify(adminRepository).findById(adminId);
        verify(emailService).sendAdminAuthComplete(anyString(), anyString());
    }
}