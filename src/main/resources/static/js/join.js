$(document).ready(function () {
    $('input[type="text"]').on('input', function (e) {
        var maxLength = $(this).attr('maxlength');
        var textLength = e.target.value.length;

        if (textLength > maxLength) {
            var trimmedValue = e.target.value.substring(0, maxLength);
            $(this).val(trimmedValue);
        }
    });

    $('#loginId').on('focusout', function () {
        isLoginIdValid();
    });

    $('#password').on('focusout', function () {
        isPasswordValid();
        isPwConfirmValid();
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

    $('input[id^="phonenumber"]').on('input', function () {
        verificationBtnState();
    });

    $('#agree_group').on('click', "#agree_all", function () {
        toggleAgreement(this);
    });
});
var interval;
function isLoginIdValid() {
    var loginId = $("#loginId").val();

    if (loginId.trim() === '') {
        $('#idMsg').text('아이디를 입력해주세요.');
        $('#idMsg').removeClass('success error').addClass('error');
        return false;
    } else if (!regexLoginId(loginId)) {
        $('#idMsg').text('5~20자의 영문 소문자, 숫자 조합을 사용해 주세요.');
        $('#idMsg').removeClass('success').addClass('error');
        return false;
    }
    $.ajax({
        type: 'POST',
        url: '/members/verification/loginId',
        data: { loginId: loginId },
        success: function (result) {
            if (result) {
                $('#idMsg').text('사용 가능한 아이디입니다.');
                $('#idMsg').removeClass('error').addClass('success');
            } else {
                $('#idMsg').text('이미 사용 중인 아이디입니다.');
                $('#idMsg').removeClass('success').addClass('error');
            }
        }
    });
    $('#idMsg').text('');
    return true;
}

function regexLoginId(loginId) {
    var regex = /^(?=.*[a-z])(?=.*\d)[a-z\d]{5,20}$/;
    return regex.test(loginId);
}

function isPasswordValid() {
    var password = $("#password").val();
    var confirmPassword = $('#passwordConfirm').val();

    if (password.trim() === '') {
        $('#pwMsg').text('비밀번호를 입력해주세요.');
        return false;
    } else if (!regexPassword(password)) {
        $('#pwMsg').text('8~16자의 영문 대/소문자, 숫자, 특수문자 조합을 사용해 주세요.');
        return false;
    }

    $('#pwMsg').text('');

    if (password === confirmPassword) {
        $('#pwconfirmMsg').text('');
    }

    return true;
}

function regexPassword(password) {
    var regex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&^()])[A-Za-z\d@$!%*?&^()]{8,16}$/;
    return regex.test(password);
}

function isPwConfirmValid() {
    var password = $('#password').val();
    var confirmPassword = $('#passwordConfirm').val();

    if (confirmPassword === '') {
        $('#pwconfirmMsg').text('');
    } else {
        if (password === confirmPassword) {
            $('#pwconfirmMsg').text('');
        } else {
            $('#pwconfirmMsg').text('비밀번호가 일치하지 않습니다.');
            return false;
        }
    }
    return true;
}

function isNameValid() {
    var name = $('#name').val();

    if (name.trim() === '') {
        $('#nameMsg').text('이름을 입력해주세요.');
        return false;
    } else if (!regexName(name)) {
        $('#nameMsg').text('한글, 영문 대/소문자를 사용해 주세요. (특수기호, 공백 사용 불가)');
        return false;
    }

    $('#nameMsg').text('');
    return true;
}

function regexName(name) {
    var regex = /^[가-힣a-zA-Z]{2,12}$/;
    return regex.test(name);
}

function isEmailValid() {
    var email = $('#email').val();

    if (email.trim() === '') {
        $('#emailMsg').text('이메일을 입력해주세요.');
        return false;
    } else if (!regexEmail(email)) {
        $('#emailMsg').text('유효한 이메일을 입력해주세요.');
        return false;
    }
    $.ajax({
        type: 'POST',
        url: '/members/verification/email',
        data: { email: email },
        success: function (result) {
            if (result) {
                $('#emailMsg').text('사용 가능한 이메일입니다.');
                $('#emailMsg').removeClass('error').addClass('success');
            } else {
                $('#emailMsg').text('이미 사용 중인 이메일입니다.');
                $('#emailMsg').removeClass('success').addClass('error');
            }
        }
    });
    return true;
}

function regexEmail(email) {
    var regex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    return regex.test(email);
}

function isPhoneValid() {
    var phonenumber1 = $('#phonenumber1').val();
    var phonenumber2 = $('#phonenumber2').val();
    var phonenumber3 = $('#phonenumber3').val();

    if (phonenumber1.trim() === '' || phonenumber2.trim() === '' || phonenumber3.trim() === '') {
        $('#phoneNumberMsg').text('휴대전화번호를 입력해 주세요.');
        return false;
    } else if (!regexPhone(phonenumber1, phonenumber2, phonenumber3)) {
        $('#phoneNumberMsg').text('유효한 휴대전화번호를 입력해 주세요.');
        return false;
    }

    $('#phoneNumberMsg').text('');
    return true;
}

function regexPhone(phonenumber1, phonenumber2, phonenumber3) {
    var regex1 = /^[0-9]{3,4}$/;
    var regex2 = /^[0-9]{4}$/;
    return regex1.test(phonenumber1) && regex1.test(phonenumber2) && regex2.test(phonenumber3);
}
function startTimer() {
    var timer = 180; // 3분 = 180초
    interval = setInterval(function () {
        var minutes = Math.floor(timer / 60);
        var seconds = timer % 60;

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
    var phonenumber1 = $('#phonenumber1').val();
    var phonenumber2 = $('#phonenumber2').val();
    var phonenumber3 = $('#phonenumber3').val();

    $.ajax({
        type: 'POST',
        url: '/members/send/verificationNo',
        data: { phonenumber1: phonenumber1, phonenumber2: phonenumber2, phonenumber3: phonenumber3 },
        dataType: 'json',
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

function verificationBtnState() {
    var phonenumber1 = $('#phonenumber1').val();
    var phonenumber2 = $('#phonenumber2').val();
    var phonenumber3 = $('#phonenumber3').val();
    var regex1 = /^[0-9]{3,4}$/;
    var regex2 = /^[0-9]{4}$/;
    var isValidPhoneNumber = regex1.test(phonenumber1) && regex1.test(phonenumber2) && regex2.test(phonenumber3);

    if (isValidPhoneNumber) {
        $('#btn_action_verify_mobile').removeClass('disabled');
    } else {
        $('#btn_action_verify_mobile').addClass('disabled');
    }

    $('#confirm_verify_mobile').hide();
    $('#phoneNumberMsg').text('');
    $('#btn_action_verify_mobile').text('인증번호 받기');
    clearInterval(interval);
    $('#expiryTime').text('');
}
function isVerificationValid() {
    var verificationCode = $('#verificationNo').val();

    if (verificationCode.trim() === '') {
        return false;
    }

    return true;
}

function toggleAgreement(element) {
    var isChecked = $(element).prop('checked');

    if (isChecked) {
        $(element).parents("#agree_group").find('input[type="checkbox"]').prop("checked", true);
    } else {
        $(element).parents("#agree_group").find('input[type="checkbox"]').prop("checked", false);
    }
}

function signUp() {
    if (!isLoginIdValid()) {
        alert("아이디를 입력해 주세요.");
        return false;
    }
    if (!isPasswordValid()) {
        alert("비밀번호를 입력해 주세요.");
        return false;
    }
    if (!isPwConfirmValid()) {
        alert("비밀번호가 일치하지 않습니다.");
        return false;
    }
    if (!isNameValid()) {
        alert("이름을 입력해 주세요.");
        return false;
    }
    if (!isPhoneValid()) {
        alert("휴대전화번호를 입력해 주세요.");
        return false;
    }
    if (!isVerificationValid()) {
        alert("휴대전화번호 인증해 주세요.");
        return false;
    }
    if (!isEmailValid()) {
        alert("이메일을 입력해 주세요.");
        return false;
    }

    if (!$('#agree_to_terms_of_use').is(':checked')) {
        alert("이용약관 동의를 확인해 주세요.");
        return false;
    }

    if (!$('#agree_to_personal_information').is(':checked')) {
        alert("개인정보 수집 및 이용 동의를 확인해 주세요.");
        return false;
    }

    return true;
}
