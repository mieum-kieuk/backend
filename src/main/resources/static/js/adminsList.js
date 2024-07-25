$(document).ready(function() {
    $('.admins_list input[type="checkbox"]').click(function () {
        let isChecked = $(this).prop('checked');
        if (isChecked) {
            $('.admins_list input[type="checkbox"]').not(this).prop('checked', false);
        }
    });
});



// 폼 제출 전 유효성 검사 함수
function validateBeforeSubmit() {
    let startDatetime = $('#startDate').val().trim();
    let endDatetime = $('#endDate').val().trim();

    // 시작일과 종료일 중 하나만 입력되어 있을 경우
    if ((startDatetime === '' && endDatetime !== '') || (startDatetime !== '' && endDatetime === '')) {
        alert('시작일시와 종료일시를 모두 입력해 주세요.');
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
        alert('시작일시는 종료일시보다 이전이어야 합니다.');
        return false;
    }

    return true;
}

//선택승인
$("#authAdminBtn").click(function() {

    let admin = $('.list_content .list_item input[type=checkbox]:checked')
    if (admin.length === 0) {
        alert('승인할 관리자를 선택해 주세요.');
        return;
    }

    let isAuth = admin.closest('.list_item').find('.is_authorized');
    if(isAuth.text() === 'O') {
        alert('이미 승인된 관리자입니다.');
        return;
    }

    let adminId = admin.attr('id').split('checkbox')[1];
    let csrfToken = $("meta[name='_csrf']").attr("content");
    let csrfHeader = $("meta[name='_csrf_header']").attr("content");

    if(confirm("선택한 관리자에게 관리자 권한을 부여하시겠습니까?")) {
        // $('.loader_wrap.white').css('display', 'block');
        $.ajax({
            method: 'POST',
            url: '/ajax/admin/admins/auth',
            async: false,
            data: {adminId: adminId},
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken)
            },
            success: function (result) {
                if(result.code === 200) {
                    document.location.reload();
                }
            },
            error: function () {
                alert('승인 중 오류가 발생했습니다. 다시 시도해 주세요.');
                // $('.loader_wrap.white').css('display', 'none');
            }
        })
    } else {
        return false;
    }
});

//선택삭제
$("#deleteAdminBtn").click(function() {

    let admin = $('.list_content .list_item input[type=checkbox]:checked');
    if (admin.length === 0) {
        alert('삭제할 관리자를 선택해 주세요.');
        return;
    }

    let adminId = admin.attr('id').split('checkbox')[1];
    let csrfHeader = $("meta[name='_csrf_header']").attr("content");
    let csrfToken = $("meta[name='_csrf']").attr("content");

    if (confirm("선택한 관리자를 삭제하시겠습니까?")) {
        $.ajax({
            method: 'DELETE',
            url: '/ajax/admin/admins/delete',
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
                alert('삭제 중 오류가 발생했습니다. 다시 시도해 주세요.');
            }
        })
    } else {
        return false;
    }
});