let isAvailableLoginId = false;
let isAvailableEmail = false;

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

    $('#email').on('focusout', function () {
        isEmailValid();
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
        url: '/admin/admins/verification/loginId',
        data: {loginId: loginId},
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        },
        success: function (result) {
            if (result) {
                isAvailableLoginId = true;
                $('#idMsg').text('사용 가능한 아이디입니다.');
                $('#idMsg').removeClass('error').addClass('success');
            } else {
                isAvailableLoginId = false;
                $('#idMsg').text('이미 사용 중인 아이디입니다.');
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
        url: '/admin/admins/verification/email',
        data: {email: email},
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        },
        success: function (result) {
            if (result) {
                isAvailableEmail = true;
                $('#emailMsg').text('사용 가능한 이메일입니다.');
                $('#emailMsg').removeClass('error').addClass('success');
            } else {
                isAvailableEmail = false;
                $('#emailMsg').text('이미 사용 중인 이메일입니다.');
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

function validateBeforeSubmit() {
    // 아이디 유효성 검사
    if (!isLoginIdEmpty()) {
        alert("아이디를 입력해 주세요.");
        return false;
    } else if (!regexLoginId()) {
        alert("유효한 아이디를 입력해 주세요.");
        return false;
    } else if(!isAvailableLoginId) {
        alert("이미 사용중인 아이디입니다.");
        return false;
    }

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

    // 이메일 유효성 검사
    if (!isEmailEmpty()) {
        alert("이메일을 입력해 주세요.");
        return false;
    } else if (!regexEmail()) {
        alert("유효한 이메일을 입력해 주세요.");
        return false;
    } else if(!isAvailableEmail) {
        alert("이미 사용중인 이메일입니다.");
        return false;
    }

    $('.submit_btn').prop('disabled', true);

    return true;
}

