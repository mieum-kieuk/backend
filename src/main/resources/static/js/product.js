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

    function updateTotal() {
        let totalQuantity = 0;
        let totalPrice = 0;
        $('.prd_quantity').each(function () {
            let quantity = parseInt($(this).find('.quant_input').val());
            let price = parseFloat($(this).siblings('.prd_info').find('#price').text().replace('KRW', '').replace(',', '').trim());
            let subtotalPrice = quantity * price;
            totalPrice += subtotalPrice;
            totalQuantity += quantity;
        });
        $('#totalQuantity').text(totalQuantity + '개');
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
                url: '/wish/remove',
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
                url: '/wish/add',
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
