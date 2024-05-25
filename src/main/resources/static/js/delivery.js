$(document).ready(function () {

    $('#detailAddress').focus(function () {
        let zipCode = $('#zipCode').val().trim();
        let basicAddress = $('#basicAddress').val().trim();

        if (zipCode === '' || basicAddress === '') {
            alert("주소 검색을 통해 우편번호와 기본주소를 먼저 입력해 주세요.");
            $('#searchZipCodeBtn').focus();
        }
    });

    $('#phonenumber2, #phonenumber3').on('focusout', function () {
        isPhoneValid();
    });

    // list
    $('#checkAll').change(function () {
        var isChecked = $(this).prop('checked');
        $('.checkbox').prop('checked', isChecked);
    });

    $('#deleteBtn').click(function() {
        deleteAddresses();
    });
});

function isDeliveryNameEmpty() {
    let deliveryName = $("#deliveryName").val();

    if (deliveryName.trim() === '') {
        return false;
    }
    return true;
}

//이름 검증
function isNameValid() {
    if (!isNameEmpty()) {
        return;
    } else if (!regexName()) {
        return;
    }
    $('#nameMsg').text('');
    return;
}

function isNameEmpty() {
    let name = $("#recipientName").val();

    if (name.trim() === '') {
        return false;
    }

    return true;
}

function regexName() {
    let name = $('#recipientName').val();
    let regex = /^[가-힣a-zA-Z]{2,12}$/;
    if (!regex.test(name)) {
        return false;
    }

    return true;
}

function isAddressEmpty() {

    // 우편번호 검사
    let zipCode = $('#zipCode').val().trim();
    if (zipCode === '') {
        return false;
    }

    // 기본주소 검사
    let basicAddress = $('#basicAddress').val().trim();
    if (basicAddress === '') {
        return false;
    }

    return true;
}


//휴대전화번호 검증
function isPhoneValid() {
    if (!isPhoneEmpty()) {
        return;
    } else if (!regexPhone()) {
        return;
    }

    $('#phoneNumberMsg').text('');
    return;
}

function isPhoneEmpty() {
    let phonenumber2 = $('#phonenumber2').val();
    let phonenumber3 = $('#phonenumber3').val();

    if (phonenumber2.trim() === '' && phonenumber3.trim() === '') {
        return false;
    }

    return true;
}

function regexPhone() {
    let phonenumber2 = $('#phonenumber2').val();
    let phonenumber3 = $('#phonenumber3').val();
    let regex1 = /^[0-9]{3,4}$/;
    let regex2 = /^[0-9]{4}$/;
    if (regex1.test(phonenumber2) && regex2.test(phonenumber3)) {
        return true;
    } else {
        return false;
    }
}

function validateBeforeSubmit() {

    // 배송지명 유효성 검사
    if (!isDeliveryNameEmpty()) {
        alert("배송지명을 입력해 주세요.");
        return false;
    }

    // 수령인 유효성 검사
    if (!isNameEmpty()) {
        alert("수령인을 입력해 주세요.");
        return false;
    } else if (!regexName()) {
        alert("유효한 수령인을 입력해 주세요.");
        return false;
    }

    // 주소 유효성 검사
    if (!isAddressEmpty()) {
        alert("주소를 입력해 주세요.");
        return false;
    }

    // 휴대전화번호 유효성 검사
    if (!isPhoneEmpty()) {
        alert("휴대전화번호를 입력해 주세요.");
        return false;
    } else if (!regexPhone()) {
        alert("유효한 휴대전화번호를 입력해 주세요.");
        return false;
    }

    $('.submit_btn').prop('disabled', true);

    return true;
}


