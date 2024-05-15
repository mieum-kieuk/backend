$(document).ready(function () {
    // 전체 선택 체크박스 변경 시
    $('#checkAll').change(function () {
        var isChecked = $(this).prop('checked');
        $('.cart_checkbox').prop('checked', isChecked);
        updateTotalPrice();
    });
// 체크박스 변경 시
    $('.cart_checkbox').change(function () {
        updateTotalPrice();
        updatePurchaseButton();
    });

    $('.quant_input').each(function() {
        let currentValue = parseInt($(this).val());
        let decreaseBtn = $(this).siblings('.decrease');
        if (currentValue === 1) {
            decreaseBtn.addClass('disabled');
        }
    });

// 구매하기 버튼 상태 업데이트 함수
    function updatePurchaseButton() {
        var isChecked = false;

        $('.cart_checkbox').each(function () {
            if ($(this).prop('checked')) {
                isChecked = true;
                return false; // 반복문 종료
            }
        });

        if (isChecked) {
            $('.submit_btn').prop('disabled', false);
            $('.submit_btn').removeClass('disabled'); // 버튼 활성화 시 disabled 클래스 제거
        } else {
            $('.submit_btn').prop('disabled', true);
            $('.submit_btn').addClass('disabled');
        }
    }


    $('.increase').click(function(event) {
        event.preventDefault();
        let input = $(this).siblings('.quant_input');
        let currentValue = parseInt(input.val());
        let cartItem = $(this).closest('.cart_item');
        let decreaseBtn = $(this).siblings('.decrease');

        if (!cartItem.hasClass('soldout')) {
            input.val(currentValue + 1);
            if (currentValue === 1) {
                decreaseBtn.removeClass('disabled');
            }
            updateTotalPrice();
        }
        else {
            $(this).addClass('disabled');
        }
    });

    $('.decrease').click(function(event) {
        event.preventDefault();
        let input = $(this).siblings('.quant_input');
        let currentValue = parseInt(input.val());
        let cartItem = $(this).closest('.cart_item');
        let increaseBtn = $(this).siblings('.increase');

        if (!cartItem.hasClass('soldout') && currentValue > 1) {
            input.val(currentValue - 1);
            if (currentValue === 2) {
                $(this).addClass('disabled');
            }
            updateTotalPrice();
        }else {
            $(this).addClass('disabled');
        }
    });



    // 수량 변경 시
    $('.quant_input').change(function() {
        updateTotalPrice();
    });

    // 가격 업데이트 함수
    function updateTotalPrice() {
        var totalPrice = 0;
        var cartProductPrice = 0;
        var cartDiscountPrice = 0;

        // 선택된 상품의 가격 합계 및 할인 금액 계산
        $('.cart_item').each(function() {
            var isChecked = $(this).find('.cart_checkbox').prop('checked');
            if (isChecked) {
                var quantity = parseInt($(this).find('.quant_input').val());
                var originalPrice = parseFloat($(this).find('.item.info .original_price #productPrice').text().replace('원', '').replace(',', ''));

                var discountPriceElement = $(this).find('.item.info .sale_price #productDiscountPrice');
                var discountPrice = 0;
                if (discountPriceElement.length > 0) {
                    // 할인가가 있는 경우
                    discountPrice = parseFloat(discountPriceElement.text().replace('원', '').replace(',', ''));
                    $(this).find('.item.price #productSalePrice').text(addCommas(discountPrice * quantity) + '원');
                } else {
                    // 할인가가 없는 경우
                    discountPrice = originalPrice;
                    var salePrice = originalPrice;
                    $(this).find('.item.price #productSalePrice').text(addCommas(salePrice * quantity) + '원');
                }

                var itemTotalPrice = quantity * discountPrice; // 할인된 가격으로 계산
                var itemOriginalPrice = quantity * originalPrice;
                var itemDiscountPrice = itemOriginalPrice - itemTotalPrice;

                totalPrice += itemTotalPrice;
                cartProductPrice += itemOriginalPrice; // 할인 적용 전 가격 합계
                cartDiscountPrice += itemDiscountPrice;
            }
        });

        // 상품 합계 업데이트
        $('.total.price .content').text(addCommas(cartProductPrice) + '원');

        // 상품 할인 금액 업데이트
        $('.total.discount .content').text('-' + addCommas(cartDiscountPrice) + '원');

        // 결제 예정 금액 계산 및 업데이트
        var paymentAmount = cartProductPrice - cartDiscountPrice;
        var shippingFee = parseInt($('.total.shipping .content').text().replace('원', '').replace(',', ''));
        paymentAmount += shippingFee;
        $('.total_price .content').text(addCommas(paymentAmount) + '원');
    }

    // 콤마 추가 함수
    function addCommas(num) {
        return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    }
});


let csrfToken = $("meta[name='_csrf']").attr("content");
let csrfHeader = $("meta[name='_csrf_header']").attr("content");

function deleteProduct(productId) {

    if (!confirm('상품을 삭제하시겠습니까?')) {
        return false;
    } else {
        $.ajax({
            type: 'POST',
            url: '/api/cart/' + productId + '/delete',
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken);
            },
            success: function () {
                window.location.href = '/cart';
            },
            error: function () {
                alert('삭제하는 동안 문제가 발생하였습니다.\n다시 시도해주세요.');
            }
        })
    }
}

function deleteProducts() {

    let productIds = [];
    let checkboxes = $('.cart_content input[type=checkbox]:checked');

    if (checkboxes.length == 0) {
        alert('삭제할 상품을 선택해 주세요.');
        return false;
    } else {
        if (!confirm(checkboxes.length + '개의 상품을 삭제하시겠습니까?')) {
            return false;
        } else {
            checkboxes.each(function (v) {
                let productId = checkboxes[v].id.split('checkbox')[1];
                productIds.push(productId);
            })

            $.ajax({
                type: 'POST',
                url: '/api/cart/delete',
                contentType: 'application/json',
                data: JSON.stringify(productIds),
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(csrfHeader, csrfToken);
                },
                success: function () {
                    window.location.href = '/cart';
                },
                error: function () {
                    alert('삭제하는 동안 문제가 발생하였습니다.\n다시 시도해주세요.');
                }
            })
        }
    }
}

function deleteSoldOutProducts() {

    let productIds = [];
    let checkboxes = $('.cart_checkbox.sold_out');

    if (checkboxes.length == 0) {
        alert('품절 상품이 없습니다.');
        return false;
    } else {
        if (!confirm('품절 상품을 삭제하시겠습니까?')) {
            return false;
        } else {
            checkboxes.each(function (v) {
                let productId = checkboxes[v].id.split('checkbox')[1];
                productIds.push(productId);
            })

            $.ajax({
                type: 'POST',
                url: '/api/cart/delete',
                contentType: 'application/json',
                data: JSON.stringify(productIds),
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(csrfHeader, csrfToken);
                },
                success: function () {
                    window.location.href = '/cart';
                },
                error: function () {
                    alert('삭제하는 동안 문제가 발생하였습니다.\n다시 시도해주세요.');
                }
            })
        }
    }
}
