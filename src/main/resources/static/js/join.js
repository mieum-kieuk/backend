let isLoginIdChecked = false;
let isEmailChecked = false;
let isAvailableLoginId = false;
let isAvailableEmail = false

$(document).ready(function () {
    $('#loginId').on('focusout', function () {
        isLoginIdValid();
    });

    $('#password').on('focusout', function () {
        isPasswordValid();
        if($('#passwordConfirm').val().trim() != '') {
            isPwConfirmValid();
        }
    });

    $('#passwordConfirm').on('focusout', function () {
        isPwConfirmValid();
    });

    $('#name').on('focusout', function () {
        isNameValid();
    });

    $('#detailAddress').click(function() {
        let zipCode = $('#zipCode').val().trim();
        let basicAddress = $('#basicAddress').val().trim();

        if (zipCode === '' || basicAddress === '') {
            Swal.fire({
                html: '주소 검색을 통해 우편번호와 기본주소를<br/>먼저 입력해 주세요.',
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                focusConfirm: true,
                buttonsStyling: false
            });
        } else {
            $('#detailAddress').prop('readonly', false);
        }
    });

    $('#phonenumber2, #phonenumber3').on('focusout', function () {
        isPhoneValid();
    });

    $('#email').on('focusout', function () {
        isEmailValid();
    });

    $('#btn_action_verify_mobile').on('click', function () {
        requestVerificationCode();
    });
    $('#btn_verify_mobile_confirm').click(function () {
        isVerificationValid();
    });
    $('input[id^="phonenumber"]').on('input', function () {
        verificationBtnState();
    });
    // 전체 체크
    $('#agree_group').on('click', "#agree_all", function() {
        let isChecked = $(this).prop("checked");
        $('#agree_group').find('.checkbox').prop("checked", isChecked);
    });

    // 개별 동의 체크박스 변경 시
    $('#agree_group').find('.checkbox').change(function() {
        let allChecked = $('#agree_group').find('.checkbox:not(#agree_all)').length === $('#agree_group').find('.checkbox:not(#agree_all):checked').length;
        $('#agree_all').prop('checked', allChecked);
    });
});

let csrfToken = $("meta[name='_csrf']").attr("content");
let csrfHeader = $("meta[name='_csrf_header']").attr("content");

//아이디 검증
function isLoginIdValid() {
    let loginId = $("#loginId").val();

    if (!isLoginIdEmpty()) {
        return;
    } else if (!regexLoginId(loginId)) {
        return;
    }

    $('#idMsg').text('');

    $.ajax({
        type: 'POST',
        url: '/members/check/loginId',
        data: {loginId: loginId},
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        },
        success: function (result) {
            isLoginIdChecked = true;
            if (result.code == 200) {
                isAvailableLoginId = true;
                $('#idMsg').text(result.message);
                $('#idMsg').removeClass('error').addClass('success');
            } else {
                isAvailableLoginId = false;
                $('#idMsg').text(result.message);
                $('#idMsg').removeClass('success').addClass('error');
            }
        },
        error: function () {
            $('#idMsg').text('아이디 중복 확인 중 오류가 발생했습니다. 다시 시도해 주세요.');
            $('#idMsg').removeClass('success').addClass('error');
        }
    });

    return;
}

function isLoginIdEmpty() {
    let loginId = $("#loginId").val();

    if (loginId.trim() === '') {
        $('#idMsg').text('아이디를 입력해 주세요.');
        $('#idMsg').removeClass('success error').addClass('error');
        return false;
    }

    return true;
}

function regexLoginId() {
    let loginId = $("#loginId").val();
    let regex = /^(?=.*[a-z])(?=.*\d)[a-z\d]{5,20}$/;
    if (!regex.test(loginId)) {
        $('#idMsg').text('5~20자의 영문 소문자, 숫자 조합을 사용해 주세요.');
        $('#idMsg').removeClass('success').addClass('error');
        return false;
    }

    return true;
}

//비밀번호 검증
function isPasswordValid() {
    let password = $('#password').val();
    let confirmPassword = $('#passwordConfirm').val();

    if (!isPasswordEmpty()) {
        return;
    } else if (!regexPassword()) {
        return;
    }

    if (password === confirmPassword) {
        $('#pwconfirmMsg').text('');
    }

    $('#pwMsg').text('');

    return;
}

function isPasswordEmpty() {
    let password = $("#password").val();

    if (password.trim() === '') {
        $('#pwMsg').text('비밀번호를 입력해 주세요.');
        $('#pwMsg').removeClass('success').addClass('error');
        return false;
    }

    return true;
}

function regexPassword() {
    let password = $("#password").val();
    let regex = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[@$!%*?&^()])[a-zA-Z\d@$!%*?&^()]{8,16}$/;
    if (!regex.test(password)) {
        $('#pwMsg').text('8~16자의 영문 대/소문자, 숫자, 특수문자 조합을 사용해 주세요.');
        $('#pwMsg').removeClass('success').addClass('error');

        return false;
    }

    return true;
}

//비밀번호 확인 검증
function isPwConfirmValid() {
    let password = $('#password').val();
    let confirmPassword = $('#passwordConfirm').val();

    if (password === confirmPassword) {
        $('#pwconfirmMsg').text('');
        return true;
    } else {
        $('#pwconfirmMsg').text('비밀번호가 일치하지 않습니다.');
        $('#pwconfirmMsg').removeClass('success').addClass('error');
        return false;
    }
}

//이름 검증
function isNameValid() {
    if (!isNameEmpty()) {
        return;
    } else if (!regexName()) {
        return;
    }
    $('#nameMsg').text('');
    return;
}

function isNameEmpty() {
    let name = $("#name").val();

    if (name.trim() === '') {
        $('#nameMsg').text('이름을 입력해 주세요.');
        $('#nameMsg').removeClass('success').addClass('error');
        return false;
    }

    return true;
}

function regexName() {
    let name = $('#name').val();
    let regex = /^[가-힣a-zA-Z]{2,12}$/;
    if (!regex.test(name)) {
        $('#nameMsg').text('2~12자의 한글, 영문 대/소문자를 사용해 주세요. (특수기호, 공백 사용 불가)');
        $('#nameMsg').removeClass('success').addClass('error');
        return false;
    }

    return true;
}

//이메일 검증
function isEmailValid() {
    let email = $('#email').val();

    if (!isEmailEmpty()) {
        return;
    } else if (!regexEmail()) {
        return;
    }

    $('#emailMsg').text('');

    $.ajax({
        type: 'POST',
        url: '/members/check/email',
        data: {email: email},
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        },
        success: function (result) {
            isEmailChecked = true;
            if (result.code == 200) {
                isAvailableEmail = true;
                $('#emailMsg').text(result.message);
                $('#emailMsg').removeClass('error').addClass('success');
            } else {
                isAvailableEmail = false;
                $('#emailMsg').text(result.message);
                $('#emailMsg').removeClass('success').addClass('error');
            }
        },
        error: function () {
            $('#emailMsg').text('이메일 중복 확인 중 오류가 발생했습니다. 다시 시도해 주세요.');
            $('#emailMsg').removeClass('success').addClass('error');
        }
    });

    return;
}

function isEmailEmpty() {
    let email = $('#email').val();

    if (email.trim() === '') {
        $('#emailMsg').text('이메일을 입력해 주세요.');
        $('#emailMsg').removeClass('success').addClass('error');

        return false;
    }

    return true;
}

function regexEmail() {
    let email = $('#email').val();
    let regex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    if (!regex.test(email)) {
        $('#emailMsg').text('유효한 이메일을 입력해 주세요.');
        $('#emailMsg').removeClass('success').addClass('error');
        return false;
    }

    return true;
}

//휴대전화번호 검증
function isPhoneValid() {
    if (!isPhoneEmpty()) {
        return;
    } else if (!regexPhone()) {
        return;
    }

    $('#phoneNumberMsg').text('');
    return;
}

function isPhoneEmpty() {
    let phonenumber2 = $('#phonenumber2').val();
    let phonenumber3 = $('#phonenumber3').val();

    if (phonenumber2.trim() === '' && phonenumber3.trim() === '') {
        $('#phoneNumberMsg').text('휴대전화번호를 입력해 주세요.');
        $('#phoneNumberMsg').removeClass('success').addClass('error');
        return false;
    }

    return true;
}

function regexPhone() {
    let phonenumber2 = $('#phonenumber2').val();
    let phonenumber3 = $('#phonenumber3').val();
    let regex1 = /^[0-9]{3,4}$/;
    let regex2 = /^[0-9]{4}$/;
    if (regex1.test(phonenumber2) && regex2.test(phonenumber3)) {
        return true;
    } else {
        $('#phoneNumberMsg').text('유효한 휴대전화번호를 입력해 주세요.');
        $('#phoneNumberMsg').removeClass('success').addClass('error');
        return false;
    }
}
function isAddressEmpty() {
    let zipCode = $('#zipCode').val().trim();
    let basicAddress = $('#basicAddress').val().trim();

    if (zipCode === '' || basicAddress === '') {
        return false;
    }

    return true;
}
//타이머 설정
let interval;

function startTimer() {
    clearInterval(interval);
    $('#expiryTime').css('display', 'block');
    $('#expiryTime').text('3:00');
    let timer = 179; // 2분 59초 = 179초

    interval = setInterval(function () {
        let minutes = Math.floor(timer / 60);
        let seconds = timer % 60;

        $('#expiryTime').text(minutes + ':' + (seconds < 10 ? '0' + seconds : seconds));

        if (--timer < 0) {
            clearInterval(interval);
            $('#phoneNumberMsg').text('');
            $('#confirm_verify_mobile').css('display', 'none');
            $('#btn_action_verify_mobile').text('인증번호 받기');
            $('#verificationNo').val('');
        }
    }, 1000);
}

function requestVerificationCode() {
    let phonenumber1 = $('#phonenumber1').val();
    let phonenumber2 = $('#phonenumber2').val();
    let phonenumber3 = $('#phonenumber3').val();

    $.ajax({
        type: 'POST',
        url: '/members/send/verificationNo',
        data: {phonenumber1: phonenumber1, phonenumber2: phonenumber2, phonenumber3: phonenumber3},
        dataType: 'json',
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        },
        success: function (result) {
            if (result.code == 200) {
                $('#phoneNumberMsg').text(result.message);
                $('#confirm_verify_mobile').css('display', 'flex');
                $('#btn_action_verify_mobile').text('재전송');
                $('#verificationNo').val('');
                startTimer();
            } else {
                $('#confirm_verify_mobile').css('display', 'none');
                $('#phoneNumberMsg').text(result.message);
            }
        },
        error: function () {
            $('#phoneNumberMsg').text('인증번호 발급 중 오류가 발생했습니다. 다시 시도해 주세요.');
        }
    });
}

//인증번호 검증
function verificationBtnState() {
    let phonenumber2 = $('#phonenumber2').val();
    let phonenumber3 = $('#phonenumber3').val();
    let regex1 = /^[0-9]{3,4}$/;
    let regex2 = /^[0-9]{4}$/;
    let isValidPhoneNumber = regex1.test(phonenumber2) && regex2.test(phonenumber3);

    if (isValidPhoneNumber) {
        $('#btn_action_verify_mobile').removeClass('disabled');
    } else {
        $('#verificationNo').val('');
        $('#verificationNo').attr('placeholder', '');
        $('#verificationNo').attr('disabled', false);
        $('#verificationNo').css('background-color', '#FFF');
        $('#btn_action_verify_mobile').addClass('disabled');
        $('#btn_action_verify_mobile').attr('disabled', false);
        $('#btn_verify_mobile_confirm').removeClass('disabled');
        $('#btn_verify_mobile_confirm').attr('disabled', false);
        $('#verificationNo').attr('complete', "false");

        clearInterval(interval);
        $('#confirm_verify_mobile').css('display', 'none');
        $('#phoneNumberMsg').text('');
        $('#btn_action_verify_mobile').text('인증번호 받기');
    }
}

function isVerificationValid() {
    let phonenumber1 = $('#phonenumber1').val();
    let phonenumber2 = $('#phonenumber2').val();
    let phonenumber3 = $('#phonenumber3').val();
    let verificationNo = $('#verificationNo').val();

    $.ajax({
        type: 'POST',
        url: '/members/check/verificationNo',
        data: {
            phonenumber1: phonenumber1,
            phonenumber2: phonenumber2,
            phonenumber3: phonenumber3,
            verificationNo: verificationNo
        },
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        },
        success: function (result) {
            if (result.code == 200) {
                $('#verificationNo').val('');
                $('#verificationNo').attr('placeholder', '휴대전화 인증 완료');
                $('#verificationNo').attr('disabled', true);
                $('#verificationNo').css('background-color', '#EFEFEF');
                $('#verificationNo').css('cursor', 'default');
                $('#btn_action_verify_mobile').attr('disabled', true);
                $('#btn_action_verify_mobile').addClass('disabled');
                $('#btn_verify_mobile_confirm').attr('disabled', true);
                $('#btn_verify_mobile_confirm').addClass('disabled');
                clearInterval(interval);
                $('#expiryTime').css('display', 'none');
                $('#verificationNo').attr('complete', "true");
            } else {
                $('#verificationNo').val('');
                Swal.fire({
                    text: result.message,
                    showConfirmButton: true,
                    confirmButtonText: '확인',
                    customClass: mySwal,
                    buttonsStyling: false
                });
                $('#verificationNo').attr('complete', "false");
            }
        },
        error: function () {
            $('verificationMsg').text('인증번호 인증 중 오류가 발생했습니다. 다시 시도해 주세요.');
        }
    });
}

function isVerificationCompelte() {
    return $('#verificationNo').attr('complete') === "true";
}

function validateBeforeSubmit() {
    // 아이디 유효성 검사
    if (!isLoginIdEmpty()) {
        Swal.fire({
            text: '아이디를 입력해 주세요.',
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    } else if (!regexLoginId()) {
        Swal.fire({
            text: '5~20자의 영문 소문자, 숫자 조합을 사용해 주세요.',
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    } else if (!isLoginIdChecked)  {
        Swal.fire({
            text: '아이디 중복검사를 해주세요.',
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }
    else if (!isAvailableLoginId) {
        Swal.fire({
            text: '이미 사용중인 아이디입니다.',
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }

    // 비밀번호 유효성 검사
    if (!isPasswordEmpty()) {
        Swal.fire({
            text: '비밀번호를 입력해 주세요.',
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    } else if (!regexPassword()) {
        Swal.fire({
            text: '8~16자의 영문 대/소문자, 숫자, 특수문자 조합을 사용해 주세요.',
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }

    // 비밀번호 확인 유효성 검사
    if (!isPwConfirmValid()) {
        Swal.fire({
            text: '비밀번호가 일치하지 않습니다.',
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }

    // 이름 유효성 검사
    if (!isNameEmpty()) {
        Swal.fire({
            text: '이름을 입력해 주세요.',
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    } else if (!regexName()) {
        Swal.fire({
            html: '2~12자의 한글, 영문 대/소문자를 사용해 주세요.<br>(특수기호, 공백 사용 불가)',
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }
    if (!isAddressEmpty()) {
        console.log("hi");
        Swal.fire({
            text: '주소를 입력해 주세요.',
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }

    // 휴대전화번호 유효성 검사
    if (!isPhoneEmpty()) {
        Swal.fire({
            text: '휴대전화번호를 입력해 주세요.',
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    } else if (!regexPhone()) {
        Swal.fire({
            text: '휴대전화번호 형식으로 입력해 주세요.',
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }

    // 인증 번호 유효성 검사
    if (!isVerificationCompelte()) {
        Swal.fire({
            text: '휴대전화번호를 인증해 주세요.',
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }

    // 이메일 유효성 검사
    if (!isEmailEmpty()) {
        Swal.fire({
            text: '이메일을 입력해 주세요.',
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    } else if (!regexEmail()) {
        Swal.fire({
            text: '이메일 형식으로 입력해 주세요.',
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    } else if (!isEmailChecked) {
        Swal.fire({
            text: '이메일 중복검사를 해주세요.',
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    } else if (!isAvailableEmail) {
        Swal.fire({
            text: '이미 사용중인 이메일입니다.',
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }

    // 이용약관 동의 확인
    if (!$('#agree_to_terms_of_use').is(':checked')) {
        Swal.fire({
            text: '이용약관 동의를 확인해 주세요.',
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal
        });
        return false;
    }

    // 개인정보 수집 및 이용 동의 확인
    if (!$('#agree_to_personal_information').is(':checked')) {
        Swal.fire({
            text: '개인정보 수집 및 이용 동의를 확인해 주세요.',
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal
        });
        return false;
    }

    $('.submit_btn').prop('disabled', true);
    $('.loader_wrap').css('display', 'block');

    return true;
}

