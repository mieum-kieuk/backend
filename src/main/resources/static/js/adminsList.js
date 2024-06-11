$(document).ready(function() {
    // 전체 선택 체크박스 클릭 이벤트
    $('#selectAll').click(function () {
        let isChecked = $(this).prop('checked');
        $('tbody input[type="checkbox"]').prop('checked', isChecked);
    });

});
// 폼 제출 전 유효성 검사 함수
function validateBeforeSubmit() {
    let searchKeyword = $('#searchKeyword').val().trim();
    let startDatetime = $('#startDate').val().trim();
    let endDatetime = $('#endDate').val().trim();

    if (searchKeyword === '') {
        alert('검색어를 입력해 주세요.');
        return false;
    }

    let startDate = new Date(startDatetime);
    let endDate = new Date(endDatetime);

    if (startDate >= endDate) {
        alert('시작일시는 종료일시보다 이전이어야 합니다.');
        return false;
    }

    return true;
}
