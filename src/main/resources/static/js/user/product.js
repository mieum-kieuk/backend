$(window).on('pageshow', function (event) {
    if (event.originalEvent.persisted || window.performance.getEntriesByType("navigation")[0].type === "back_forward") {
        $('.quant_input').val(1).trigger('change'); // 수량 초기화 후 버튼 상태 업데이트
    }
});
$(document).ready(function () {

    let url = window.location.href;
    if (!url.includes('category')) {
        $('#all').addClass('selected');
    } else {
        if (url.includes('sticker')) {
            $('#sticker').addClass('selected');
        } else if (url.includes('masking-tape')) {
            $('#masking-tape').addClass('selected');
        } else if (url.includes('phone-case')) {
            $('#phone-case').addClass('selected');
        } else {
            $('#all').addClass('selected');
        }
    }

    if (url.includes('sorted_type')) {
        let sortedType = url.split('sorted_type=')[1];
        if (sortedType.includes('&')) {
            sortedType = sortedType.split('&page')[0];
        }
        if (sortedType === '0') {
            $('#sortedType0').addClass('selected');
        } else if (sortedType === '1') {
            $('#sortedType1').addClass('selected');
        } else if (sortedType === '2') {
            $('#sortedType2').addClass('selected');
        } else if (sortedType === '3') {
            $('#sortedType3').addClass('selected');
        } else if (sortedType === '4') {
            $('#sortedType4').addClass('selected');
        }
    }
    setProductState();
    $('.display_img:first-child').addClass('selected');

    $('.display_img').click(function () {
        $('.display_img.selected').removeClass('selected');

        $(this).addClass('selected');

        let imgUrl = $(this).find('img').attr('src');
        $('.prd_img img').attr('src', imgUrl);
    });
    if ($('#productList').length > 0) {
        if ($('#productList #noDataMessage').length > 0) {
            $('footer').addClass('fixed');
        }
    }
    // 페이지 로드 시 각 quant_input 요소의 초기 값을 확인하여 감소 버튼을 초기 상태로 설정
    $('.quant_input').each(function () {
        let currentValue = parseInt($(this).val());
        let decreaseBtn = $(this).siblings('.decrease');
        if (currentValue === 1) {
            decreaseBtn.addClass('disabled');
        }
    });

    // quant_input 값 변경
    $('.quant_input').change(function () {
        let currentValue = parseInt($(this).val());
        let decreaseBtn = $(this).siblings('.decrease');

        if (currentValue === 1) {
            decreaseBtn.addClass('disabled');
        } else {
            decreaseBtn.removeClass('disabled');
        }
    });

    // 감소 버튼 클릭
    $('#decreaseBtn').click(function (event) {
        event.preventDefault();
        let input = $(this).siblings('.quant_input');
        let currentValue = parseInt(input.val());
        if (currentValue > 1) {
            input.val(currentValue - 1);
            updateTotal(); // 총 수량 및 가격 업데이트
        }
        if (currentValue === 2) { // 현재 값이 2일 때
            $(this).addClass('disabled'); // 감소 버튼을 비활성화
        }
    });

    $('#increaseBtn').click(function (event) {
        event.preventDefault();
        let input = $(this).siblings('.quant_input');
        let currentValue = parseInt(input.val());
        let decreaseBtn = $(this).siblings('.decrease');
        input.val(currentValue + 1);
        if (currentValue === 1) { // 현재 값이 1일 때
            decreaseBtn.removeClass('disabled'); // 감소 버튼을 활성화
        }
        updateTotal(); // 총 수량 및 가격 업데이트
    });

    $('.prd_info_list > li').click(function () {
        let tabWrap = $(this).find('.tab_wrap');
        $('.prd_info_list > li .tab_wrap').not(tabWrap).slideUp();
        tabWrap.slideToggle();

        let expandIcon = $(this).find('.expand_icon');
        $('.prd_info_list > li .expand_icon').not(expandIcon).text('expand_more');
        expandIcon.text(function (_, text) {
            return text === 'expand_more' ? 'expand_less' : 'expand_more';
        });

        return false;
    });


});

function setProductState() {
    // 초기화
    $('.prd_price .original_price, .prd_price .discount').removeClass('active');
    $('.buy_btn .btn1').addClass('active');
    // $('.sold_out_btn .btn1').removeClass('active');

    // 상태 설정
    if ($('.prd_price .discount').length > 0) {
        $('.prd_price .discount').addClass('active');
    } else {
        $('.prd_price .original_price').addClass('active');
        $('#CartBtn, #BuyBtn').addClass('active');
    }

    let isSoldOut = $('.sold_out_btn').length > 0;
    if (isSoldOut) {
        $(".prd_quantity").hide();
        $(".prd_total").hide();
    }

    updateTotal();
}
function updateTotal() {
    let quantity = parseInt($('.quant_input').val());
    let isDiscount = $('.prd_price .discount').hasClass('active');
    let currentPrice = isDiscount ? parseInt($('#salePrice').text().replace(/[^0-9]/g, '')) : parseInt($('#productPrice').text().replace(/[^0-9]/g, ''));
    let totalPrice = quantity * currentPrice;

    $('#totalQuantity').text(quantity + '개');
    $('#totalPrice').text(totalPrice.toLocaleString() + '원');
}


let csrfToken = $("meta[name='_csrf']").attr("content");
let csrfHeader = $("meta[name='_csrf_header']").attr("content");

//장바구니 담기
$('#CartBtn').click(function () {
    let url = window.location.href;
    let parts = url.split('/');
    let productId = parts[parts.length - 1];
    let count = $('.quant_input').val();

    $.ajax({
        type: 'POST',
        url: '/ajax/cart/add',
        data: {productId: productId, count: count},
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
            xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
        },
        success: function (result) {
            $('#cartItemCount').text(result.cartItemCount);
            Swal.fire({
                html: result.message.replace('\n', '<br>'),
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
        },
        error: function(xhr) {
            if(xhr.status == 401) {
                window.location.href = '/login';
            } else {
                Swal.fire({
                    html: '장바구니에 삼품을 담는 중 오류가 발생했습니다.<br>다시 시도해 주세요.',
                    showConfirmButton: true,
                    confirmButtonText: '확인',
                    customClass: mySwal,
                    buttonsStyling: false
                });
            }
        }
    })
});