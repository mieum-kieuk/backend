$(document).ready(function() {
    toggleDateBtn();
    // 초기화 버튼 클릭 시 검색폼 초기화
    $('.btn_wrap .reset_btn').click(function () {
        resetSearchForm();
    });
});
function toggleDateBtn() {
    $('.date_option input[type="button"]').click(function() {
        $('.date_option input[type="button"]').removeClass('active');
        $(this).addClass('active');
    });
}
function resetSearchForm() {
    $('#searchKeyword').val('');
    $('select').each(function() {
        $(this).val($(this).find('option:first').val());
    });
    $('#startDate, #endDate').val(''); // 시작일, 종료일 초기화
    $('.date_option input[type="button"]').removeClass('active');
    $('#adminsSearchForm')[0].reset();

}

function validateBeforeSearch() {
    let searchKey = $('#searchKey').val();
    let searchKeyword = $('#searchKeyword').val().trim();
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

function validateBeforeDiscountSearch() {
    let searchKey = $('#searchKey').val();
    let searchKeyword = $('#searchKeyword').val().trim();

    if (searchKey === 'percent' && isNaN(searchKeyword)) {
        alert('할인율으로 검색할 경우 입력칸에 숫자만 입력해 주세요');
        return false;
    }
}