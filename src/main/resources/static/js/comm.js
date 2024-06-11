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

    let activeItem = localStorage.getItem('activeMenuItem');
    if (activeItem) {
        let activeLink = $('.side_menu.menu a[href="' + activeItem + '"]');
        activeLink.addClass('active').closest('li').addClass('active');
        activeLink.parents('ul').slideDown();
    }

    // 메뉴 항목 클릭 시 로컬 스토리지에 href 저장 및 active 클래스 적용
    $('.side_menu.menu a').click(function (event) {
        event.preventDefault(); // 기본 동작 막기

        var href = $(this).attr('href');
        var isActive = $(this).hasClass('active');

        if (isActive) {
            // 클릭한 메뉴가 이미 활성화되어 있으면 닫기
            $(this).removeClass('active').closest('li').removeClass('active').find('.depth2').slideUp();
            localStorage.removeItem('activeMenuItem'); // 로컬 스토리지에서 제거
        } else {
            // 클릭한 메뉴가 비활성화되어 있으면 열기
            localStorage.setItem('activeMenuItem', href);

            // 클릭된 항목만 활성화하고 서브메뉴 열기
            $(this).addClass('active').closest('li').addClass('active').find('.depth2').slideDown();
        }

        // 페이지 이동
        window.location.href = href;
    });


    $('#startDate, #endDate').datepicker({
        dateFormat: 'yy-mm-dd'
    });

    // 초기화 버튼 클릭 시 검색폼 초기화
    $('.btn_wrap .reset_btn').click(function () {
        $('#searchKey').val(''); // 검색어 선택 초기화
        $('#searchKeyword').val(''); // 검색어 입력 초기화
        $('#startDate, #endDate').val(''); // 시작일, 종료일 초기화
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