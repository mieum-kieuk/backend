let originalEmail;

$(document).ready(function () {
    $('#verificationEmailBtn').on('click', function() {
        sendVerificationEmail();
    });

    $('#newNumberBtn').on('click', function () {
        $(this).attr('id', 'btn_action_verify_mobile');

        $(this).addClass('disabled');

        $('#phonenumber1').val('010');
        $('#phonenumber2').val('');
        $('#phonenumber3').val('');
        $('#phonenumber1').removeAttr('readonly');
        $('#phonenumber2').removeAttr('readonly');
        $('#phonenumber3').removeAttr('readonly');
        $(this).text('인증번호 받기');
    });
    $('#phonenumber2, #phonenumber3').on('focusout', function () {
        isPhoneValid();
    });

    if ($('#memberInfoModify').length) {
        originalEmail = $('#email').val().trim();
    }

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

    $('#withdrawBtn').on('click', function () {
        withdrawal();
    });
});

let csrfToken = $("meta[name='_csrf']").attr("content");
let csrfHeader = $("meta[name='_csrf_header']").attr("content");

function sendVerificationEmail() {
    $.ajax({
        type: 'POST',
        url: '/ajax/email/verification',
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        },
        success: function(data) {
            if (data.code === 200) {
                Swal.fire({
                    text: "인증 메일이 전송되었습니다.",
                    showConfirmButton: true,
                    confirmButtonText: '확인',
                    customClass: mySwal,
                    buttonsStyling: false
                });
            } else {
                Swal.fire({
                    text: "인증 메일 전송 중 오류가 발생했습니다.",
                    showConfirmButton: true,
                    confirmButtonText: '확인',
                    customClass: mySwal,
                    buttonsStyling: false
                });
            }
        },
        error: function() {
            Swal.fire({
                html: "인증 메일 전송 중 오류가 발생했습니다.<br>다시 시도해 주세요.",
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
        }
    });
}

// 새 비밀번호 ajax
function validateNewPassword() {
    let newPassword = $('#password').val();

    if (!regexPassword(newPassword)) {
        return false;
    }

    $.ajax({
        type: 'POST',
        url: '/ajax/mypage/check/password',
        data: {'newPassword': newPassword},
        async: false,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        },
        success: function (result) {
            if (result.code === 200) {
                $('#pwMsg').text('');
                return true;
            } else if (result.code == 409){
                Swal.fire({
                    text: result.message,
                    showConfirmButton: true,
                    confirmButtonText: '확인',
                    customClass: mySwal,
                    buttonsStyling: false
                });
                $('#pwMsg').text(result.message);
                return false;
            }
        },
        error: function () {
            Swal.fire({
                html: "비밀번호 중복 확인 중 오류가 발생했습니다.<br>다시 시도해 주세요.",
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
            return false;
        }
    });
}

// 비밀번호 확인 유효성 검사
function validatePasswordConfirm() {
    let newPassword = $('#password').val();
    let confirmPassword = $('#passwordConfirm').val();

    if (newPassword === confirmPassword) {
        $('#pwconfirmMsg').text('');
    } else {
        $('#pwconfirmMsg').text('비밀번호가 일치하지 않습니다.');
        $('#pwconfirmMsg').removeClass('success error').addClass('error');
    }
}

function regexPassword(password) {
    let regex = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[@$!%*?&^()])[a-zA-Z\d@$!%*?&^()]{8,16}$/;
    if (!regex.test(password)) {
        $('#pwMsg').text('8~16자의 영문 대/소문자, 숫자, 특수문자 조합을 사용해 주세요.');
        $('#pwMsg').removeClass('success error').addClass('error');
        Swal.fire({
            html: '8~16자의 영문 대/소문자, 숫자, 특수문자 조합을<br/>사용해 주세요.',
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }

    return true;
}

// 이메일 검증
function isEmailValid() {
    let email = $('#email').val();

    if (!isEmailEmpty()) {
        return;
    } else if (!regexEmail()) {
        return;
    }

    $('#emailMsg').text('');

    if (email === originalEmail) {
        $('#emailMsg').text('');
        return;
    }
    $.ajax({
        type: 'POST',
        url: '/members/verification/email',
        data: {email: email},
        async: false,
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
    if (!$('#phonenumber1').prop('readonly') && !$('#phonenumber2').prop('readonly') && !$('#phonenumber3').prop('readonly')) {
        // 비어 있는지 여부 검사
        if (!isPhoneEmpty()) {
            return;
        }
        // 정규식 유효성 검사
        if (!regexPhone()) {
            return;
        }

        $('#phoneNumberMsg').text('');

        // 인증 번호 유효성 검사
        if (!isVerificationCompelte()) {
            Swal.fire({
                text: "휴대전화번호를 인증해 주세요.",
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
            return false;
        }
    }
}

function isPhoneEmpty() {
    let phonenumber2 = $('#phonenumber2').val();
    let phonenumber3 = $('#phonenumber3').val();

    if (phonenumber2.trim() === '' || phonenumber3.trim() === '') {
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

    if (phonenumber2.trim() !== '' && phonenumber3.trim() !== '') {
        if (regex1.test(phonenumber2) && regex2.test(phonenumber3)) {
            return true;
        } else {
            $('#phoneNumberMsg').text('유효한 휴대전화번호를 입력해 주세요.');
            $('#phoneNumberMsg').removeClass('success').addClass('error');
            return false;
        }
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
                Swal.fire({
                    html: "인증번호가 일치하지 않습니다.<br>확인 후 다시 시도해 주세요.",
                    showConfirmButton: true,
                    confirmButtonText: '확인',
                    customClass: mySwal,
                    buttonsStyling: false
                });
                $('#verificationNo').attr('complete', "false");
            }
        }
    });
}

function isVerificationCompelte() {
    return $('#verificationNo').attr('complete') === "true";
}

function validateBeforeSubmit() {
    let newPassword = $('#password').val();
    let newConfirmPassword = $('#passwordConfirm').val();

    if (newPassword.trim() !== "") {
        if (!validateNewPassword()) {
            return false;
        }

        if (newConfirmPassword.trim() === "") {
            Swal.fire({
                text: "새 비밀번호 확인을 입력해 주세요.",
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
            return false;
        }

        if (!validatePasswordConfirm()) {
            Swal.fire({
                text: "비밀번호가 일치하지 않습니다.",
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
            return false;
        }
    }

    // 휴대전화번호 유효성 검사
    isPhoneValid();

    // 이메일 유효성 검사
    if (!isEmailEmpty()) {
        Swal.fire({
            text: "이메일을 입력해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    } else if (!regexEmail()) {
        Swal.fire({
            text: "유효한 이메일을 입력해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }

    return true;
}

function withdrawal() {
    Swal.fire({
        text: "탈퇴하시겠습니까?",
        showCancelButton: true,
        cancelButtonText: '아니요',
        confirmButtonText: '예',
        closeOnConfirm: false,
        closeOnCancel: true,
        customClass: mySwalConfirm,
        reverseButtons: true,
        buttonsStyling: false,
    }).then((result) => {
        if (result.isConfirmed) {
            $.ajax({
                type: 'POST',
                url: '/ajax/', // 실제 탈퇴 요청 URL로 변경
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(csrfHeader, csrfToken); // CSRF 토큰 설정
                },
                success: function (data) {
                    if (data.code === 200) {
                        Swal.fire({
                            text: "성공적으로 탈퇴되었습니다.",
                            showConfirmButton: true,
                            confirmButtonText: '확인',
                            customClass: mySwal,
                            buttonsStyling: false
                        }).then(() => {
                            window.location.href = '/login'; // 탈퇴 후 리디렉션할 URL
                        });
                    } else {
                        Swal.fire({
                            text: "탈퇴 중 오류가 발생했습니다.",
                            showConfirmButton: true,
                            confirmButtonText: '확인',
                            customClass: mySwal,
                            buttonsStyling: false
                        });
                    }
                },
                error: function () {
                    Swal.fire({
                        html: "탈퇴 중 오류가 발생했습니다.<br>다시 시도해 주세요.",
                        showConfirmButton: true,
                        confirmButtonText: '확인',
                        customClass: mySwal,
                        buttonsStyling: false
                    });
                }
            });
        }
    });
}

//회원 정보 수정 전 본인 확인
$('#loginBtn').click(function () {
    let csrfHeader = $("meta[name='_csrf_header']").attr("content");
    let csrfToken = $("meta[name='_csrf']").attr("content");
    let password = $('#password').val().trim();

    if (password === '') {
        Swal.fire({
            text: "비밀번호를 입력해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    } else {
        $.ajax({
                type: 'POST',
                url: '/ajax/mypage/validate',
                data: {'password': password},
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(csrfHeader, csrfToken);
                },
                success: function (result) {
                    if (result.code == 200) {
                        window.location.href = '/mypage/info/edit'
                    } else if (result.code == 400) {
                        Swal.fire({
                            text: result.message,
                            showConfirmButton: true,
                            confirmButtonText: '확인',
                            customClass: mySwal,
                            buttonsStyling: false
                        });
                    }
                },
                error: function () {
                    Swal.fire({
                        html: "본인 확인 중 오류가 발생했습니다.<br>다시 시도해 주세요.",
                        showConfirmButton: true,
                        confirmButtonText: '확인',
                        customClass: mySwal,
                        buttonsStyling: false
                    });
                }
            }
        );
    }
});
