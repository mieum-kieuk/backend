$(document).ready(function() {
    $('#startDate, #endDate').datepicker({
        dateFormat: 'yy-mm-dd',
        maxDate: 0

    });
    $('.date_option input[type="button"]').click(function() {
        $('.date_option input[type="button"]').removeClass('active');
        $(this).addClass('active');
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

