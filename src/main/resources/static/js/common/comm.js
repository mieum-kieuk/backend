const mySwal = {
    container: 'my-swal-container',
    popup: 'my-swal-popup',
    htmlContainer: 'my-swal-text',
    confirmButton: 'my-swal-confirm-button',
    actions: 'my-swal-actions',
};
const mySwalConfirm = {
    container: 'my-swal-confirm',
    popup: 'my-swal-popup',
    htmlContainer: 'my-swal-text',
    confirmButton: 'my-swal-cf-confirm-button',
    cancelButton: 'my-swal-cf-cancel-button',
    actions: 'my-swal-cf-actions',
    reverseButton: true,
};
// $(function () {
//     let now = 0
//     const slideCount = $('.main_slide > ul > li').length
//     const slideList = $('.main_slide > ul > li')
//     slideList.hide().eq(now).show();
//
//     setInterval(function () {
//         let next = (now + 1) % slideCount
//
//         $(slideList[now]).fadeOut(3000)
//         $(slideList[next]).fadeIn(3000)
//         now = next
//     }, 10000)
// })
$(document).ready(function () {

    const wrapper = $('.wrapper');
    const width = $(window).innerWidth() - 490;
    wrapper.css('min-width', width + 'px');

    $(window).on('scroll', function () {

        if ($(this).scrollTop() > 80) {
            $('#header').addClass('scrolled');
        } else {
            $('#header').removeClass('scrolled');
        }
    });

    initializeDropdownMenus();

    $('input[type="text"]').on('input', function (e) {
        let maxLength = $(this).attr('maxlength');
        let textLength = e.target.value.length;

        if (textLength > maxLength) {
            let trimmedValue = e.target.value.substring(0, maxLength);
            $(this).val(trimmedValue);
        }
    });
    $('.phone_number input[type="number"]').on('input', function () {
        let maxLength = $(this).attr('maxlength');
        let value = $(this).val();

        if (value.length > maxLength) {
            $(this).val(value.slice(0, maxLength));
        }
    });
    $('#phonenumber3').on('input', function () {
        let value = $(this).val();
        if (value.length > 4) {
            $(this).val(value.slice(0, 4)); // 최대 4자리까지만 입력 가능
        }
    });
    $('.search_btn').click(function (event) {
        let keyword = $('.search_form_wrap input[type="text"]').val().trim();

        if (keyword === '') {
            event.preventDefault();
            Swal.fire({
                text: '검색어를 입력해 주세요.',
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
        }
    });
    $('.search_btn2').click(function (event) {
        let keyword = $('#search_complete input[type="text"]').val().trim();

        if (keyword === '') {
            event.preventDefault();
            Swal.fire({
                text: '검색어를 입력해 주세요.',
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
        }
    });
    $('.search button').click(function () {
        $('.search_popup').fadeIn();
    });

    $('.search_bg').click(function () {
        $('.search_popup').fadeOut();
    });

    $('#search_complete').click(function (e) {
        e.stopPropagation();
    });

    initializeSideMenu();

    // 모바일일 때만 작동
    $(".hamburger").on("click", function () {
        $(this).toggleClass("active");
        $(".mobile_menu_bg").toggleClass("active"); // 메뉴 열고 닫기
    });
    function isMobile() {
        return window.innerWidth <= 768;
    }

    $(".depth1_menu > a, .depth1_menu > span").on("click", function (e) {
        if (isMobile()) {
            const depth1 = $(this).parent();
            const hasDepth2 = depth1.find(".depth2").length > 0;

            if (hasDepth2) {
                e.preventDefault();
                depth1.toggleClass("active");
                depth1.siblings().removeClass("active");
            }
        }
    });
});
function initializeDropdownMenus() {
    // Document 클릭 이벤트
    $(document).on('click', function (event) {
        closeDropdownMenus(event);
    });

    // 메뉴 토글 클릭 이벤트
    $(document).on('click', '.menu_toggle', function () {
        toggleDropdownMenu($(this));
    });

    // 드롭다운 메뉴 내 버튼 클릭 이벤트
    $(document).on('click', '.dropdown_menu button', function () {
        closeDropdownMenus();
    });
}

function closeDropdownMenus(event) {
    let dropdownMenus = $('.dropdown_menu');

    // 메뉴 토글 또는 드롭다운 메뉴 내부를 클릭한 경우, 함수 종료
    if (event && $(event.target).closest('.menu_toggle, .dropdown_menu').length) {
        return;
    }

    // 모든 드롭다운 메뉴를 닫기
    dropdownMenus.removeClass('show');
}

function toggleDropdownMenu(toggleElement) {
    let dropdownMenu = toggleElement.siblings('.dropdown_menu');

    // 다른 모든 드롭다운 메뉴를 닫기
    $('.dropdown_menu').not(dropdownMenu).removeClass('show');

    // 해당 드롭다운 메뉴 토글
    dropdownMenu.toggleClass('show');
}

function initializeSideMenu() {
    let url = window.location.pathname;

    // depth2 메뉴 처리
    $('.side_menu .depth2 li[data-path]').each(function () {
        let dataPath = $(this).data('path');
        if (dataPath && url.includes('/' + dataPath)) {
            $(this).addClass('active');
            $(this).closest('.depth2').css('display', 'block');
        }
    });

    // depth2 없는 depth1 li 처리
    $('.side_menu .depth1 > li').each(function () {
        let depth2 = $(this).find('.depth2');
        let dataPath = $(this).data('path');

        if (depth2.length === 0 && dataPath && url.includes('/' + dataPath)) {
            $(this).addClass('active');
        }
    });

    // depth2 토글 이벤트
    $('.side_menu .depth1 > li > a').click(function (event) {
        let depth2 = $(this).siblings('.depth2');
        if (depth2.length > 0) {
            event.preventDefault();
            depth2.slideToggle();
            $('.side_menu .depth2').not(depth2).slideUp();
            $('.side_menu .depth1 > li').not($(this).parent()).removeClass('active');
            $(this).parent().toggleClass('active');
        }
    });
}
