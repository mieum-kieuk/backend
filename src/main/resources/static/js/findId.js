$(window).on('unload', function() {
    $('input[type="text"]').val('');
});
$(document).ready(function() {
    $('.submit_btn').click(function() {
        // 유효성 검사 실행
        if (!validateBeforeSubmit()) {
            return;
        }

        let csrfHeader = $("meta[name='_csrf_header']").attr("content");
        let csrfToken = $("meta[name='_csrf']").attr("content");

        if ($('input[name="findType"]:checked').val() === 'EMAIL') {
            let name = $('#name').val();
            let email = $('#email').val();

            // 이메일로 데이터 가져오기
            $.ajax({
                url: '/find-id/email',
                type: 'POST',
                data: {'name': name, 'email': email},
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(csrfHeader, csrfToken)
                },
                success: function(result) {
                    if(result) {
                        console.log("아이디 찾기 성공");
                    }
                },
                error: function() {
                    console.log("아이디 찾기 실패");
                }
            });
        } else if ($('input[name="findType"]:checked').val() === 'PHONENUMBER') {
            let name = $('#name').val();
            let phonenumber = $('#phonenumber1').val() + '-' + $('#phonenumber2').val() + '-' + $('#phonenumber3').val();

            // 휴대전화로 데이터 가져오기
            $.ajax({
                url: '/find-id', // 적절한 엔드포인트로 변경 필요
                type: 'POST',
                data: {'name': name, 'phonenumber': phonenumber},
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(csrfHeader, csrfToken)
                },
                success: function(result) {
                    if(result) {
                        console.log("아이디 찾기 성공");
                    }
                },
                error: function() {
                    console.log("아이디 찾기 실패");
                }
            });
        }
    });

});
// 이름 입력란 유효성 검사
function isNamePresent() {
    let name = $('#name').val();
    let result = name.trim() === '' ? false : true;
    return result;
}
function regexName() {
    let name = $('#name').val();
    let regex = /^[가-힣a-zA-Z]{2,12}$/;
    if (!regex.test(name)) {
        $('#nameMsg').text('2~12자의 한글, 영문 대/소문자를 사용해 주세요. (특수기호, 공백 사용 불가)');
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

    if (!isNamePresent()) {
        alert("이름을 입력해 주세요.");
        return false;
    }
    if (!regexName()) {
        alert("2~12자의 한글, 영문 대/소문자를 사용해 주세요. (특수기호, 공백 사용 불가)")
        return false;
    }

    if (byEmail) {
        if (!isEmailPresent()) {
            alert("이메일을 입력해 주세요.");
            return false;
        }

        if (!regexEmail()) {
            alert("유효한 이메일을 입력해 주세요.");
            return false;
        }
    }

    if (byPhonenumber) {
        if (!isPhonenumberPresent()) {
            alert("휴대전화번호를 입력해 주세요.");
        }

        if (!regexPhonenumber()) {
            alert("유효한 휴대전화번호를 입력해 주세요.");
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