$(document).ready(function() {
    if ($('.discount').length) {
        $('#startDate, #expireDate').datepicker({
            dateFormat: 'yy-mm-dd',
            minDate: 0  // 과거 날짜 선택 불가
        });
    } else {
        $('#startDate, #expireDate').datepicker({
            dateFormat: 'yy-mm-dd',
            maxDate: 0  // 미래 날짜 선택 불가
        });
    }

});

function setSearchDate(days) {
    if (days === 'all') {
        $('#startDate').datepicker('setDate', null);
        $('#expireDate').datepicker('setDate', null);
    } else {
        let expireDate = new Date();
        let startDate = new Date();

        if (days === 0) {
            startDate = expireDate;
        } else {
            startDate.setDate(startDate.getDate() - days);
        }

        $('#startDate').datepicker('setDate', formatDate(startDate));
        $('#expireDate').datepicker('setDate', formatDate(expireDate));
    }

    $('.hasDatepicker').removeAttr('readonly').on('click', function () {
        $(this).datepicker('show');
    });

    $('.hasDatepicker').css('pointer-events', 'auto');
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