$(document).ready(function() {

    // 초기화 버튼 클릭 시 검색폼 초기화
    $('.btn_wrap .reset_btn').click(function () {
        $('#searchKeyword').val(''); // 검색어 입력 초기화
        $('select').each(function() {
            $(this).val($(this).find('option:first').val()); // select 요소 초기화
        });
        $('#startDate, #endDate').val(''); // 시작일, 종료일 초기화
        $('.date_option input[type="button"]').removeClass('active'); // active 클래스 제거
    });

});