$(document).ready(function(){
    // 기본 배송지 탭 클릭 시
    $('.address_tabs li:first-child').addClass('active'); // 처음에 기본 배송지 탭을 활성화
    $('.input_area.default').addClass('active'); // 처음에 기본 배송지 입력 영역을 활성화

    $('.address_tabs li:first-child').click(function(){
        $(this).addClass('active').siblings().removeClass('active');
        $('.input_area.default').addClass('active').siblings('.input_area').removeClass('active');
    });

    // 신규 입력 탭 클릭 시
    $('.address_tabs li:last-child').click(function(){
        $(this).addClass('active').siblings().removeClass('active');
        $('.input_area:last-child').addClass('active').siblings('.input_area').removeClass('active');
    });

    // 직접 입력 옵션 선택 시 입력란 표시
    $(".shipping_message select").change(function() {
        let directInputContainer = $(this).closest('.shipping_message').find(".direct_input_wrap");
        if ($(this).val() === "direct_input") {
            directInputContainer.show();
        } else {
            directInputContainer.hide();
        }
    });
    function toggleIcon() {
        let icon = $(this).find(".material-symbols-outlined");
        if (icon.text() === "expand_more") {
            icon.text("expand_less");
        } else {
            icon.text("expand_more");
        }
    }
    $(".coupon_select").click(function() {
        $(this).find(".coupon_list").slideToggle();
        toggleIcon.call(this); // 아이콘 토글 함수 호출

    });

    $('.pay_btn').click(function() {
        $('.card_select').show();
    });

    $('.card_select').click(function() {
        $('.card_list').slideToggle();
        toggleIcon.call(this);

    });
    $('.card_list .card').click(function() {
        var selectedCard = $(this).find('.card_name').text();
        $('.card_value span').text(selectedCard);
    });

    // 전체 동의 체크박스 기능 구현
    $(".order_agree.all input[type='checkbox']").change(function() {
        let isChecked = $(this).prop("checked");
        $(".order_agree input[type='checkbox']").prop("checked", isChecked);
    });

    // 결제하기 버튼 활성/비활성화 처리
    $(".order_agree input[type='checkbox']").change(function() {
        let checked = $(".order_agree input[type='checkbox']:checked").length;
        if (checked === 3) {
            $(".submit_btn").prop("disabled", false);
        } else {
            $(".submit_btn").prop("disabled", true);
        }
    });
});
function updateDiscount() {
    // 쿠폰이 선택되었을 때 총 할인 금액 업데이트
    let productCouponDiscount = 0;
    $(".cart_item").each(function() {
        let originalPrice = parseInt($(this).find(".original_price span").text().replace(/[^0-9]/g, ""));
        let discountPrice = parseInt($(this).find(".discount_price").text().replace(/[^0-9]/g, ""));
        let itemDiscount = originalPrice - discountPrice;
        productCouponDiscount += itemDiscount;
    });

    return productCouponDiscount;
}
function handleCouponSelect() {
    $(".coupon_list .coupon").click(function() {
        if ($(this).hasClass("none")) {
            // "선택 안함" 쿠폰을 선택한 경우
            $(".coupon_select .coupon_value span:first-child").text("선택 안함");
            $(".coupon_select .coupon_value span:last-child").text("");
            updateOrderSummary(updateDiscount(), 0); // 할인 없음을 전달하여 업데이트
        } else {
            let couponValue = parseInt($(this).find(".coupon_name").text().match(/\d+/)[0]); // 쿠폰 이름에서 숫자 추출
            let discount = couponValue/100 * parseInt($(".total.price .content").text().replace(/[^0-9]/g, "")); // 할인 가격 계산
            let couponName = $(this).find(".coupon_name").text();
            $(".coupon_select .coupon_value span:first-child").text(couponName);
            $(".coupon_select .coupon_value span:last-child").text("-" + discount.toLocaleString() + "원");

            // 회원 쿠폰 할인은 회원 쿠폰 하나만 선택 가능하다고 가정합니다.
            let memberCouponDiscount = discount;

            // 주문서 업데이트 함수 호출
            updateOrderSummary(updateDiscount(), memberCouponDiscount);
        }
    });
}
handleCouponSelect();
function handleMileage() {
    let availableMileageText = $("#ownedMileage").text().replace(",", "");
    let availableMileage = parseInt(availableMileageText);
    $("#availableMileage").text(availableMileage.toLocaleString());

    $("#useAll").click(function() {
        if ($(this).text() === "모두 사용") {
            // 입력란에 사용 가능한 마일리지 값 설정
            $("#mileage").val(availableMileage.toLocaleString());
            // "사용 안함" 버튼 텍스트 변경
            $(this).text("사용 안함");
        } else {
            // 입력란에 0으로 설정
            $("#mileage").val("0");
            // "모두 사용" 버튼 텍스트 변경
            $(this).text("모두 사용");
        }
        // 보유 마일리지 값 변경
        $("#ownedMileage").text(availableMileage.toLocaleString());
        // 마일리지가 변경되었을 때는 할인 내역과 회원 쿠폰 내역을 유지한 채로 주문 요약 업데이트
        updateOrderSummary(updateDiscount(), parseInt($(".total.discount .member.coupon .content").text().replace(/[^0-9]/g, "")));
    });

    $("#mileage").on("input", function() {
        let mileage = $(this).val().replace(/[^0-9]/g, "");
        $(this).val(mileage.replace(/\B(?=(\d{3})+(?!\d))/g, ""));

        let mileageInput = parseInt($(this).val().replace(/[^0-9]/g, "") || 0); // 입력된 값이 없을 때는 0으로 설정
        let ownedMileage = availableMileage - mileageInput;
        ownedMileage = Math.max(ownedMileage, 0); // 음수인 경우 0으로 설정

        if (mileageInput > availableMileage) {
            $(this).val(availableMileage.toLocaleString());
            alert("사용 가능한 마일리지를 초과했습니다.");
        }

        $("#ownedMileage").text(ownedMileage.toLocaleString());
        // 마일리지가 변경되었을 때는 할인 내역과 회원 쿠폰 내역을 유지한 채로 주문 요약 업데이트
        updateOrderSummary(updateDiscount(), parseInt($(".total.discount .member.coupon .content").text().replace(/[^0-9]/g, "")));
    });
}
handleMileage();
function updateOrderSummary(productCouponDiscount, memberCouponDiscount) {
    // 할인 내역을 업데이트할 때는 할인 쿠폰 정보가 넘어오면 해당 정보를 사용하고,
    // 넘어오지 않으면 기존의 할인 내역을 유지합니다.
    productCouponDiscount = productCouponDiscount || 0;
    memberCouponDiscount = memberCouponDiscount !== null && memberCouponDiscount !== undefined ? memberCouponDiscount : 0;

    // 총 상품 금액 계산
    let totalProductPrice = 0;
    $(".cart_item").each(function() {
        let productPrice = parseInt($(this).find(".original_price span").text().replace(/[^0-9]/g, ""));
        totalProductPrice += productPrice;
    });

    // 적립금 계산
    let usedMileage = parseInt($("#mileage").val().replace(/[^0-9]/g, "") || 0);

    // 할인 적용 후 총 상품 금액 계산
    let discountedTotalProductPrice = totalProductPrice - productCouponDiscount;

    // 총 결제 금액 계산
    let shippingFee = parseInt($(".total.shipping .content").text().replace(/[^0-9]/g, ""));
    let totalPrice = discountedTotalProductPrice + shippingFee - usedMileage - memberCouponDiscount;
    let totalDiscount = productCouponDiscount + memberCouponDiscount;

    // 주문서 업데이트
    $(".total.price .content").text(totalProductPrice.toLocaleString() + "원");
    $(".total.discount .product.coupon .content").text("-" + productCouponDiscount.toLocaleString() + "원");
    $(".total.discount .member.coupon .content").text("-" + memberCouponDiscount.toLocaleString() + "원");
    $(".total.discount > ul >  .content").text("-" + totalDiscount.toLocaleString() + "원");
    $(".total.mileage .content").text("-" + usedMileage.toLocaleString() + "원");
    $(".total_price .content").text(totalPrice.toLocaleString() + "원");
}
updateOrderSummary(updateDiscount());
