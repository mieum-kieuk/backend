$(window).on('unload', function () {
    $('input[type="text"]').val('');
    $('#findType1').prop('checked', true);
});

// swal설정
const mySwal = {
    container: 'my-swal-container',
    popup: 'my-swal-popup',
    htmlContainer: 'my-swal-text',
    confirmButton: 'my-swal-confirm-button',
    actions: 'my-swal-actions',
};
$('.submit_btn').click(function () {

    if (!validateBeforeSubmit()) {
        return;
    }

    let csrfHeader = $("meta[name='_csrf_header']").attr("content");
    let csrfToken = $("meta[name='_csrf']").attr("content");

    // 이메일 또는 휴대전화번호로 아이디 찾기
    if ($('input[name="findType"]:checked').val() === 'EMAIL') {
        let name = $('#name').val();
        let email = $('#email').val();

        $.ajax({
            url: '/ajax/member/find-id/email',
            type: 'POST',
            data: {name: name, email: email},
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken)
            },
            success: function (result) {
                if (result.code == 200) {
                    window.location.href = '/member/find-id/complete';
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
                    html: '아이디 찾기 중 오류가 발생했습니다.<br>다시 시도해 주세요.',
                    showConfirmButton: true,
                    confirmButtonText: '확인',
                    customClass: mySwal,
                    buttonsStyling: false
                });
            }
        });
    } else if ($('input[name="findType"]:checked').val() === 'PHONENUMBER') {
        let name = $('#name').val();
        let phonenumber = $('#phonenumber1').val() + '-' + $('#phonenumber2').val() + '-' + $('#phonenumber3').val();

        // 휴대전화로 데이터 가져오기
        $.ajax({
            url: '/ajax/member/find-id/phonenumber',
            type: 'POST',
            data: {name: name, phonenumber: phonenumber},
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken)
            },
            success: function (result) {
                if (result.code == 200) {
                    window.location.href = '/member/find-id/complete';
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
                    html: '아이디 찾기 중 오류가 발생했습니다.<br>다시 시도해 주세요.',
                    showConfirmButton: true,
                    confirmButtonText: '확인',
                    customClass: mySwal,
                    buttonsStyling: false
                });
            }
        });
    }
});

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

// 폼 제출 전 유효성 검사
function validateBeforeSubmit() {

    let byEmail = $('#findType1').is(':checked');
    let byPhonenumber = $('#findType2').is(':checked');

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
    if (!regexName()) {
        Swal.fire({
            html: '2~30자의 한글, 영문 대/소문자를 사용해 주세요.<br>(특수기호, 공백 사용 불가)',
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
    return true;
}
$('input[name="findType"]').change(function () {
    if ($('#findType1').is(':checked')) {
        $('#name').val('');
        $('#email').val('');
        $('#phonenumber1').val('010');
        $('#phonenumber2').val('');
        $('#phonenumber3').val('');
        $('#email_view').show();
        $('#phonenumber_view').hide();
        $('.field-error').empty();
    } else if ($('#findType2').is(':checked')) {
        $('#name').val('');
        $('#email').val('');
        $('#email_view').hide();
        $('#phonenumber_view').show();
        $('.phone_number').css("display", "block");
        $('.field-error').empty();
    }
})