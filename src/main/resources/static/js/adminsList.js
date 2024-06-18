$(document).ready(function() {
    $('tbody input[type="checkbox"]').click(function () {
        let isChecked = $(this).prop('checked');
        if (isChecked) {
            $('tbody input[type="checkbox"]').not(this).prop('checked', false);
        }
    });
    $('#startDate, #endDate').datepicker({
        dateFormat: 'yy-mm-dd',
        maxDate: 0

    });

    // 초기화 버튼 클릭 시 검색폼 초기화
    $('.btn_wrap .reset_btn').click(function () {
        $('#searchKeyword').val(''); // 검색어 입력 초기화
        $('#startDate, #endDate').val(''); // 시작일, 종료일 초기화
    });
});

function setSearchDate(days) {
    if (days === 'all') {
        $('#startDate').datepicker('setDate', null);
        $('#endDate').datepicker('setDate', null);
    } else {
        let endDate = new Date();
        let startDate = new Date();

        if (days === 0) {
            startDate = endDate;
        } else {
            startDate.setDate(startDate.getDate() - days);
        }

        $('#startDate').datepicker('setDate', formatDate(startDate));
        $('#endDate').datepicker('setDate', formatDate(endDate));
    }
}

function formatDate(date) {
    let d = new Date(date),
        month = '' + (d.getMonth() + 1),
        day = '' + d.getDate(),
        year = d.getFullYear();

    if (month.length < 2) month = '0' + month;
    if (day.length < 2) day = '0' + day;

    return [year, month, day].join('-');
}

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

    let admin = $('.admins_table tbody tr input[type=checkbox]:checked')
    if (admin.length === 0) {
        alert('승인할 관리자를 선택해 주세요.');
        return;
    }

    let isAuth = admin.closest('tr').find('.is_authorized');
    if(isAuth.text() === 'O') {
        alert('이미 승인된 관리자입니다.');
        return;
    }

    let adminId = admin.attr('id').split('checkbox')[1];
    let csrfToken = $("meta[name='_csrf']").attr("content");
    let csrfHeader = $("meta[name='_csrf_header']").attr("content");

    if(confirm("선택한 관리자에게 관리자 권한을 부여하시겠습니까?")) {
        $.ajax({
            method: 'POST',
            url: '/ajax/admin/admins/auth',
            async: false,
            data: {'adminId': adminId},
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken)
            },
            success: function (data) {
                if(data.code === 200) {
                    document.location.reload();
                }
            },
            error: function () {
                alert('승인중 오류가 발생했습니다. 다시 시도해 주세요.');
            }
        })
    } else {
        return false;
    }
});

//선택삭제
$("#deleteAdminBtn").click(function() {

    let admin = $('.admins_table tbody tr input[type=checkbox]:checked');
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
            async: false,
            data: {'adminId': adminId},
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken)
            },
            success: function (data) {
                if(data.code === 200) {
                    document.location.reload();
                } else {
                    alert(data.message);
                }
            },
            error: function () {
                alert('삭제중 오류가 발생했습니다. 다시 시도해 주세요.');
            }
        })
    } else {
        return false;
    }
});