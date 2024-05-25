$(document).ready(function(){
    initPopup();
    updateCoupon();

    $(".submit_btn").addClass("disabled");

    // 기본 배송지 탭 클릭 시
    $('.delivery_tabs li:first-child').addClass('active'); // 처음에 기본 배송지 탭을 활성화
    $('.input_area.default').addClass('active'); // 처음에 기본 배송지 입력 영역을 활성화

    $('.delivery_tabs li:first-child').click(function(){
        $(this).addClass('active').siblings().removeClass('active');
        $('.input_area.default').addClass('active').siblings('.input_area').removeClass('active');
    });

    // 신규 입력 탭 클릭 시
    $('.delivery_tabs li:last-child').click(function(){
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
       $(".shipping_message select").change(function() {
        let directInputContainer = $(this).closest('.shipping_message').find(".direct_input_wrap");
        if ($(this).val() === "direct_input") {
            directInputContainer.show();
        } else {
            directInputContainer.hide();
        }
    });

    $(".coupon_select").click(function() {
        $(this).find(".coupon_list").slideToggle();
        toggleIcon.call(this); // 아이콘 토글 함수 호출

    });

    $('.pay_btn.card').click(function() {
        $('.card_select').show();
        $('.pay_btn.card').addClass("selected");
        $('.easy_select').hide();
        $('.pay_btn.easy').removeClass("selected");
    });
    $('.card_select').click(function() {
        $('.card_list').toggle();
        toggleIcon.call(this);
    });
    $('.pay_btn.easy').click(function() {
        $('.easy_select').show();
        $('.pay_btn.easy').addClass("selected");
        $('.card_select').hide();
        $('.pay_btn.card').removeClass("selected");
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

    $('#deliveryList').on('click', 'li .delivery_item', function() {
        let deliveryName = $(this).find('.delivery_name .popup_delivery_name').text().trim();
        let recipient = $(this).find('.recipient_info .popup_recipient_name').text().trim();
        let zipCode = $(this).find('.delivery_details .popup_zip_code').text().trim().replace(/[()]/g, '');
        let basicAddress = $(this).find('.delivery_details .popup_basic_address').text().trim();
        let detailAddress = $(this).find('.delivery_details .popup_detail_address').text().trim();
        let phoneNumber = $(this).find('.recipient_info .popup_phonenumber').text().trim();

        // 부모 창의 주소 입력란 업데이트
        window.opener.$('#defaultDelivery .input_wrap #defaultDeliveryName').text(deliveryName);
        window.opener.$('#defaultDelivery .input_wrap #defaultRecipientName').text(recipient);
        window.opener.$('#defaultDelivery .input_wrap #defaultZipCode').text(zipCode);
        window.opener.$('#defaultDelivery .input_wrap #defaultBasicAddress').text(basicAddress);
        window.opener.$('#defaultDelivery .input_wrap #defaultDetailAddress').text(detailAddress);
        window.opener.$('#defaultDelivery .input_wrap #defaultPhonenumber1').text(phoneNumber.split('-')[0]);
        window.opener.$('#defaultDelivery .input_wrap #defaultPhonenumber2').text(phoneNumber.split('-')[1]);
        window.opener.$('#defaultDelivery .input_wrap #defaultPhonenumber3').text(phoneNumber.split('-')[2]);

        // 팝업 창 닫기
        window.close();
    });
});
function toggleIcon() {
    let icon = $(this).find(".material-symbols-outlined");
    if (icon.text() === "expand_more") {
        icon.text("expand_less");
    } else {
        icon.text("expand_more");
    }
}
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
    let width = 450;
    let height = 500;
    let left = (window.screen.width / 2) - (width / 2);
    let top = (window.screen.height / 2) - (height / 2);
    let popupUrl = 'checkout_delivery_popup.html'; // 팝업으로 열 페이지의 URL
    let popupName = '주소 입력'; // 팝업 창의 이름
    let popupOptions =  `width=${width},height=${height},top=${top},left=${left}`; // 팝업 창의 옵션

    let popupBtn = $('.delivery_popup_btn');

    popupBtn.on('click', function (event) {
        event.preventDefault();
        window.open(popupUrl, popupName, popupOptions);
    });
}

// function loadAddresses() {
//     $.ajax({
//         type: 'GET',
//         url: '',
//         data:
//         success: function() {
//             updateAddressList();
//         },
//
//     });
// }
function isOrderAgree() {
    if (!$('#agreeAll').prop('checked')) {
        alert("주문 내용을 확인하고 모두 동의해주세요.");
        return false;
    }
    return true;
}

function isDeliveryNameEmpty() {
    let deliveryName = $("#deliveryName").val();

    if (deliveryName.trim() === '') {
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

function isDeliveryEmpty() {

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
function validateNewDelivery() {
    // 배송지명 검사
    if (!isDeliveryNameEmpty()) {
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
    if (!isDeliveryEmpty()) {
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
function validateEditPopup() {
    // 필드 값 가져오기
    let detailAddress = $('#detailAddress').val().trim();
    let recipientName = $('#recipientName').val().trim();
    let phoneNumber2 = $('#phonenumber2').val().trim();
    let phoneNumber3 = $('#phonenumber3').val().trim();

    if (recipientName === '') {
        alert('수령인을 입력해 주세요.');
        $('#recipientName').focus();
        return false;
    }

    if (phoneNumber2 === '' || phoneNumber3 === '') {
        alert('휴대전화번호를 입력해 주세요.');
        if (phoneNumber2 === '') {
            $('#phonenumber2').focus();
        } else {
            $('#phonenumber3').focus();
        }
        return false;
    }

    if (!/^\d{3,4}$/.test(phoneNumber2) || !/^\d{4}$/.test(phoneNumber3)) {
        alert('유효한 휴대전화번호를 입력해 주세요.');
        if (!/^\d{3,4}$/.test(phoneNumber2)) {
            $('#phonenumber2').focus();
        } else {
            $('#phonenumber3').focus();
        }
        return false;
    }

    return true;
}
// 전체 유효성 검사 함수
function validateBeforeSubmit() {
    if (!isOrderAgree()) {
        return false;
    }

    if ($('#newDelivery').hasClass('active')) {
        if (!validateNewDelivery()) {
            return false;
        }
    }

    return true;
}

function updateDiscount() {
    let productCouponDiscount = 0;
    $(".cart_item").each(function() {
        let originalPrice = parseInt($(this).find("#productPrice").text().replace(/[^0-9]/g, ""));
        let salePrice = $(this).find("#productSalePrice").length > 0 ? parseInt($(this).find("#productSalePrice").text().replace(/[^0-9]/g, "")) : originalPrice;
        let itemDiscount = originalPrice - salePrice;
        productCouponDiscount += itemDiscount;
    });

    return productCouponDiscount;
}
function updateCoupon() {
    $('.coupon').each(function(index) {
        if (index > 0) { // 첫 번째 쿠폰(선택 안함)은 건너뛰기
            let couponName = $(this).find(".coupon_name").text().trim();
            let couponValueMatch = couponName.match(/\d+/);
            if (couponValueMatch) {
                let couponValue = parseInt(couponValueMatch[0]); // 쿠폰 이름에서 숫자 추출
                let totalProductPrice = 0;
                $(".cart_item").each(function() {
                    let productPrice = parseInt($(this).find("#productPrice").text().replace(/[^0-9]/g, ""));
                    totalProductPrice += productPrice;
                });
                let productCouponDiscount = updateDiscount();
                let discountedTotalProductPrice = totalProductPrice - productCouponDiscount;
                let discount = discountedTotalProductPrice * couponValue / 100; // 할인 가격 계산
                $(this).find(".coupon_details .discount_price").text("-" + discount.toLocaleString() + "원");
            }
        }
    });
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
            let totalProductPrice = 0;
            $(".cart_item").each(function() {
                let productPrice = parseInt($(this).find("#productPrice").text().replace(/[^0-9]/g, ""));
                totalProductPrice += productPrice;
            });
            let productCouponDiscount = updateDiscount();
            let discountedTotalProductPrice = totalProductPrice - productCouponDiscount;
            let discount = discountedTotalProductPrice * couponValue / 100; // 할인 가격 계산
            let couponName = $(this).find(".coupon_name").text();
            $(".coupon_select .coupon_value span:first-child").text(couponName);
            $(".coupon_select .coupon_value span:last-child").text("-" + discount.toLocaleString() + "원");
            // $(this).find(".coupon_details .discount_price").text("-" + discount.toLocaleString() + "원");

            // 회원 쿠폰 할인은 회원 쿠폰 하나만 선택 가능하다고 가정합니다.
            let memberCouponDiscount = discount;

            // 주문서 업데이트 함수 호출
            updateOrderSummary(productCouponDiscount, memberCouponDiscount);
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
            $("#ownedMileage").text("0");
            // "사용 안함" 버튼 텍스트 변경
            $(this).text("사용 안함");
        } else {
            // 입력란에 0으로 설정
            $("#mileage").val("0");
            // 보유 마일리지 값 원래대로 설정
            $("#ownedMileage").text(availableMileage.toLocaleString());
            // "모두 사용" 버튼 텍스트 변경
            $(this).text("모두 사용");
        }
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
        let productPrice = parseInt($(this).find("#productPrice").text().replace(/[^0-9]/g, ""));
        let quantity = parseInt($(this).find(".item.quantity span").text());
        totalProductPrice += productPrice * quantity;
    });

    let usedMileage = parseInt($("#mileage").val().replace(/[^0-9]/g, "") || 0);

    let discountedTotalProductPrice = totalProductPrice - productCouponDiscount;
    let totalDiscount = productCouponDiscount + memberCouponDiscount;
    let shippingFee = (totalProductPrice - totalDiscount - usedMileage >= 50000) ? 0 : 3000;
    let totalPrice = discountedTotalProductPrice + shippingFee - usedMileage - memberCouponDiscount;

    $(".total.price .content").text(totalProductPrice.toLocaleString() + "원");
    $(".total.discount .product.coupon .content").text(productCouponDiscount > 0 ? "-" + productCouponDiscount.toLocaleString() + "원" : "0원");
    $(".total.discount .member.coupon .content").text(memberCouponDiscount > 0 ? "-" + memberCouponDiscount.toLocaleString() + "원" : "0원");
    $(".total.discount > ul >  .content").text(totalDiscount > 0 ? "-" + totalDiscount.toLocaleString() + "원" : "0원");
    $(".total.mileage .content").text(usedMileage > 0 ? "-" + usedMileage.toLocaleString() + "원" : "0원");
    $(".total_price .content").text(totalPrice.toLocaleString() + "원");
    $(".total.shipping .content").text(shippingFee.toLocaleString() + "원");
}
updateOrderSummary(updateDiscount());
