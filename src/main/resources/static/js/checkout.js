$(document).ready(function(){
    initPopup();

    $(".submit_btn").addClass("disabled");

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
    $('#detailAddress').focus(function () {
        let zipCode = $('#zipCode').val().trim();
        let basicAddress = $('#basicAddress').val().trim();

        if (zipCode === '' || basicAddress === '') {
            alert("주소 검색을 통해 우편번호와 기본주소를 먼저 입력해 주세요.");
            $('#searchZipCodeBtn').focus();
        }
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
        let selectedCard = $(this).find('.card_name').text();
        $('.card_value span').text(selectedCard);
    });

    // 전체 동의 체크박스 기능 구현
    $("#agreeAll").click(function() {
        let isChecked = $(this).prop("checked");
        $(".order_agree input[type='checkbox']").prop("checked", isChecked);
        updateSubmitButtonState();
    });

    // 개별 동의 체크박스 변경 시
    $(".order_agree input[type='checkbox']").click(function() {
        let allChecked = $(".order_agree:not(.all) input[type='checkbox']:checked").length === $(".order_agree:not(.all) input[type='checkbox']").length;
        $(".order_agree.all input[type='checkbox']").prop("checked", allChecked);
        updateSubmitButtonState();
    });


});
function updateSubmitButtonState() {
        let checked = $(".order_agree input[type='checkbox']:checked").length;
        if (checked === 4) {
            $("#agreeAll").prop("checked", true);
        } else {
            $("#agreeAll").prop("checked", false);
        }
        if (checked === 4) {
            $(".submit_btn").prop("disabled", false);
            $('.submit_btn').removeClass('disabled');
        } else {
            $(".submit_btn").prop("disabled", true);
            $('.submit_btn').addClass('disabled');
        }
    }
function initPopup() {
    let popup = $('#addressPopup');
    let popupBtn = $('.address_popup_btn');
    let closeBtn = $('.close_btn');

    popupBtn.on('click', function (event) {
        event.preventDefault();
        popup.css('display', 'block');
    });

    closeBtn.on('click', function () {
        popup.css('display', 'none');
    });

    $(window).on('click', function (event) {
        if ($(event.target).is(popup)) {
            popup.css('display', 'none');
        }
    });
}

function isOrderAgree() {
    if (!$('#agreeAll').prop('checked')) {
        alert("주문 내용을 확인하고 모두 동의해주세요.");
        return false;
    }
    return true;
}

function isAddressNameEmpty() {
    let addressName = $("#addressName").val();

    if (addressName.trim() === '') {
        return false;
    }
    return true;
}

//이름 검증
function isNameValid() {
    if (!isNameEmpty()) {
        return;
    } else if (!regexName()) {
        return;
    }
    $('#nameMsg').text('');
    return;
}

function isNameEmpty() {
    let name = $("#recipientName").val();

    if (name.trim() === '') {
        return false;
    }

    return true;
}

function regexName() {
    let name = $('#recipientName').val();
    let regex = /^[가-힣a-zA-Z]{2,12}$/;
    if (!regex.test(name)) {
        return false;
    }

    return true;
}

function isAddressEmpty() {

    // 우편번호 검사
    let zipCode = $('#zipCode').val().trim();
    if (zipCode === '') {
        return false;
    }

    // 기본주소 검사
    let basicAddress = $('#basicAddress').val().trim();
    if (basicAddress === '') {
        return false;
    }

    return true;
}


//휴대전화번호 검증
function isPhoneValid() {
    if (!isPhoneEmpty()) {
        return false;
    } else if (!regexPhone()) {
        return false;
    }
    return true;
}

function isPhoneEmpty() {
    let phonenumber2 = $('#phonenumber2').val();
    let phonenumber3 = $('#phonenumber3').val();

    if (phonenumber2.trim() === '' && phonenumber3.trim() === '') {
        return false;
    }

    return true;
}

function regexPhone() {
    let phonenumber2 = $('#phonenumber2').val();
    let phonenumber3 = $('#phonenumber3').val();
    let regex1 = /^[0-9]{3,4}$/;
    let regex2 = /^[0-9]{4}$/;
    if (regex1.test(phonenumber2) && regex2.test(phonenumber3)) {
        return true;
    } else {
        return false;
    }
}

// 주문서 신규 배송지 유효성 검사
function validateNewAddress() {
    // 배송지명 검사
    if (!isAddressNameEmpty()) {
        alert("배송지명을 입력해 주세요.");
        return false;
    }

    // 수령인 검사
    if (!isNameEmpty()) {
        alert("수령인을 입력해 주세요.");
        return false;
    } else if (!regexName()) {
        alert("유효한 수령인을 입력해 주세요.");
        return false;
    }

    // 주소 검사
    if (!isAddressEmpty()) {
        alert("주소를 입력해 주세요.");
        return false;
    }

    // 휴대전화번호 검사
    if (!isPhoneEmpty()) {
        alert("휴대전화번호를 입력해 주세요.");
        return false;
    } else if (!regexPhone()) {
        alert("유효한 휴대전화번호를 입력해 주세요.");
        return false;
    }

    return true;
}

// 전체 유효성 검사 함수
function validateBeforeSubmit() {
    if (!isOrderAgree()) {
        return false;
    }

    if ($('#newAddress').hasClass('active')) {
        if (!validateNewAddress()) {
            return false;
        }
    }

    return true;
}

function updateDiscount() {
    let productCouponDiscount = 0;
    $(".cart_item").each(function() {
        let originalPrice = parseInt($(this).find(".original_price span").first().text().replace(/[^0-9]/g, ""));
        let salePrice = parseInt($(this).find(".sale_price").text().replace(/[^0-9]/g, ""));
        let itemDiscount = originalPrice - salePrice;
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
    productCouponDiscount = productCouponDiscount || 0;
    memberCouponDiscount = memberCouponDiscount !== null && memberCouponDiscount !== undefined ? memberCouponDiscount : 0;

    let totalProductPrice = 0;
    $(".cart_item").each(function() {
        let productPrice = parseInt($(this).find(".original_price span").first().text().replace(/[^0-9]/g, ""));
        totalProductPrice += productPrice;
        console.log(totalProductPrice);
    });

    let usedMileage = parseInt($("#mileage").val().replace(/[^0-9]/g, "") || 0);

    let discountedTotalProductPrice = totalProductPrice - productCouponDiscount;

    let shippingFee = parseInt($(".total.shipping .content").text().replace(/[^0-9]/g, ""));
    let totalPrice = discountedTotalProductPrice + shippingFee - usedMileage - memberCouponDiscount;
    let totalDiscount = productCouponDiscount + memberCouponDiscount;

    $(".total.price .content").text(totalProductPrice.toLocaleString() + "원");
    $(".total.discount .product.coupon .content").text("-" + productCouponDiscount.toLocaleString() + "원");
    $(".total.discount .member.coupon .content").text("-" + memberCouponDiscount.toLocaleString() + "원");
    $(".total.discount > ul >  .content").text("-" + totalDiscount.toLocaleString() + "원");
    $(".total.mileage .content").text("-" + usedMileage.toLocaleString() + "원");
    $(".total_price .content").text(totalPrice.toLocaleString() + "원");
}
updateOrderSummary(updateDiscount());
