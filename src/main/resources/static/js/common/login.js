// 아이디 입력란 유효성 검사
function isLoginIdPresent() {
    let loginId = $("#loginId").val();
    let result = loginId.trim() === '' ? false : true;
    return result;
}

// 비밀번호 입력란 유효성 검사
function isPasswordPresent() {
    let password = $("#password").val();
    let result = password.trim() === '' ? false : true;
    return result;
}

function validateBeforeSubmit() {
    if (!isLoginIdPresent()) {
        Swal.fire({
            text: "아이디를 입력해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }
    if (!isPasswordPresent()) {
        Swal.fire({
            text: "비밀번호를 입력해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }

    $('.submit_btn').prop('disabled', true);
    return true;
}