$(document).ready(function() {
    // 전체 선택 체크박스 변경 시
    $('#checkAll').change(function() {
        var isChecked = $(this).prop('checked');
        $('.cart_checkbox').prop('checked', isChecked);
        updateTotalPrice();
    });
// 체크박스 변경 시
    $('.cart_checkbox').change(function() {
        updateTotalPrice();
        updatePurchaseButton();
    });

// 구매하기 버튼 상태 업데이트 함수
    function updatePurchaseButton() {
        var isChecked = false;

        $('.cart_checkbox').each(function() {
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
        input.val(currentValue + 1);
        updateTotalPrice();
    });

    $('.decrease').click(function(event) {
        event.preventDefault();
        let input = $(this).siblings('.quant_input');
        let currentValue = parseInt(input.val());
        if (currentValue > 1) {
            input.val(currentValue - 1);
            updateTotalPrice();
        } else {
            alert("최소 주문 수량은 1개 입니다.");
        }
    });

    // 수량 변경 시
    $('.quant_input').change(function() {
        updateTotalPrice();
    });

    // 삭제 버튼 클릭 시
    $('.remove_btn').click(function() {
        $(this).closest('.cart_item').remove();
        updateTotalPrice();
    });
    $("#deleteAllBtn").click(function() {
        if (confirm("정말 삭제하시겠습니까?")) {

            $(".cart_item").remove();
            alert("전체 삭제되었습니다.");
        }
    });

    $("#deleteBtn").click(function() {
        var checkedItems = $("input[name='cartCheckBox']:checked");
        if (checkedItems.length === 0) {
            alert("상품을 선택해 주세요.");
        } else {
            if (confirm("정말 삭제하시겠습니까?")) {

                checkedItems.closest('.cart_item').remove();
                alert("선택된 상품이 삭제되었습니다.");
            }
        }
    });
    // 가격 업데이트 함수
    function updateTotalPrice() {
        var totalPrice = 0;
        var totalItemsPrice = 0;
        var totalDiscountPrice = 0;

        // 선택된 상품의 가격 합계 및 할인 금액 계산
        $('.cart_item').each(function() {
            if ($(this).find('.cart_checkbox').prop('checked')) {
                var quantity = parseInt($(this).find('.quant_input').val());
                var originalPrice = parseFloat($(this).find('.item.info .original_price #productPrice').text().replace('원', '').replace(',', ''));
                var discountPrice = parseFloat($(this).find('.item.info .sale_price .discount_price').text().replace('원', '').replace(',', ''));
                var salePrice = originalPrice - discountPrice;
                var itemTotalPrice = quantity * discountPrice;
                var itemDiscountPrice = quantity * salePrice;

                totalPrice += itemTotalPrice;
                totalItemsPrice += quantity * originalPrice; // 할인 적용 전 가격 합계
                totalDiscountPrice += itemDiscountPrice;

                $(this).find('.item.price .sale_price').text(addCommas(itemTotalPrice) + '원');
            }
        });

        // 상품 합계 업데이트
        $('.total.price .content').text(addCommas(totalItemsPrice) + '원');

        // 상품 할인 금액 업데이트
        $('.total.discount .content').text('-' + addCommas(totalDiscountPrice) + '원');

        // 결제 예정 금액 계산 및 업데이트
        var paymentAmount = totalItemsPrice - totalDiscountPrice;
        var shippingFee = parseInt($('.total.shipping .content').text().replace('원', '').replace(',', ''));
        paymentAmount += shippingFee;
        $('.total_price .content').text(addCommas(paymentAmount) + '원');
    }

    // 콤마 추가 함수
    function addCommas(num) {
        return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    }
});
