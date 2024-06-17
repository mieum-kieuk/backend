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
