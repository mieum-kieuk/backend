function validateBeforeSubmit() {
    let discountValue = $('#value').val().trim();
    let discountType = $('input[name=type]:checked').val();

    if (discountValue === '') {
        alert('할인 설정을 입력해 주세요.');
        return false;
    }

    if (parseInt(discountValue) < 1) {
        alert('0보다 큰 값을 입력해 주세요.');
        return false;
    }

    if (discountType === 'RATE') {
        if (!/^[1-9][0-9]?$|^100$/.test(discountValue)) {
            alert('1부터 100사이의 값을 입력해 주세요.');
            return false;
        }
    } else if (discountType === 'FIX') {
        if (parseInt(discountValue) < 1) {
            alert('0보다 큰 값을 입력해 주세요.');
            return false;
        }
    }

    return true;
}


