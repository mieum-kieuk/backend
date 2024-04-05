$(document).ready(function() {
    $('#increaseBtn').click(function(event) {
        event.preventDefault();
        let input = $(this).siblings('.quant_input');
        let currentValue = parseInt(input.val());
        input.val(currentValue + 1);
    });

    $('#decreaseBtn').click(function(event) {
        event.preventDefault();
        let input = $(this).siblings('.quant_input');
        let currentValue = parseInt(input.val());
        if (currentValue > 1) {
            input.val(currentValue - 1);
        }
    });

    $('.tab_tit').click(function() {
        let tabWrap = $(this).siblings('.tab_wrap');
        $('.prd_info_tab .tab_wrap:visible').not(tabWrap).slideUp();
        tabWrap.slideToggle();
        
        let expandIcon = $(this).find('.expand_icon');
        $('.tab_tit .expand_icon').not(expandIcon).text('expand_more'); 
        expandIcon.text(function(_, text) {
            return text === 'expand_more' ? 'expand_less' : 'expand_more';
        });

        return false;
    });

    $('.wish').click(function() {
        $(this).toggleClass('filled');
        if ($(this).hasClass('filled')) {
            $(this).text('favorite');
        } else {
            $(this).hasClass('wish');
        }
    });
});


