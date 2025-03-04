$(document).ready(function () {

    const membershipList = $('.list_content');
    const width = $(window).innerWidth() - 490;
    membershipList.css('min-width', width + 'px');

    // 회원 등급명 중복 검사
    $('#name').on('keyup', function () {
        isNameValid();
    });

    $("select").each(function () {
        if ($(this).find("option:selected").val() !== "") {
            $(this).addClass("selected");
        }
    });

    $("select").on("change", function () {
        if ($(this).find("option:selected").val() !== "") {
            $(this).addClass("selected");
        } else {
            $(this).removeClass("selected");
        }
    });

    // 체크박스 상태 변경
    $('#membershipList .list.membership').on('click', '.item.check input[type="checkbox"]', function () {
        $('.list.membership .item.check input[type="checkbox"]').not(this).prop('checked', false);
    });

    $('.list_head .item.check input[type="checkbox"]').on('click', function () {
        let isChecked = $(this).prop('checked').attr('id').split('checkbox')[1];
        $('.list .item.check input[type="checkbox"]').prop('checked', isChecked);
    });

    $('.list.membership .list_item').each(function () {
        let level = $(this).find('.item.level').text().trim();
        let checkbox = $(this).find('input[name="checkBox"]');

        if (level === "1") {
            checkbox.prop('disabled', true); // 체크박스 비활성화
        }
    });

    $('#membershipList #deleteMembershipBtn').on('click', function () {
        let checkedBox = $('#membershipList input[type="checkbox"]:checked');

        if (checkedBox.length === 0) {
            Swal.fire({
                text: "삭제할 회원 등급을 선택해 주세요.",
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
            return;
        }

        let checkboxId = checkedBox.attr('id'); // 체크된 체크박스의 ID 가져오기
        let membershipId = checkboxId.replace('checkbox', ''); // 'checkbox123' → '123'

        deleteMembership(membershipId);
    });

    $('#membershipDetails #deleteMembershipBtn').on('click', function () {
        let membershipId = $(this).val();
        console.log(membershipId);
        deleteMembership(membershipId);
    });

    $('#addMembershipForm').submit(async function (event) {
        event.preventDefault();
        const isValid = await validateBeforeSubmit();

        if (isValid) {
            this.submit();
        }
    });

    $('#editMembershipForm').submit(async function (event) {
        event.preventDefault();
        const isValid = await validateBeforeSubmit();

        if (isValid) {
            this.submit();
        }
    });
});

let csrfHeader = $("meta[name='_csrf_header']").attr("content");
let csrfToken = $("meta[name='_csrf']").attr("content");

let isAvailableName = false;
let originalName = $('#editMembershipForm').length > 0 ? $('#editMembershipForm #name').val().trim() : '';

// 회원 등급명 중복검사
async function isNameValid() {
    let name = $('#name').val().trim();
    let nameRegex = /^[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9\s]+$/;

    if (name === '') {
        return false;
    }
    if (!nameRegex.test(name)) {
        return false;
    }

    if ($('#editMembershipForm').length > 0) {
        if (name === originalName) {
            return true;
        }
    }
    try {
        const result = await $.ajax({
            type: 'POST',
            url: '/ajax/admin/membership/check/name',
            data: {'name': name},
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken);
            }
        });

        if (result.code == 200) {
            if (result.message === '사용 가능한 회원 등급명입니다.') {
                isAvailableName = true;
                return true;
            } else if (result.message === '이미 존재하는 회원 등급명입니다.') {
                isAvailableName = false;
                return false;
            }
        } else {
            return false;
        }
    } catch (error) {
        Swal.fire({
            html: "회원 등급명 중복 확인 중 오류가 발생했습니다.<br>다시 시도해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }
}

// 폼 제출 전 유효성 검사
async function validateBeforeSubmit() {
    let membershipName = $('#name').val().trim();
    let nameRegex = /^[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9\s]+$/;
    let pointRate = $('#pointRate').val().trim();
    let minAmountSpent = $('#minAmountSpent').val().trim();

    if (membershipName === '') {
        Swal.fire({
            text: "회원 등급명을 입력해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    } else if (!nameRegex.test(membershipName)) {
        Swal.fire({
            text: "회원 등급명은 한글, 영문, 숫자, 공백만 허용됩니다.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }

    const isValid = await isNameValid();
    if (!isValid) {
        Swal.fire({
            text: "이미 존재하는 회원 등급명입니다.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }

    if (pointRate === '') {
        Swal.fire({
            text: "적립률을 입력해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }

    let pointRateRegex = /^(?:1|[1-9]\d?|100)$/;
    if (!pointRateRegex.test(pointRate)) {
        Swal.fire({
            text: "유효한 최소 소비 금액을 입력해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }

    if (minAmountSpent === '') {
        Swal.fire({
            text: "최소 소비 금액을 입력해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }

    if (parseFloat(minAmountSpent) < 0) {
        Swal.fire({
            text: "유효한 최소 소비 금액을 입력해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }

    return true;
}

// 회원 등급 단건 삭제
function deleteMembership(membershipId) {

    Swal.fire({
        text: "삭제하시겠습니까?",
        showCancelButton: true,
        cancelButtonText: '아니요',
        confirmButtonText: '예',
        customClass: mySwalConfirm,
        reverseButtons: true,
        buttonsStyling: false,
    }).then((result) => {
        if (result.isConfirmed) {
            $.ajax({
                type: 'DELETE',
                url: '/ajax/admin/membership',
                async: false,
                data: {'membershipId': membershipId},
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(csrfHeader, csrfToken)
                },
                success: function (data) {
                    if (data.code === 200) {
                        window.location.href = '/admin/memberships';
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
                        html: "삭제중 오류가 발생했습니다.<br>다시 시도해 주세요.",
                        showConfirmButton: true,
                        confirmButtonText: '확인',
                        customClass: mySwal,
                        buttonsStyling: false
                    });
                }
            })
        }
    });
}
