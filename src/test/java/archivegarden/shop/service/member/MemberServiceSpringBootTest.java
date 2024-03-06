//package archivegarden.shop.service.member;
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@Transactional
//@SpringBootTest
//@AutoConfigureMockMvc
//class MemberServiceSpringBootTest {
//
//    @Autowired
//    MockMvc mockMvc;
//
//    @Autowired
//    MemberService memberService;
//
//    @Test
//    @DisplayName("회원가입 성공")
//    public void join() throws Exception {
//        //given
//        MemberSaveRequest form = new MemberSaveRequest("test1234", "test1234!@", "test1234!@", "테스터", null, null, null, "010", "1234", "5678", "123456", "test@gmail.com", true, true, true, true);
//
//        //when
//        mockMvc.perform(MockMvcRequestBuilders
//                        .post("/members/join")
//                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//                        .flashAttr("form", form))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/members/join/complete"));
//    }
//
//    @Test
//    @DisplayName("회원가입 실패: loginId_empty")
//    public void joinX_loginId1() throws Exception {
//        //given
//        MemberSaveRequest form = new MemberSaveRequest("", "test1234!@", "test1234!@", "테스터", null, null, null, "010", "1234", "5678", "123456", "test@gmail.com", true, true, true, true);
//
//        //when
//        mockMvc.perform(MockMvcRequestBuilders
//                        .post("/members/join")
//                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//                        .flashAttr("form", form))
//                .andExpect(model().errorCount(1))
//                .andExpect(model().attributeHasFieldErrorCode("form", "loginId", "Pattern"));
//    }
//
////    @Test
////    @DisplayName("회원가입 실패: loginId_blank")
////    public void joinX_loginId2() throws Exception {
////        //given
////        MemberSaveRequest form = new MemberSaveRequest("  ", "test1234!@", "test1234!@", "테스터", "010", "1234", "5678", "123456", "test@gmail.com", true, true, true, true);
////
////        //when
////        mockMvc.perform(MockMvcRequestBuilders
////                        .post("/members/join")
////                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
////                        .flashAttr("form", form))
////                .andExpect(model().errorCount(1))
////                .andExpect(model().attributeHasFieldErrorCode("form", "loginId", "Pattern"));
////    }
////
////    @Test
////    @DisplayName("회원가입 실패: loginId_정규식X-영문소문자O, 숫자X")
////    public void joinX_loginId3() throws Exception {
////        //given
////        MemberSaveRequest form = new MemberSaveRequest("testtest", "test1234!@", "test1234!@", "테스터", "010", "1234", "5678", "123456", "test@gmail.com", true, true, true, true);
////
////        //when
////        mockMvc.perform(MockMvcRequestBuilders
////                        .post("/members/join")
////                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
////                        .flashAttr("form", form))
////                .andExpect(model().errorCount(1))
////                .andExpect(model().attributeHasFieldErrorCode("form", "loginId", "Pattern"));
////    }
////
////    @Test
////    @DisplayName("회원가입 실패: loginId_정규식X-영문소문자X, 숫자O")
////    public void joinX_loginId4() throws Exception {
////        //given
////        MemberSaveRequest form = new MemberSaveRequest("12345678", "test1234!@", "test1234!@", "테스터", "010", "1234", "5678", "123456", "test@gmail.com", true, true, true, true);
////
////        //when
////        mockMvc.perform(MockMvcRequestBuilders
////                        .post("/members/join")
////                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
////                        .flashAttr("form", form))
////                .andExpect(model().errorCount(1))
////                .andExpect(model().attributeHasFieldErrorCode("form", "loginId", "Pattern"));
////    }
////
////    @Test
////    @DisplayName("회원가입 실패: loginId_정규식X-특수문자 포함")
////    public void joinX_loginId5() throws Exception {
////        //given
////        MemberSaveRequest form = new MemberSaveRequest("test1234!@", "test1234!@", "test1234!@", "테스터", "010", "1234", "5678", "123456", "test@gmail.com", true, true, true, true);
////
////        //when
////        mockMvc.perform(MockMvcRequestBuilders
////                        .post("/members/join")
////                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
////                        .flashAttr("form", form))
////                .andExpect(model().errorCount(1))
////                .andExpect(model().attributeHasFieldErrorCode("form", "loginId", "Pattern"));
////    }
////
////    @Test
////    @DisplayName("회원가입 실패: loginId_길이부족")
////    public void joinX_loginId6() throws Exception {
////        //given
////        MemberSaveRequest form = new MemberSaveRequest("te1", "test1234!@", "test1234!@", "테스터", "010", "1234", "5678", "123456", "test@gmail.com", true, true, true, true);
////
////        //when
////        mockMvc.perform(MockMvcRequestBuilders
////                        .post("/members/join")
////                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
////                        .flashAttr("form", form))
////                .andExpect(model().errorCount(1))
////                .andExpect(model().attributeHasFieldErrorCode("form", "loginId", "Pattern"));
////    }
////
////    @Test
////    @DisplayName("회원가입 실패: loginId_길이초과")
////    public void joinX_loginId7() throws Exception {
////        //given
////        MemberSaveRequest form = new MemberSaveRequest("testtesttesttesttest1234", "test1234!@", "test1234!@", "테스터", "010", "1234", "5678", "123456", "test@gmail.com", true, true, true, true);
////
////        //when
////        mockMvc.perform(MockMvcRequestBuilders
////                        .post("/members/join")
////                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
////                        .flashAttr("form", form))
////                .andExpect(model().errorCount(1))
////                .andExpect(model().attributeHasFieldErrorCode("form", "loginId", "Pattern"));
////    }
////
////    @Test
////    @DisplayName("회원가입 실패: password_empty&passwordConfirm_empty")
////    public void joinX_password1() throws Exception {
////        //given
////        MemberSaveRequest form = new MemberSaveRequest("test1234", "", "", "테스터", "010", "1234", "5678", "123456", "test@gmail.com", true, true, true, true);
////
////        //when
////        mockMvc.perform(MockMvcRequestBuilders
////                        .post("/members/join")
////                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
////                        .flashAttr("form", form))
////                .andExpect(model().errorCount(1))
////                .andExpect(model().attributeHasFieldErrorCode("form", "password", "Pattern"));
////    }
////
////    @Test
////    @DisplayName("회원가입 실패: password_값O정규식O&passwordConfirm_empty")
////    public void joinX_password2() throws Exception {
////        //given
////        MemberSaveRequest form = new MemberSaveRequest("test1234", "test1234!@", "", "테스터", "010", "1234", "5678", "123456", "test@gmail.com", true, true, true, true);
////
////        //when
////        mockMvc.perform(MockMvcRequestBuilders
////                        .post("/members/join")
////                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
////                        .flashAttr("form", form))
////                .andExpect(model().errorCount(1))
////                .andExpect(model().attributeHasFieldErrorCode("form", "passwordConfirm", "passwordNotEqual"));
////    }
////
////    @Test
////    @DisplayName("회원가입 실패: password_값O정규식X&passwordConfirm_empty")
////    public void joinX_password3() throws Exception {
////        //given
////        MemberSaveRequest form = new MemberSaveRequest("test1234", "  ", "test1234!", "테스터", "010", "1234", "5678", "123456", "test@gmail.com", true, true, true, true);
////
////        //when
////        mockMvc.perform(MockMvcRequestBuilders
////                        .post("/members/join")
////                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
////                        .flashAttr("form", form))
////                .andExpect(model().errorCount(2))
////                .andExpect(model().attributeHasFieldErrors("form", "password"))
////                .andExpect(model().attributeHasFieldErrorCode("form", "password", "Pattern"));
////    }
////
////    @Test
////    @DisplayName("회원가입 실패: name_blank")
////    public void joinX_name1() throws Exception {
////        //given
////        MemberSaveRequest form = new MemberSaveRequest("test1234", "test1234!@", "test1234!@", "", "010", "1234", "5678", "123456", "test@gmail.com", true, true, true, true);
////
////        //when
////        mockMvc.perform(MockMvcRequestBuilders
////                        .post("/member/join")
////                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
////                        .flashAttr("form", form))
////                .andExpect(model().errorCount(1))
////                .andExpect(model().attributeHasFieldErrors("form", "name"))
////                .andExpect(model().attributeHasFieldErrorCode("form", "name", "Pattern"));
////    }
////
////    @Test
////    @DisplayName("회원가입 실패: name_empty")
////    public void joinX_name2() throws Exception {
////        //given
////        MemberSaveRequest form = new MemberSaveRequest("test1234", "test1234!@", "test1234!@", "  ", "010", "1234", "5678", "123456", "test@gmail.com", true, true, true, true);
////
////        //when
////        mockMvc.perform(MockMvcRequestBuilders
////                        .post("/member/join")
////                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
////                        .flashAttr("form", form))
////                .andExpect(model().errorCount(1))
////                .andExpect(model().attributeHasFieldErrors("form", "name"))
////                .andExpect(model().attributeHasFieldErrorCode("form", "name", "Pattern"));
////    }
////
//////    @Test
//////    @DisplayName("회원가입 실패: password_영문O숫자O특수문자X")
//////    public void joinX_password3() throws Exception {
//////        //given
//////        MemberSaveRequest form = new MemberSaveRequest("test1234", "test123456", "test123456", "테스터", "010-1234-5678", "test@gmail.com", true, true, true, false);
//////
//////        //when
//////        mockMvc.perform(MockMvcRequestBuilders
//////                        .post("/members/join")
//////                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//////                        .flashAttr("form", form))
//////                .andExpect(model().errorCount(2))
//////                .andExpect(model().attributeHasFieldErrors("form", new String[]{"password", "passwordConfirm"}))
//////                .andExpect(model().attributeHasFieldErrorCode("form", "password", "Pattern"))
//////                .andExpect(model().attributeHasFieldErrorCode("form", "passwordConfirm", "Pattern"));
//////    }
//////
//////    @Test
//////    @DisplayName("회원가입 실패: password_일치X")
//////    public void joinX_password_confirm() throws Exception {
//////        //given
//////        MemberSaveRequest form = new MemberSaveRequest("test1234", "test1234!@", "test1234!#", "테스터", "010-1234-5678", "test@gmail.com", true, true, true, false);
//////
//////        //when
//////        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
//////                        .post("/members/join")
//////                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//////                        .flashAttr("form", form))
//////                .andExpect(model().errorCount(1))
//////                .andExpect(model().attributeHasErrors("form"))
//////                .andReturn();
//////
//////        BeanPropertyBindingResult bindingResult = (BeanPropertyBindingResult) mvcResult.getModelAndView().getModelMap().getAttribute("org.springframework.validation.BindingResult.form");
//////        ObjectError globalError = bindingResult.getGlobalError();
//////        String defaultMessage = globalError.getDefaultMessage();
//////
//////        //then
//////        Assertions.assertThat(defaultMessage).isEqualTo("동일한 비밀번호를 입력해주세요.");
//////    }
//////
//////    @Test
//////    @DisplayName("아이디 중복 검증: 이미 사용중인 아이디")
//////    public void join_duplicate_loginId1() {
//////        //given
//////        MemberSaveRequest form = new MemberSaveRequest("test1234", "test1234!@", "test1234!@", "테스터", "010-1234-5678", "test@gmail.com", true, true, true, true);
//////        memberService.join(new MemberSaveDto(form));
//////
//////        //when
//////        boolean isNewLoginId = memberService.duplicateLoginId("test1234");
//////
//////        //then
//////        Assertions.assertThat(isNewLoginId).isFalse();
//////    }
//////
//////    @Test
//////    @DisplayName("아이디 중복 검증: 새로운 아이디")
//////    public void join_duplicate_loginId2() {
//////        //given
//////        MemberSaveRequest form = new MemberSaveRequest("test1234", "test1234!@", "test1234!@", "테스터", "010-1234-5678", "test@gmail.com", true, true, true, true);
//////        memberService.join(new MemberSaveDto(form));
//////
//////        //when
//////        boolean isNewLoginId = memberService.duplicateLoginId("new1234");
//////
//////        //then
//////        Assertions.assertThat(isNewLoginId).isTrue();
//////    }
//
//}