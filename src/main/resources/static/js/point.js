function validateBeforeSubmit() {
    let userId = $('#userId').val().trim();
    let pointContent = $('#pointContent').val().trim();
    let point = $('#point').val().trim();

    if (userId === '') {
        alert('아이디를 입력해 주세요.');
        return false;
    }

    if (pointContent === '') {
        alert('적립금 내용을 입력해 주세요.');
        return false;
    }

    if (point === '') {
        alert('적립금을 입력해 주세요.');
        return false;
    }

    return true;
}



