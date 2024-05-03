$(document).ready(function () {
    // add, edit
    $('input').on('focus', function() {
        var input = $(this);
        var len = input.val().length;
        input[0].setSelectionRange(len, len);
    });

    $('#detailAddress').focus(function() {
        let zipCode = $('#zipCode').val().trim();
        let basicAddress = $('#basicAddress').val().trim();

        if (zipCode === '' || basicAddress === '') {
            alert("우편번호를 먼저 입력해 주세요.");
            $('#searchZipCodeBtn').focus();
        }
    });

    $('#phonenumber2, #phonenumber3').on('focusout', function () {
        isPhoneValid();
    });

    // list
    $('#checkAll').change(function() {
        var isChecked = $(this).prop('checked');
        $('.checkbox').prop('checked', isChecked);
        updateTotalPrice();
    });



    $("#deleteBtn").click(function() {
        var checkedItems = $("input[name='addressCheckBox']:checked");
        if (checkedItems.length === 0) {
            alert("삭제할 항목을 선택해 주세요.");
        } else {
            if (confirm("정말 삭제하시겠습니까?")) {
                z
                checkedItems.closest('.address_item').remove();
                alert("선택된 주소가 삭제되었습니다.");
            }
        }
    });
});
function isAddressNameEmpty() {
    let addressName = $("#addressName").val();

    if (addressName.trim() === ''){
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
    let name = $("#name").val();

    if (name.trim() === '') {
        $('#nameMsg').text('이름을 입력해 주세요.');
        $('#nameMsg').removeClass('success error').addClass('error');
        return false;
    }

    return true;
}

function regexName() {
    let name = $('#name').val();
    let regex = /^[가-힣a-zA-Z]{2,12}$/;
    if (!regex.test(name)) {
        $('#nameMsg').text('한글, 영문 대/소문자를 사용해 주세요. (특수기호, 공백 사용 불가)');
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

// 상세주소 검사
let detailAddress = $('#detailAddress').val().trim();
if (detailAddress === '') {
    return false;
}
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
        $('#phoneNumberMsg').text('휴대전화번호를 입력해 주세요.');
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
        $('#phoneNumberMsg').text('유효한 휴대전화번호를 입력해 주세요.');
        return false;
    }
}

function validateBeforeSubmit(ㅓ) {

    // 배송지명 유효성 검사
    if (!isAddressNameEmpty()) {
        alert("배송지명을 입력해 주세요.");
        return false;
    }

    // 이름 유효성 검사
    if (!isNameEmpty()) {
        alert("이름을 입력해 주세요.");
        return false;
    } else if (!regexName()) {
        alert("유효한 이름을 입력해 주세요.");
        return false;
    }
    if (!isAddressEmpty()) {
        alert("주소를 입력해 주세요.");
    }

    $('.submit_btn').prop('disabled', true);

    return true;
}

