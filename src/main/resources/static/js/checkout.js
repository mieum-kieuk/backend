$(document).ready(function(){
    initPopup();
    $('#checkoutBtn').click(function(event) {
        event.preventDefault();
        if (isOrderAgree()) {
            Swal.fire({
                text: '주문이 성공적으로 제출되었습니다.',
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
        }
    });
    $("#checkoutBtn").addClass("disabled");
    $('#deliveryList').on('click', 'li .delivery_item', function() {
        let deliveryData = {
            recipient: $(this).find('.recipient_info .popup_recipient_name').text().trim(),
            detailAddress: $(this).find('.delivery_details .popup_detail_address').text().trim(),
            phoneNumber: $(this).find('.recipient_info .popup_phonenumber').text().trim(),
            zipCode: $(this).find('.delivery_details .popup_zip_code').text().trim(),
            basicAddress: $(this).find('.delivery_details .popup_basic_address').text().trim()
        };

        // 배송 정보 업데이트
        updateDelivery(deliveryData);
    });

    // 기본 배송지 탭 클릭 시
    $('.delivery_tabs li:first-child').addClass('active'); // 처음에 기본 배송지 탭을 활성화
    $('.input_area.default').addClass('active'); // 처음에 기본 배송지 입력 영역을 활성화

    $('.delivery_tabs li:first-child').click(function(){
        $(this).addClass('active').siblings().removeClass('active');
        $('.input_area.default').addClass('active').siblings('.input_area').removeClass('active');
        resetNewDelivery();
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
            Swal.fire({
                html: '주소 검색을 통해 우편번호와 기본주소를<br>먼저 입력해 주세요.',
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
            $('#searchZipCodeBtn').focus();
        }
    });
    $(".shipping_message select").change(function() {
        let directInputContainer = $(this).closest('.shipping_message').find(".direct_input_wrap");
        let directInputField = $(this).closest('.shipping_message').find(".direct_input_wrap textarea");
        if ($(this).val() === "direct_input") {
            directInputContainer.show();
        } else {
            directInputContainer.hide();
            directInputField.val('');
        }
    });

    $('.pay_btn.card').click(function() {
        // 모든 선택 상태 초기화
        resetSelections();

        // 카드 결제 선택 시
        $('.pay_btn.card').addClass("selected");
        $('.easy_select').hide();
        $('.pay_btn.easy').removeClass("selected");
    });

    $('.pay_btn.easy').click(function() {
        // 모든 선택 상태 초기화
        resetSelections();

        // 간편 결제 선택 시
        $('.easy_select').show();
        $('.pay_btn.easy').addClass("selected");
        $('.pay_btn.card').removeClass("selected");
    });

    $('.payment_option').click(function() {
        $('.payment_option').removeClass('selected');
        $(this).addClass('selected');
    });

    // $('.card_list .card').on('click', function() {
    //     let cardCode = handleCardClick.call(this);
    // });

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

    $('#checkoutBtn').click(function() {
        if (validateBeforeSubmit()) {
            payment('CARD');
        } else {
            return false;
        }
    });
});
function resetNewDelivery() {
    let directInputContainer = $(".direct_input_wrap");

    $("#deliveryName").val('');
    $("#recipientName").val('');
    $("#zipCode").val('');
    $("#basicAddress").val('');
    $("#detailAddress").val('');
    $("#phonenumber1").val('010');
    $("#phonenumber2").val('');
    $("#phonenumber3").val('');
    $("#phonenumber3").val('');
    $("#shippingMsg").prop('selectedIndex', 0);
    $(".default_delivery input[type='checkbox']").prop('checked', false);
    $("#directInput").hide();
}
// 부모 창의 팝업 창 닫기 함수
function closeDeliveryPopup() {
    let deliveryPopup = window.open('', '주소 입력');
    if (deliveryPopup && !deliveryPopup.closed) {
        deliveryPopup.close();
    }
}
window.closeDeliveryPopup = closeDeliveryPopup;

// 부모 창의 배송 정보 업데이트 함수
function updateDelivery(deliveryData) {
    if (window.opener) {
        window.opener.$('#defaultDelivery #defaultRecipientName').text(deliveryData.recipient);
        window.opener.$('#defaultDelivery #defaultZipCode').text(deliveryData.zipCode);
        window.opener.$('#defaultDelivery #defaultBasicAddress').text(deliveryData.basicAddress);
        window.opener.$('#defaultDelivery #defaultDetailAddress').text(deliveryData.detailAddress);
        window.opener.$('#defaultDelivery #defaultPhonenumber1').text(deliveryData.phoneNumber.split('-')[0]);
        window.opener.$('#defaultDelivery #defaultPhonenumber2').text(deliveryData.phoneNumber.split('-')[1]);
        window.opener.$('#defaultDelivery #defaultPhonenumber3').text(deliveryData.phoneNumber.split('-')[2]);
        window.opener.Swal.fire({
            text: '배송지가 변경되었습니다.',
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        window.opener.closeDeliveryPopup();
    }
    window.close();
}

// function handleCardClick() {
//     let cardName = $(this).find('.card_name').text();
//
//     $('.card_list .card').removeClass('selected');
//     $(this).addClass('selected');
//     $('.card.selected .card_value span').text(cardName);
//
//     $('.card_list').hide();
//     $('.card.selected').find('.material-symbols-outlined').text('expand_more');
//
//     return cardCompanyCode(); // 선택된 카드의 코드를 반환
// }

// function cardCompanyCode() {
//     let cardCompanyCode = $('.card_list .card.selected').data('code');
//     return cardCompanyCode;
// }
function toggleIcon() {
    let icon = $(this).find(".material-symbols-outlined");
    if (icon.text() === "expand_more") {
        icon.text("expand_less");
    } else {
        icon.text("expand_more");
    }
}
function resetSelections() {
    // 선택된 카드 및 결제 옵션 초기화
    $('.payment_option').removeClass('selected');
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
    let popupUrl = '/popup/deliveries'; // 팝업으로 열 페이지의 URL

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
// function isOrderAgree() {
//     if (!$('#agreeAll').prop('checked')) {
//         alert("주문 내용을 확인하고 모두 동의해주세요.");
//         return false;
//     }
//     return true;
// }
function isOrderAgree() {
    if (!$('#agreeAll').prop('checked')) {
        Swal.fire({
            text: '주문 내용을 확인하고 모두 동의해주세요.',
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
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
        Swal.fire({
            text: '배송지명을 입력해 주세요.',
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }

    // 수령인 검사
    if (!isNameEmpty()) {
        Swal.fire({
            text: '수령인을 입력해 주세요.',
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    } else if (!regexName()) {
        Swal.fire({
            html: '2~12자의 한글, 영문 대/소문자를 사용해 주세요.<br>(특수기호, 공백 사용 불가)',
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }

    // 주소 검사
    if (!isDeliveryEmpty()) {
        Swal.fire({
            text: '주소를 입력해 주세요.',
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }

    // 휴대전화번호 검사
    if (!isPhoneEmpty()) {
        Swal.fire({
            text: '휴대전화번호를 입력해 주세요.',
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });

        return false;
    } else if (!regexPhone()) {
        Swal.fire({
            text: '휴대전화번호 형식으로 입력해 주세요.',
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
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
        Swal.fire({
            text: '수령인을 입력해 주세요.',
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        $('#recipientName').focus();
        return false;
    }

    if (phoneNumber2 === '' || phoneNumber3 === '') {
        Swal.fire({
            text: '휴대전화번호를 입력해 주세요.',
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        if (phoneNumber2 === '') {
            $('#phonenumber2').focus();
        } else {
            $('#phonenumber3').focus();
        }
        return false;
    }

    if (!/^\d{3,4}$/.test(phoneNumber2) || !/^\d{4}$/.test(phoneNumber3)) {
        Swal.fire({
            text: '휴대전화번호 형식으로 입력해 주세요.',
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
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

    if (!$('.pay_btn').hasClass('selected')) {
        Swal.fire({
            text: '결제방식을 선택해 주세요.',
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }

    if ($('.pay_btn.easy').hasClass('selected')) {
        if (!$('.payment_option').hasClass('selected')) {
            Swal.fire({
                text: '결제방식을 선택해 주세요.',
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
            return false;
        }
    }

    Swal.fire({
        text: '주문이 성공적으로 제출되었습니다.',
        showConfirmButton: true,
        confirmButtonText: '확인',
        customClass: mySwal,
        buttonsStyling: false
    });
    return true;
}
function updateDiscount() {
    let productCouponDiscount = 0;
    $(".cart_item").each(function()     {
        let originalPrice = parseInt($(this).find("#productPrice").text().replace(/[^0-9]/g, ""));
        let productSalePriceElement = $(this).find("#productSalePrice");
        let salePrice = productSalePriceElement && productSalePriceElement.text().trim() !== ""
            ? parseInt(productSalePriceElement.text().replace(/[^0-9]/g, ""))
            : originalPrice;
        let itemDiscount = originalPrice - salePrice;
        productCouponDiscount += itemDiscount;
    });

    return productCouponDiscount;
}
function handlePoint() {
    let availablePointText = $("#ownedPoint").text().replace(",", "");
    let availablePoint = parseInt(availablePointText);
    $("#availablePoint").text(availablePoint.toLocaleString());

    // 사용 가능한 마일리지가 0이면 #useAll 버튼을 비활성화
    if (availablePoint === 0) {
        $("#useAll").prop("disabled", true).addClass("disabled");
        $("#point").prop("disabled", true).addClass("disabled");
    }

    $("#useAll").click(function() {
        if ($(this).text() === "모두 사용") {
            // 입력란에 사용 가능한 마일리지 값 설정
            $("#point").val(availablePoint);
            $("#ownedPoint").text("0");
            // "사용 안함" 버튼 텍스트 변경
            $(this).text("사용 안함");
        } else {
            // 입력란에 0으로 설정
            $("#point").val("0");
            // 보유 마일리지 값 원래대로 설정
            $("#ownedPoint").text(availablePoint.toLocaleString());
            // "모두 사용" 버튼 텍스트 변경
            $(this).text("모두 사용");
            // 입력란 활성화
            if (availablePoint > 0) {
                $("#point").prop("disabled", false);
            }
        }
        // 마일리지가 변경되었을 때는 할인 내역과 회원 쿠폰 내역을 유지한 채로 주문 요약 업데이트
        updateOrderSummary(updateDiscount(), parseInt($(".total.discount .content").text().replace(/[^0-9]/g, "")));
    });

    $("#point").on("input", function() {
        let point = $(this).val().replace(/[^0-9]/g, "");
        $(this).val(point.replace(/\B(?=(\d{3})+(?!\d))/g, ""));

        let pointInput = parseInt($(this).val().replace(/[^0-9]/g, "") || 0); // 입력된 값이 없을 때는 0으로 설정
        let ownedPoint = availablePoint - pointInput;
        ownedPoint = Math.max(ownedPoint, 0); // 음수인 경우 0으로 설정

        if (pointInput > availablePoint) {
            $(this).val(availablePoint);
            Swal.fire({
                text: '사용 가능한 마일리지를 초과했습니다.',
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
        }

        $("#ownedPoint").text(ownedPoint.toLocaleString());
        // 만약 사용 가능한 마일리지가 0이면 "모두 사용" 버튼과 #point 입력란 비활성화, 그렇지 않으면 활성화
        if (availablePoint === 0) {
            $("#useAll").prop("disabled", true).addClass("disabled");
            $("#point").prop("disabled", true);
        } else {
            $("#useAll").prop("disabled", false).removeClass("disabled");
            $("#point").prop("disabled", false);
        }
        // 마일리지가 변경되었을 때는 할인 내역과 회원 쿠폰 내역을 유지한 채로 주문 요약 업데이트
        updateOrderSummary(updateDiscount(), parseInt($(".total.discount .content").text().replace(/[^0-9]/g, "")));
    });
}
handlePoint();
function updateOrderSummary(productCouponDiscount) {
    productCouponDiscount = productCouponDiscount || 0;

    let totalProductPrice = 0;
    $(".cart_item").each(function() {
        let productPrice = parseInt($(this).find("#productPrice").text().replace(/[^0-9]/g, ""), 10);
        if (!isNaN(productPrice)) {
            totalProductPrice += productPrice;
        }
    });

    // $("#point").val() 값이 존재하고 null이 아닌 경우에만 처리
    let pointValue = $("#point").val();
    let usedPoint = 0;
    if (pointValue !== undefined && pointValue !== null) {
        let parsedPoint = parseInt(pointValue.replace(/[^0-9]/g, ""), 10);
        if (!isNaN(parsedPoint)) {
            usedPoint = parsedPoint;
        }
    }

    let discountedTotalProductPrice = totalProductPrice - productCouponDiscount;
    let totalDiscount = productCouponDiscount;
    let shippingFee = (totalProductPrice - totalDiscount >= 50000) ? 0 : 3000;
    let totalPrice = discountedTotalProductPrice + shippingFee - usedPoint;

    $(".total.price .content").text(totalProductPrice.toLocaleString() + "원");
    $(".total.discount .content").text(totalDiscount > 0 ? "-" + totalDiscount.toLocaleString() + "원" : "0원");
    $(".total.point .content").text(usedPoint > 0 ? "-" + usedPoint.toLocaleString() + "원" : "0원");
    $(".total_price .content").text(totalPrice.toLocaleString() + "원");
    $(".total.shipping .content").text(shippingFee.toLocaleString() + "원");
}

updateOrderSummary(updateDiscount());
function getTotalPrice() {
    let orderTotalPrice = parseInt($('#orderTotalPrice').text().replace(/[^0-9]/g, ''), 10);

    return orderTotalPrice
}
function getOrderName() {
    let firstProductName = $('.cart_item').first().find('.name > span').text();
    let totalProducts = $('.cart_item').length;
    let orderName;

    if (totalProducts === 1) {
        orderName = firstProductName;
    } else {
        orderName = `${firstProductName} 외 ${totalProducts - 1}개`;
    }

    return orderName;
}

