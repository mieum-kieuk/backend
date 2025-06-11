package archivegarden.shop.service.admin.admin;

import archivegarden.shop.dto.admin.AdminSearchCondition;
import archivegarden.shop.dto.admin.admin.AdminListDto;
import archivegarden.shop.dto.admin.admin.JoinAdminForm;
import archivegarden.shop.dto.common.JoinSuccessDto;
import archivegarden.shop.entity.Admin;
import archivegarden.shop.exception.ajax.AjaxEntityNotFoundException;
import archivegarden.shop.exception.common.DuplicateEntityException;
import archivegarden.shop.exception.common.EntityNotFoundException;
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

    @DisplayName("관리자 회원가입 - 성공")
    @Test
    void join_Success() {
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

    @DisplayName("관리자 회원가입 완료 정보 조회 - 성공")
    @Test
    void getJoinSuccessInfo_Success() {
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

    @DisplayName("관리자 회원가입 완료 정보 조회 - 존재하지 않는 관리자")
    @Test
    void getJoinSuccessInfo_NotFound() {
        //given
        given(adminRepository.findById(anyLong())).willReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> adminService.getJoinSuccessInfo(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("존재하지 않는 관리자입니다.");

        verify(adminRepository, times(1)).findById(1L);
    }

    @DisplayName("관리자 중복 검사 - 중복 없음")
    @Test
    void checkAdminDuplicate_NoDuplicate() {
        //given
        JoinAdminForm form = createValidJoinForm();
        given(adminRepository.findDuplicateAdmin(anyString(), anyString())).willReturn(Optional.empty());

        //when
        adminService.checkAdminDuplicate(form);

        //then
        verify(adminRepository, times(1)).findDuplicateAdmin("test1", "test@example.com");
    }

    @DisplayName("관리자 중복 검사 - 중복 발생")
    @Test
    void checkAdminDuplicate_Duplicate() {
        //given
        JoinAdminForm form = createValidJoinForm();
        given(adminRepository.findDuplicateAdmin(anyString(), anyString())).willReturn(Optional.of(mock(Admin.class)));

        //when & then
        assertThatThrownBy(() -> adminService.checkAdminDuplicate(form))
                .isInstanceOf(DuplicateEntityException.class)
                .hasMessage("이미 존재하는 관리자입니다.");

        verify(adminRepository, times(1)).findDuplicateAdmin("test1", "test@example.com");
    }

    @DisplayName("로그인 아이디 사용 가능 여부 확인 - 사용 가능")
    @Test
    void isLoginIdAvailable_True() {
        //given
        given(adminRepository.findByLoginId("availableId")).willReturn(Optional.empty());

        //when
        boolean result = adminService.isLoginIdAvailable("availableId");

        //then
        assertThat(result).isTrue();
        verify(adminRepository, times(1)).findByLoginId("availableId");
    }

    @DisplayName("로그인 아이디 사용 가능 여부 확인 - 사용 불가능")
    @Test
    void isLoginIdAvailable_False() {
        //given
        given(adminRepository.findByLoginId("duplicateId")).willReturn(Optional.of(mock(Admin.class)));

        //when
        boolean result = adminService.isLoginIdAvailable("duplicateId");

        //then
        assertThat(result).isFalse();
        verify(adminRepository, times(1)).findByLoginId("duplicateId");
    }

    @DisplayName("이메일 사용 가능 여부 확인 - 사용 가능")
    @Test
    void isEmailAvailable_True() {
        //given
        given(adminRepository.findByEmail("available@example.com")).willReturn(Optional.empty());

        //when
        boolean result = adminService.isEmailAvailable("available@example.com");

        //then
        assertThat(result).isTrue();
        verify(adminRepository, times(1)).findByEmail("available@example.com");
    }

    @DisplayName("이메일 사용 가능 여부 확인 - 사용 불가능")
    @Test
    void isEmailAvailable_False() {
        //given
        given(adminRepository.findByEmail("duplicate@example.com")).willReturn(Optional.of(mock(Admin.class)));

        //when
        boolean result = adminService.isEmailAvailable("duplicate@example.com");

        //then
        assertThat(result).isFalse();
        verify(adminRepository, times(1)).findByEmail("duplicate@example.com");
    }

    @DisplayName("관리자 목록 조회 - 성공")
    @Test
    void getAdmins_Success() {
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

    @DisplayName("관리자 단건 삭제 - 성공")
    @Test
    void deleteAdmin_Success() {
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

    @DisplayName("관리자 단건 삭제 - 존재하지 않는 관리자")
    @Test
    void deleteAdmin_NotFound() {
        //given
        given(adminRepository.findById(anyLong())).willReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> adminService.deleteAdmin(1L))
                .isInstanceOf(AjaxEntityNotFoundException.class)
                .hasMessage("존재하지 않는 관리자입니다.");

        verify(adminRepository, times(1)).findById(1L);
        verify(adminRepository, never()).delete(any(Admin.class));
    }

    @DisplayName("관리자 권한 부여 - 성공")
    @Test
    void authorizeAdmin_Success() {
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

    @DisplayName("관리자 권한 부여 - 존재하지 않는 관리자")
    @Test
    void authorizeAdmin_NotFound() {
        //given
        given(adminRepository.findById(anyLong())).willReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> adminService.authorizeAdmin(1L))
                .isInstanceOf(AjaxEntityNotFoundException.class)
                .hasMessage("존재하지 않는 관리자입니다.");

        verify(adminRepository, times(1)).findById(1L);
        verify(emailService, never()).sendAdminAuthComplete(anyString(), anyString());
    }
}