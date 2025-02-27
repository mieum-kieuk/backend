$(document).ready(function() {
    $('.admins_list input[type="checkbox"]').click(function () {
        let isChecked = $(this).prop('checked');
        if (isChecked) {
            $('.admins_list input[type="checkbox"]').not(this).prop('checked', false);
        }
    });
});

// 폼 제출 전 유효성 검사
function validateBeforeSubmit() {
    let startDatetime = $('#startDate').val().trim();
    let endDatetime = $('#endDate').val().trim();

    // 시작일과 종료일 중 하나만 입력되어 있을 경우
    if ((startDatetime === '' && endDatetime !== '') || (startDatetime !== '' && endDatetime === '')) {
        Swal.fire({
            text: "시작일시와 종료일시를 모두 입력해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }

    // 시작일과 종료일이 모두 비어 있을 경우
    if (startDatetime === '' && endDatetime === '') {
        // 다른 유효성 검사를 통과하지 않아도 상관 없음
        return true;
    }

    let startDate = new Date(startDatetime);
    let endDate = new Date(endDatetime);

    if (startDate > endDate) {
        Swal.fire({
            text: "시작일시는 종료일시보다 이전이어야 합니다.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }

    return true;
}

// 선택승인
$("#authAdminBtn").click(function() {

    let admin = $('.list_content .list_item input[type=checkbox]:checked')
    if (admin.length === 0) {
        Swal.fire({
            text: "승인할 관리자를 선택해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return;
    }

    let isAuth = admin.closest('.list_item').find('.isAuthorized');
    if (isAuth.text() === 'O') {
        Swal.fire({
            text: "이미 승인된 관리자입니다.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return;
    }

    let loader = $('#authAdminBtn .loader_wrap.white');
    let adminId = admin.attr('id').split('checkbox')[1];
    let csrfToken = $("meta[name='_csrf']").attr("content");
    let csrfHeader = $("meta[name='_csrf_header']").attr("content");

    Swal.fire({
        text: "선택한 관리자에게 관리자 권한을 부여하시겠습니까?",
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
            $('#authAdminBtn').prop('disabled', true);
            $('#authAdminBtn').append(`
            <div class="loader_wrap white" style="display: none;">
                <div class="loader"></div>
            </div>`
            );
            let loader = $('#authAdminBtn .loader_wrap.white');
            loader.css('display', 'block');
        }
        $.ajax({
            method: 'POST',
            url: '/ajax/admin/admin/auth',
            async: true,
            data: {adminId: adminId},
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken);
            },
            success: function (result) {
                $('#authAdminBtn').prop('disabled', false);
                loader.css('display', 'none');
                if (result.code === 200) {
                    document.location.reload();
                }
            },
            error: function () {
                Swal.fire({
                    text: "승인 중 오류가 발생했습니다. 다시 시도해 주세요.",
                    showConfirmButton: true,
                    confirmButtonText: '확인',
                    customClass: mySwal,
                    buttonsStyling: false
                });
                $('#authAdminBtn').prop('disabled', false);
                loader.css('display', 'none');
            }
        });
    });
});

// 선택삭제
$("#deleteAdminBtn").click(function() {

    let admin = $('.list_content .list_item input[type=checkbox]:checked');
    if (admin.length === 0) {
        Swal.fire({
            text: "삭제할 관리자를 선택해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return;
    }

    let adminId = admin.attr('id').split('checkbox')[1];
    let csrfHeader = $("meta[name='_csrf_header']").attr("content");
    let csrfToken = $("meta[name='_csrf']").attr("content");

    Swal.fire({
        text: "선택한 관리자를 삭제하시겠습니까?",
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
                method: 'DELETE',
                url: '/ajax/admin/admin',
                data: {adminId: adminId},
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(csrfHeader, csrfToken);
                },
                success: function (result) {
                    if(result.code === 200) {
                        document.location.reload();
                    }
                },
                error: function () {
                    Swal.fire({
                        text: "삭제 중 문제가 발생했습니다. 다시 시도해 주세요.",
                        showConfirmButton: true,
                        confirmButtonText: '확인',
                        customClass: mySwal,
                        buttonsStyling: false
                    });
                }
            });
        }
    })
});
