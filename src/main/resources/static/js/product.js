$(document).ready(function() {
    $('#increaseBtn').click(function(event) {
        event.preventDefault();
        var input = $(this).siblings('.quant_input');
        var currentValue = parseInt(input.val());
        input.val(currentValue + 1);
    });

    $('#decreaseBtn').click(function(event) {
        event.preventDefault();
        var input = $(this).siblings('.quant_input');
        var currentValue = parseInt(input.val());
        if (currentValue > 1) {
            input.val(currentValue - 1);
        }
    });


});
$(document).ready(function() {
    $('.tab_tit').click(function() {
        var tabWrap = $(this).siblings('.tab_wrap');
        $('.prd_info_tab .tab_wrap:visible').not(tabWrap).slideUp();
        tabWrap.slideToggle();

        // Toggle expand icon
        $(this).find('.expand_icon').text(function(_, text) {
            return text === 'expand_more' ? 'expand_less' : 'expand_more';
        });

        return false;
    });
});

