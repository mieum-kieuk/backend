// 이름 입력란 유효성 검사
function isNamePresent() {
    let name = $('#name').val();
    let result = name.trim() === '' ? false : true;
    return result;
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

    let result = phonenumber2.trim() === '' || phonenumber3.trim() === '' ? false : true;
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

function validationCheck() {

    let byEmail = $('#findType1').is(':checked');
    let byPhonenumber = $('#findType2').is(':checked');

    if (!isNamePresent()) {
        alert("이름을 입력해 주세요.");
        return false;
    }

    if (byEmail) {
        if (!isEmailPresent()) {
            alert("이메일을 입력해 주세요.");
            return false;
        }

        if (!regexEmail()) {
            alert("이메일 형식이 잘못되었습니다.");
            return false;
        }
    }

    if (byPhonenumber) {
        if (!isPhonenumberPresent()) {
            alert("휴대전화번호를 입력해 주세요.");
            return false;
        }

        if (!regexPhonenumber()) {
            alert("휴대전화번호 형식에 맞게 입력해 주세요.");
            return false;
        }
    }
}

$('input[name="findType"]').change(function () {
    if ($('#findType1').is(':checked')) {
        $('#name').val('');
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
        $('.field-error').empty();
    }
})