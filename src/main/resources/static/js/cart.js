$(document).ready(function() {
    // 전체 선택 체크박스 변경 시
    $('#checkAll').change(function() {
        var isChecked = $(this).prop('checked');
        $('.cart_checkbox').prop('checked', isChecked);
        updateTotalPrice();
    });

    // 상품 체크박스 변경 시
    $('.cart_checkbox').change(function() {
        updateTotalPrice();
    });

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

    // 가격 업데이트 함수
    function updateTotalPrice() {
        var totalPrice = 0;
        var totalItemsPrice = 0;

        // 선택된 상품의 가격 합계 계산
        $('.cart_item').each(function() {
            if ($(this).find('.cart_checkbox').prop('checked')) {
                var quantity = parseInt($(this).find('.quant_input').val());
                var price = parseInt($(this).find('.item.price').text().replace('원', '').replace(',', ''));
                var itemTotalPrice = quantity * price;
                totalPrice += itemTotalPrice;
                totalItemsPrice += itemTotalPrice;
                $(this).find('.item_total_price').text(addCommas(itemTotalPrice) + '원');
            }
        });

        // 상품 합계 업데이트
        $('.total.price .content').text(addCommas(totalItemsPrice) + '원');

        // 배송비 합산
        var shippingFee = parseInt($('.total.shipping .content').text().replace('원', '').replace(',', ''));
        totalPrice += shippingFee;

        // 합계 업데이트
        $('.total_price .content').text(addCommas(totalPrice) + '원');
    }

    // 콤마 추가 함수
    function addCommas(num) {
        return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    }
});
