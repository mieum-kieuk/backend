let isNamePresent = false;
let isEmailPresent = false;
let regexEmail = false;
let isPhonenumberPresent = false;
let regexPhonenumber = false;

$(document).ready(function () {

    // 이름 입력란 유효성 검사
    $('#name').on('focusout', function () {
        let name = $(this).val();

        if (name.trim() === '') {
            isNamePresent = false;
            return;
        }

        isNamePresent = true;
    });

    // 이메일 입력란 유효성 검사
    $('#email').on('focusout', function () {
        let email = $(this).val();

        if (email.trim() === '') {
            isEmailPresent = false;
            return;
        }

        isEmailPresent = true;

        const regex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
        if (!regex.test(email)) {
            regexEmail = false;
            return;
        }

        regexEmail = true;
    });

    // 휴대전화번호 입력란 유효성 검사
    $('#phonenumber2, #phonenumber3').on('focusout', function () {
        let phonenumber2= $('#phonenumber2').val();
        let phonenumber3= $('#phonenumber3').val();

        if (phonenumber2.trim() === '' || phonenumber3.trim() === '') {
            isPhonenumberPresent = false;
            return;
        }

        isPhonenumberPresent = true;

        const regex1 = /^[0-9]{3,4}$/;
        const regex2 = /^[0-9]{4}$/;
        if (!regex1.test(phonenumber2) || !regex2.test(phonenumber3)) {
            regexPhonenumber = false;
            return;
        }

        regexPhonenumber = true;
    });

})

function validationCheck() {

    let byEmail = $('#findType1').is(':checked');
    let byPhonenumber = $('#findType2').is(':checked');

    if(!isNamePresent) {
        alert("이름을 입력해 주세요.");
        return false;
    }

    if(byEmail) {
        if (!isEmailPresent) {
            alert("이메일을 입력해 주세요.");
            return false;
        }

        if (!regexEmail) {
            alert("이메일 형식이 잘못 되었습니다.");
            return false;
        }
    }

    if(byPhonenumber) {
        if (!isPhonenumberPresent) {
            alert("휴대전화번호를 입력해 주세요.");
            return false;
        }

        if (!regexPhonenumber) {
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

$(document).ready(function() {

//    $("#totalIds").text(totalIds);
//    $("#userName").text(name);
    $("#userEmail").text(email);

    var maskedId = userId.substring(0, 3) + '*'.repeat(userId.length - 3);
    $("#userId").text(maskedId);

    $("#memberGrade").text(memberGrade);
    $("#joinDate").text(joinDate);

    function redirectToLogin() {
        window.location.href = "/login";
    }

    function redirectToFindPassword() {
        window.location.href = "/find_password";
    }

    $("#loginBtn").click(function() {
        redirectToLogin();
    });

    $("#findBtn").click(function() {
        redirectToFindPassword();
    });
});
