// 뒤로가기로 접속하는 경우 input 데이터 초기화
$(window).on('unload', function () {
    $('input[type="text"]').val('');
    $('#phonenumber1').val('010');
    $('#findType1').prop('checked', true);
});

let csrfHeader = $("meta[name='_csrf_header']").attr("content");
let csrfToken = $("meta[name='_csrf']").attr("content");

// 비밀번호 찾기
$('#find #findPwBtn').click(function () {
    // 유효성 검사 실행
    if (!validateBeforeSubmit()) {
        return;
    }

    // 이메일 또는 휴대전화번호로 비밀번호 찾기
    if ($('input[name="findType"]:checked').val() === 'EMAIL') {
        let loginId = $('#loginId').val();
        let name = $('#name').val();
        let email = $('#email').val();

        $.ajax({
            type: 'POST',
            url: '/api/member/find-password/email',
            data: {loginId: loginId, name: name, email: email},
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken)
            },
            success: function (result) {
                if (result.status == 200) {
                    window.location.href = '/member/find-password/send';
                } else {
                    Swal.fire({
                        html: result.message.replace('\n', '<br>'),
                        showConfirmButton: true,
                        confirmButtonText: '확인',
                        customClass: mySwal,
                        buttonsStyling: false
                    });
                }
            },
            error: function () {
                Swal.fire({
                    html: '비밀번호 찾기 중 오류가 발생했습니다.<br>다시 시도해 주세요.',
                    showConfirmButton: true,
                    confirmButtonText: '확인',
                    customClass: mySwal,
                    buttonsStyling: false
                });
            }
        });
    } else if ($('input[name="findType"]:checked').val() === 'PHONENUMBER') {
        let loginId = $('#loginId').val();
        let name = $('#name').val();
        let phonenumber = $('#phonenumber1').val() + '-' + $('#phonenumber2').val() + '-' + $('#phonenumber3').val();

        $.ajax({
            type: 'POST',
            url: '/api/member/find-password/phonenumber',
            data: {'loginId': loginId, 'name': name, 'phonenumber': phonenumber},
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken)
            },
            success: function (result) {
                if (result.status == 200) {
                    window.location.href = '/member/find-password/send';
                } else {
                    Swal.fire({
                        html: result.message.replace('\n', '<br>'),
                        showConfirmButton: true,
                        confirmButtonText: '확인',
                        customClass: mySwal,
                        buttonsStyling: false
                    });
                }
            },
            error: function () {
                Swal.fire({
                    html: '비밀번호 찾기 중 오류가 발생했습니다.<br>다시 시도해 주세요.',
                    showConfirmButton: true,
                    confirmButtonText: '확인',
                    customClass: mySwal,
                    buttonsStyling: false
                });
            }
        });
    }
});

// 임시 비밀번호 전송
$('#find_pw #sendPwBtn').click(function () {
    $('.loader_wrap').css('display', 'block');
    $('#find_pw #sendPwBtn').prop('disabled', 'true');
    let csrfHeader = $("meta[name='_csrf_header']").attr("content");
    let csrfToken = $("meta[name='_csrf']").attr("content");
    let email = $('#email').text();

    $.ajax({
        type: 'POST',
        url: '/email/temp-password',
        data: {email: email},
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        },
        success: function (result) {
            if(result.status == 200) {
                window.location.href = '/member/find-password/complete';
            } else {
                Swal.fire({
                    html: result.message.replace('\n', '<br>'),
                    showConfirmButton: true,
                    confirmButtonText: '확인',
                    customClass: mySwal,
                    buttonsStyling: false
                });
            }
        },
        error: function () {
            Swal.fire({
                html: '임시 비밀번호 전송 중 오류가 발생했습니다.<br>다시 시도해 주세요.',
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
        }
    })
});

// 아이디 입력란 유효성 검사
function isLoginIdPresent() {
    let loginId = $('#loginId').val();
    let result = loginId.trim() === '' ? false : true;
    return result;
}

function regexId() {
    let loginId = $('#loginId').val();
    const regex = /^(?=.*[a-z])(?=.*\d)[a-z\d]{5,20}$/;

    let result = regex.test(loginId) ? true : false;
    return result;
}

// 이름 입력란 유효성 검사
function isNamePresent() {
    let name = $('#name').val();
    let result = name.trim() === '' ? false : true;
    return result;
}

function regexName() {
    let name = $('#name').val();
    let regex = /^[가-힣a-zA-Z]{2,30}$/;
    if (!regex.test(name)) {
        $('#nameMsg').text('2~30자의 한글, 영문 대/소문자를 사용해 주세요. (특수기호, 공백 사용 불가)');
        return false;
    }

    return true;
}

// 이메일 입력란 유효성 검사
function isEmailPresent() {
    let email = $('#email').val();
    let result = email.trim() === '' ? false : true;
    return result;
}

function regexEmail() {
    let email = $('#email').val();
    const regex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

    let result = regex.test(email) ? true : false;
    return result;
}

// 휴대전화번호 입력란 유효성 검사
function isPhonenumberPresent() {
    let phonenumber2 = $('#phonenumber2').val();
    let phonenumber3 = $('#phonenumber3').val();

    let result = phonenumber2.trim() === '' && phonenumber3.trim() === '' ? false : true;
    return result;
}

function regexPhonenumber() {
    let phonenumber2 = $('#phonenumber2').val();
    let phonenumber3 = $('#phonenumber3').val();
    const regex1 = /^[0-9]{3,4}$/;
    const regex2 = /^[0-9]{4}$/;

    let result = regex1.test(phonenumber2) && regex2.test(phonenumber3) ? true : false;
    return result;
}

function validateBeforeSubmit() {

    let byEmail = $('#findType1').is(':checked');
    let byPhonenumber = $('#findType2').is(':checked');

    if (!isLoginIdPresent()) {
        Swal.fire({
            text: '아이디를 입력해 주세요.',
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }

    if (!regexId()) {
        Swal.fire({
            text: '아이디 형식으로 입력해 주세요.',
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }

    if (!isNamePresent()) {
        Swal.fire({
            text: '이름을 입력해 주세요.',
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }

    if (byEmail) {
        if (!isEmailPresent()) {
            Swal.fire({
                text: '이메일을 입력해 주세요.',
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
            return false;
        }

        if (!regexEmail()) {
            Swal.fire({
                text: '이메일 형식으로 입력해 주세요.',
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
            return false;
        }
    }

    if (byPhonenumber) {
        if (!isPhonenumberPresent()) {
            Swal.fire({
                text: '휴대전화번호를 입력해 주세요.',
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
            return false;
        }

        if (!regexPhonenumber()) {
            Swal.fire({
                text: '휴대전화번호 형식으로 입력해 주세요.',
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
            return false;
        }
    }

    $('.loader_wrap').css('display', 'block');
    return true;
}

$('input[name="findType"]').change(function () {
    if ($('#findType1').is(':checked')) {
        $('#loginId').val('');
        $('#name').val('');
        $('#phonenumber1').val('010');
        $('#phonenumber2').val('');
        $('#phonenumber3').val('');
        $('#email_view').show();
        $('#phonenumber_view').hide();
        $('.field-error').empty();
    } else if ($('#findType2').is(':checked')) {
        $('#loginId').val('');
        $('#name').val('');
        $('#email').val('');
        $('#email_view').hide();
        $('#phonenumber_view').show();
        $('.field-error').empty();
    }
})