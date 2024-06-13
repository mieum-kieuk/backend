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

    $('#search_complete').click(function(e) {ㅡ
        e.stopPropagation();
    });

    // $(window).load(function(){
    //     $('.loader').delay('1000').fadeOut();
    // });

});


$(document).ready(function() {
    // 현재 URL을 가져와서 pageLocation에 저장
    var pageLocation = String(document.location).split('/');
    var fileName = pageLocation[pageLocation.length - 1];
    var fileDoc = pageLocation[pageLocation.length - 2];
    var fullPath = '/' + fileDoc + '/' + fileName;

    // 현재 페이지에 맞는 메뉴 항목을 활성화
    var activeLink = $('.side_menu.menu .depth2 a[href="' + fullPath + '"]');
    if (activeLink.length > 0) {
        activeLink.addClass('active');
        activeLink.closest('.depth2').slideDown();
        activeLink.closest('.depth1 > li').addClass('active');
    }

    // 메뉴 클릭 시 toggle 및 localStorage에 href 저장
    $('.side_menu.menu .depth1 > li > a').click(function(event) {
        var depth1 = $(this).parent('li');

        // 모든 항목에서 .active 클래스 제거
        $('.side_menu.menu .depth1 > li').not(depth1).removeClass('active').find('.depth2').slideUp();

        if (depth1.hasClass('active')) {
            depth1.removeClass('active').find('.depth2').slideUp();
        } else {
            depth1.addClass('active').find('.depth2').slideDown();
        }

        var href = $(this).attr('href');
        localStorage.setItem('activeMenuItem', href);
    });

    // 로컬 스토리지에 저장된 메뉴 항목 처리
    var activeItem = localStorage.getItem('activeMenuItem');
    if (activeItem) {
        var storedActiveLink = $('.side_menu.menu a[href="' + activeItem + '"]');
        if (storedActiveLink.length > 0) {
            storedActiveLink.addClass('active');
            storedActiveLink.closest('.depth2').slideDown();
            storedActiveLink.closest('.depth1 > li').addClass('active');
        }
    }
});
