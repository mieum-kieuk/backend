$(document).ready(function() {
    // 쿠폰 종류 선택에 따라 입력란 표시
    $('input[name="couponType"]').on('change', function() {
        if ($(this).val() === 'rate') {
            $('.discountInput.rateInput').show();
            $('.discountInput.fixedInput').hide();
        } else if ($(this).val() === 'fixed') {
            $('.discountInput.rateInput').hide();
            $('.discountInput.fixedInput').show();
        }
    });

    $(document).on('click', function(event) {
        var dropdownMenus = $('.dropdown_menu');

        if ($(event.target).closest('.menu_toggle, .dropdown_menu').length) {
            return;
        }

        dropdownMenus.removeClass('show');
    });

    $('.menu_toggle').click(function () {
        var dropdownMenu = $(this).siblings('.dropdown_menu');
        $('.dropdown_menu').not(dropdownMenu).removeClass('show');
        dropdownMenu.toggleClass('show');
    });


    $('#selectAll').click(function () {
        if ($(this).prop('checked')) {
            $('.coupon_table tbody input[type="checkbox"]').prop('checked', true);
        } else {
            $('.coupon_table tbody input[type="checkbox"]').prop('checked', false);
        }
    });

    let currentDate = new Date();

    let minDate = currentDate.toISOString().slice(0,16);
    $('#startDatetime').attr('min', minDate);
    $('#endDatetime').attr('min', minDate);
});