$(document).ready(function () {

    let url = window.location.href;
    if (!url.includes('category')) {
        $('#all').addClass('selected');
    } else {
        if (url.includes('cafe')) {
            $('#cafe').addClass('selected');
        } else if (url.includes('ocean')) {
            $('#ocean').addClass('selected');
        } else if (url.includes('sunset')) {
            $('#sunset').addClass('selected');
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

    $('.display_img').click(function () {
        $('.display_img.selected').removeClass('selected');

        $(this).addClass('selected');

        let imgUrl = $(this).find('img').attr('src');
        $('.prd_img img').attr('src', imgUrl);
    });


    $('#increaseBtn').click(function (event) {
        event.preventDefault();
        let input = $(this).siblings('.quant_input');
        let currentValue = parseInt(input.val());
        input.val(currentValue + 1);
        updateTotal(); // 총 수량 및 가격 업데이트
    });

    $('#decreaseBtn').click(function (event) {
        event.preventDefault();
        let input = $(this).siblings('.quant_input');
        let currentValue = parseInt(input.val());
        if (currentValue > 1) {
            input.val(currentValue - 1);
            updateTotal(); // 총 수량 및 가격 업데이트
        } else {
            alert("최소 주문 수량은 1개 입니다.");
        }
    });

    function setProductState() {
        // 초기화
        $('.prd_price.price, .prd_price.discount, .prd_price.soldout').removeClass('active');
        $('.buy_btn .btn1').addClass('active');
        $('.buy_btn .soldout').removeClass('active');

        // 상태 설정
        if ($('.prd_price.discount').length) {
            $('.prd_price.discount').addClass('active');
        } else if ($('.prd_price.soldout').length) {
            $('.prd_price.soldout').addClass('active');
            $('.buy_btn .btn1').removeClass('active');
            $('.buy_btn .soldout').addClass('active');

        } else {
            $('.prd_price.price').addClass('active');
            $('#CartBtn, #BuyBtn').addClass('active');
        }

        updateTotal();
    }
    setProductState();

    function updateTotal() {
        let quantity = parseInt($('.quant_input').val());
        let isDiscount = $('.prd_price.discount').hasClass('active');
        let isSoldOut = $('.prd_price.soldout').hasClass('active');
        let currentPrice = isDiscount ? parseInt($('#salePrice').text().replace(/[^0-9]/g, '')) : parseInt($('#productPrice').text().replace(/[^0-9]/g, ''));
        let totalPrice = quantity * currentPrice;
        if (isSoldOut) {
            $('.prd_total').hide();
            $('.prd_quantity').hide();
            return;
        } else {
            $('.prd_total').show();
            $('.prd_quantity').show();
        }
        $('#totalQuantity').text(quantity + '개');
        $('#totalPrice').text(totalPrice.toLocaleString() + '원');
    }


    $('.tab_tit').click(function () {
        let tabWrap = $(this).siblings('.tab_wrap');
        $('.prd_info_tab .tab_wrap:visible').not(tabWrap).slideUp();
        tabWrap.slideToggle();

        let expandIcon = $(this).find('.expand_icon');
        $('.tab_tit .expand_icon').not(expandIcon).text('expand_more');
        expandIcon.text(function (_, text) {
            return text === 'expand_more' ? 'expand_less' : 'expand_more';
        });

        return false;
    });

    let csrfToken = $("meta[name='_csrf']").attr("content");
    let csrfHeader = $("meta[name='_csrf_header']").attr("content");

    $('.wish').click(function () {

        let heart = $(this);
        let productId = heart.attr('id');

        if (heart.hasClass('filled')) {
            $.ajax({
                url: '/api/wish/remove',
                type: 'POST',
                data: {productId: productId},
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(csrfHeader, csrfToken);
                },
                success: function () {
                    heart.removeClass('filled');
                },
                error: function (errorResult) {
                    alert(errorResult['message']);
                }
            })
        } else {
            $.ajax({
                type: 'POST',
                url: '/api/wish/add',
                data: {productId: productId},
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(csrfHeader, csrfToken);
                },
                success: function () {
                    heart.addClass('filled')
                },
                error: function (errorResult) {
                    alert(errorResult['message']);
                }
            })
        }
    });
});
