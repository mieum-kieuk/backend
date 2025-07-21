$(document).ready(function () {

    $('#detailAddress').click(function () {
        let zipCode = $('#zipCode').val().trim();
        let basicAddress = $('#basicAddress').val().trim();

        if (zipCode === '' || basicAddress === '') {
            Swal.fire({
                html: "주소 검색을 통해<br>우편번호와 기본주소를 먼저 입력해 주세요.",
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
        }else {
            $('#detailAddress').prop('readonly', false);
        }
    });

    $('#phonenumber2, #phonenumber3').on('focusout', function () {
        isPhoneValid();
    });

    // list
    $('#checkAll').change(function () {
        let isChecked = $(this).prop('checked');
        $('.checkbox').prop('checked', isChecked);
    });
});

function isDeliveryNameEmpty() {
    let deliveryName = $("#deliveryName").val();

    if (deliveryName.trim() === '') {
        return false;
    }
    return true;
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
    let regex = /^[가-힣a-zA-Z]{2,30}$/;
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
        Swal.fire({
            text: "배송지명을 입력해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }

    // 수령인 유효성 검사
    if (!isNameEmpty()) {
        Swal.fire({
            text: "수령인을 입력해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    } else if (!regexName()) {
        Swal.fire({
            text: "수령인 이름을 정확히 입력해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }

    // 주소 유효성 검사
    if (!isAddressEmpty()) {
        Swal.fire({
            text: "주소를 입력해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }

    // 휴대전화번호 유효성 검사
    if (!isPhoneEmpty()) {
        Swal.fire({
            text: "휴대전화번호를 입력해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    } else if (!regexPhone()) {
        Swal.fire({
            text: "유효한 휴대전화번호를 입력해 주세요.",
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

//배송지 삭제
function deleteDelivery(deliveryId) {

    let csrfHeader = $("meta[name='_csrf_header']").attr("content");
    let csrfToken = $("meta[name='_csrf']").attr("content");

    Swal.fire({
        text: '삭제하시겠습니까?',
        showCancelButton: true,
        cancelButtonText: '아니요',
        confirmButtonText: '예',
        closeOnConfirm: false,
        closeOnCancel: true,
        customClass: mySwalConfirm,
        reverseButtons: true,
        buttonsStyling: false,
    }).then((result) => {
        if (result.isConfirmed) {
            $.ajax({
                type: 'DELETE',
                url: '/ajax/deliveries',
                data: {'deliveryId': deliveryId},
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(csrfHeader, csrfToken);
                },
                success: function (data) {
                    if (data.status === 200) {
                        location.reload();
                    } else {
                        Swal.fire({
                            text: data.message,
                            showConfirmButton: true,
                            confirmButtonText: '확인',
                            customClass: mySwal,
                            buttonsStyling: false
                        });
                    }
                },
                error: function () {
                    Swal.fire({
                        html: "삭제 중 오류가 발생했습니다.<br> 다시 시도해 주세요.",
                        showConfirmButton: true,
                        confirmButtonText: '확인',
                        customClass: mySwal,
                        buttonsStyling: false
                    });
                }
            });
        }
    });
}

