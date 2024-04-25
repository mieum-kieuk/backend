$(document).ready(function () {
    $('#submitBtn').on('click', function() {
        validateBeforeSubmit();
    });

    $('#password').on('focusout', function () {
        isPasswordValid();
    });

    $('#passwordConfirm').on('focusout', function () {
        isPwConfirmValid();
    });

    $('#name').on('focusout', function () {
        isNameValid();
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

    $('#agree_group').on('click', "#agree_all", function () {
        toggleAgreement(this);
    });
});

let csrfToken = $("meta[name='_csrf']").attr("content");
let csrfHeader = $("meta[name='_csrf_header']").attr("content");


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
        $('#pwMsg').removeClass('success error').addClass('error');
        return false;
    }

    return true;
}

function regexPassword() {
    let password = $("#password").val();
    let regex = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[@$!%*?&^()])[a-zA-Z\d@$!%*?&^()]{8,16}$/;
    if (!regex.test(password)) {
        $('#pwMsg').text('8~16자의 영문 대/소문자, 숫자, 특수문자 조합을 사용해 주세요.');
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
        return false;
    }
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
        url: '/members/verification/email',
        data: {email: email},
        success: function (result) {
            if (result) {
                $('#emailMsg').text('사용 가능한 이메일입니다.');
                $('#emailMsg').removeClass('error').addClass('success');
            } else {
                $('#emailMsg').text('이미 사용 중인 이메일입니다.');
                $('#emailMsg').removeClass('success').addClass('error');
            }
        },
        error: function () {
            // 오류 발생 시 처리
            $('#emailMsg').text('유효한 이메일을 입력해 주세요.');
            $('#emailMsg').removeClass('success').addClass('error');
        }
    });

    return;
}

function isEmailEmpty() {
    let email = $('#email').val();

    if (email.trim() === '') {
        $('#emailMsg').text('이메일을 입력해 주세요.');
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
        return false;
    }
}

//타이머 설정
let interval;

function startTimer() {
    let timer = 180; // 3분 = 180초
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
            if (result['success']) {
                $('#phoneNumberMsg').text(result['message']);
                $('#confirm_verify_mobile').css('display', 'flex');
                $('#btn_action_verify_mobile').text('재전송');
                $('#expiryTime').text('3:00');
                $('#verificationNo').val('');
                startTimer();
            } else {
                $('#confirm_verify_mobile').css('display', 'none');
                $('#phoneNumberMsg').text(result['message']);
            }
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
        $('#btn_action_verify_mobile').addClass('disabled');
    }

    $('#verificationNo').val('');
    $('#verificationNo').attr('placeholder', '');
    $('#verificationNo').attr('disabled', false);
    $('#verificationNo').css('background-color', '#FFF');
    $('#verificationNo').css('cursor', 'pointer');
    $('#btn_verify_mobile_confirm').attr('disabled', false);
    $('#btn_verify_mobile_confirm').css('border', '1px solid #999');
    $('#verificationNo').attr('complete', "false");

    $('#confirm_verify_mobile').hide();
    $('#phoneNumberMsg').text('');
    $('#btn_action_verify_mobile').text('인증번호 받기');
    clearInterval(interval);
    $('#expiryTime').text('');
}

function isVerificationValid() {
    let phonenumber1 = $('#phonenumber1').val();
    let phonenumber2 = $('#phonenumber2').val();
    let phonenumber3 = $('#phonenumber3').val();
    let verificationNo = $('#verificationNo').val();

    $.ajax({
        type: 'POST',
        url: '/members/verification/verificationNo',
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
            if (result) {
                $('#expiryTime').css('display', 'none');
                $('#verificationNo').val('');
                $('#verificationNo').attr('placeholder', '휴대전화 인증 완료');
                $('#verificationNo').attr('disabled', true);
                $('#verificationNo').css('background-color', '#EFEFEF');
                $('#verificationNo').css('cursor', 'default');
                $('#btn_verify_mobile_confirm').attr('disabled', true);
                $('#btn_verify_mobile_confirm').css('border', '1px solid #999');
                $('#verificationNo').attr('complete', "true");
            } else {
                $('#verificationNo').val('');
                alert('인증번호가 일치하지 않습니다.\n확인 후 다시 시도해 주세요.');
                $('#verificationNo').attr('complete', "false");
            }
        }
    });
}

function isVerificationCompelte() {
    return $('#verificationNo').attr('complete') === "true";
}

//전체 선택
function toggleAgreement(element) {
    let isChecked = $(element).prop('checked');

    if (isChecked) {
        $(element).parents("#agree_group").find('input[type="checkbox"]').prop("checked", true);
    } else {
        $(element).parents("#agree_group").find('input[type="checkbox"]').prop("checked", false);
    }
}

function validateBeforeSubmit() {

    // 비밀번호 유효성 검사
    if (!isPasswordEmpty()) {
        alert("비밀번호를 입력해 주세요.");
        return false;
    } else if (!regexPassword()) {
        alert("유효한 비밀번호를 입력해 주세요.");
        return false;
    }

    // 비밀번호 확인 유효성 검사
    if (!isPwConfirmValid()) {
        alert("비밀번호가 일치하지 않습니다.");
        return false;
    }

    // 휴대전화번호 유효성 검사
    if (!isPhoneEmpty()) {
        alert("휴대전화번호를 입력해 주세요.");
        return false;
    } else if (!regexPhone()) {
        alert("유효한 휴대전화번호를 입력해 주세요.");
        return false;
    }

    // 인증 번호 유효성 검사
    if (!isVerificationCompelte()) {
        alert("휴대전화번호를 인증해 주세요.");
        return false;
    }

    // 이메일 유효성 검사
    if (!isEmailEmpty()) {
        alert("이메일을 입력해 주세요.");
        return false;
    } else if (!regexEmail()) {
        alert("유효한 이메일을 입력해 주세요.");
        return false;
    }

    // 이용약관 동의 확인
    if (!$('#agree_to_terms_of_use').is(':checked')) {
        alert("이용약관 동의를 확인해 주세요.");
        return false;
    }

    // 개인정보 수집 및 이용 동의 확인
    if (!$('#agree_to_personal_information').is(':checked')) {
        alert("개인정보 수집 및 이용 동의를 확인해 주세요.");
        return false;
    }

    return true;
}

