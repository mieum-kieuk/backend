$(function () {
    let now = 0
    const slideCount = $('.main_slide > ul > li').length
    const slideList = $('.main_slide > ul > li')

    setInterval(function () {
        let next = (now + 1) % slideCount

        $(slideList[now]).fadeOut(3000)
        $(slideList[next]).fadeIn(3000)
        now = next
    }, 10000)
})
$(document).ready(function() {

    $('input[type="text"]').on('input', function (e) {
        let maxLength = $(this).attr('maxlength');
        let textLength = e.target.value.length;

        if (textLength > maxLength) {
            let trimmedValue = e.target.value.substring(0, maxLength);
            $(this).val(trimmedValue);
        }
    });
    $('.search_btn').click(function(event) {
        var keyword = $('.search_form_wrap input[type="text"]').val().trim();

        if (keyword === '') {
            event.preventDefault();
            alert('검색어를 입력해 주세요.');
        }
    });
    $('.search_btn2').click(function(event) {
        var keyword = $('#search_complete input[type="text"]').val().trim();

        if (keyword === '') {
            event.preventDefault();
            alert('검색어를 입력해 주세요.');
        }
    });
    $('.search button').click(function() {
        $('.search_popup').fadeIn();
    });

    $('.search_bg').click(function() {
        $('.search_popup').fadeOut();
    });

    $('#search_complete').click(function(e) {
        e.stopPropagation();
    });

    // $(window).load(function(){
    //     $('.loader').delay('1000').fadeOut();
    // });
    $('.side_menu.menu .depth1 > li > a').click(function(event) {
        let depth1 = $(this).parent('li');

        depth1.find('.depth2').slideToggle();
    });
});
