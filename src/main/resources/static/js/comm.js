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
    $('.search button').click(function(e) {
        e.preventDefault();

        $('.search_bg').fadeIn();
        $('.search_form_wrap').fadeIn();

        e.stopPropagation();
    });


    $(document).click(function(e) {
        if (!$(e.target).closest('.search_form_wrap').length && !$(e.target).hasClass('search')) {
            $('.search_bg').fadeOut();
            $('.search_form_wrap').fadeOut();
        }
    });
});
