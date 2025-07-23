$(document).ready(function(){

    initPopup();
    handlePoint();

    // 배송지 팝업에서 값 받아오기
    $('#deliveryList').on('click', 'li .delivery_item', function() {
        let deliveryData = {
            deliveryName: $(this).find('.delivery_name .popup_delivery_name').text().trim(),
            recipient: $(this).find('.recipient_info .popup_recipient_name').text().trim(),
            detailAddress: $(this).find('.delivery_details .popup_detail_address').text().trim(),
            phoneNumber: $(this).find('.recipient_info .popup_phonenumber').text().trim(),
            zipCode: $(this).find('.delivery_details .popup_zip_code').text().trim(),
            basicAddress: $(this).find('.delivery_details .popup_basic_address').text().trim()
        };

        updateDelivery(deliveryData);
    });

    // 기본 배송지 탭을 활성화
    $('.delivery_tabs li:first-child').addClass('active');
    $('.input_area.default').addClass('active');

    // 기본 배송지 탭 클릭 시 신규 입력 탭 초기화
    $('.delivery_tabs li:first-child').click(function(){
        $(this).addClass('active').siblings().removeClass('active');
        $('.input_area.default').addClass('active').siblings('.input_area').removeClass('active');
        resetNewDelivery();
    });

    // 신규 입력 탭 클릭 시 기본 배송지 탭 초기화
    $('.delivery_tabs li:last-child').click(function(){
        $(this).addClass('active').siblings().removeClass('active');
        $('.input_area:last-child').addClass('active').siblings('.input_area').removeClass('active');
    });


    $('#detailAddress').click(function () {
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
        }
    });

    // 배송 메세지 선택, 직접입력 선택 시 입력창 활성화
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

    // 카드결제 클릭 시 다른 결제수단 비활성화
    $('.pay_btn.card').click(function() {
        resetSelections();

        $('.pay_btn.card').addClass("selected");
        $('.easy_select').hide();
        $('.pay_btn.easy').removeClass("selected");
    });

    // 간편결제 클릭 시 다른 결제수단 비활성화
    $('.pay_btn.easy').click(function() {
        resetSelections();

        $('.easy_select').show();
        $('.pay_btn.easy').addClass("selected");
        $('.pay_btn.card').removeClass("selected");
    });

    // 간편결제 옵션 클릭 시 클래스 추가
    $('.payment_option').click(function() {
        $('.payment_option').removeClass('selected');
        $(this).addClass('selected');
    });

    // 전체 동의 클릭 시 전체 체크
    $("#agreeAll").click(function () {
        let isChecked = $(this).prop("checked");
        $(".order_agree input[type='checkbox']").prop("checked", isChecked);
    });

    // 개별 동의 체크박스 변경
    $(".order_agree input[type='checkbox']").click(function () {
        let totalCheckbox = $(".order_agree:not(.all) input[type='checkbox']").length;
        let checkedCheckbox = $(".order_agree:not(.all) input[type='checkbox']:checked").length;
        let allChecked = totalCheckbox === checkedCheckbox;
        $("#agreeAll").prop("checked", allChecked);
    });
});

// 배송지 입력창 초기화
function resetNewDelivery() {

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

// 팝업 창 닫기
function closeDeliveryPopup() {
    let deliveryPopup = window.open('', '주소 입력');
    if (deliveryPopup && !deliveryPopup.closed) {
        deliveryPopup.close();
    }
}
window.closeDeliveryPopup = closeDeliveryPopup;

// 주문서 배송 정보 업데이트
function updateDelivery(deliveryData) {
    if (window.opener) {
        window.opener.$('#defaultDelivery #defaultDeliveryName').text(deliveryData.deliveryName);
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

// 결제 옵션 선택 초기화
function resetSelections() {
    $('.payment_option').removeClass('selected');
}

// 배송지 팝업 열기
function initPopup() {
    let width = 450;
    let height = 500;
    let left = (window.screen.width / 2) - (width / 2);
    let top = (window.screen.height / 2) - (height / 2);
    let popupUrl = '/popup/deliveries';

    let popupName = '주소 입력';
    let popupOptions =  `width=${width},height=${height},top=${top},left=${left}`;

    let popupBtn = $('.delivery_popup_btn');

    popupBtn.on('click', function (event) {
        event.preventDefault();
        window.open(popupUrl, popupName, popupOptions);
    });
}

// 주문 동의 검증
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

// 배송지명 검증
function isDeliveryNameEmpty() {
    let deliveryName = $("#deliveryName").val();

    if (deliveryName.trim() === '') {
        return false;
    }
    return true;
}

// 이름 검증
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

// 배송지 검증
function isDeliveryEmpty() {

    let zipCode = $('#zipCode').val().trim();
    if (zipCode === '') {
        return false;
    }
    let basicAddress = $('#basicAddress').val().trim();
    if (basicAddress === '') {
        return false;
    }
    return true;
}

// 휴대전화번호 검증
function isPhoneEmpty() {
    let phonenumber2 = $('#phonenumber2').val();
    let phonenumber3 = $('#phonenumber3').val();

    if (phonenumber2.trim() === '' || phonenumber3.trim() === '') {
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
            html: '수령인은 2~30자의 한글,<br>영문 대/소문자를 사용해 주세요.<br>(특수기호, 공백 사용 불가)',
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

// 배송지 팝업 수정 시 유효성 검사
function validateEditPopup() {
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
        return false;
    } else if (!regexName()) {
        Swal.fire({
            html: '수령인은 2~30자의 한글,<br>영문 대/소문자를 사용해 주세요.<br>(특수기호, 공백 사용 불가)',
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
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

// 전체 유효성 검사
function validateBeforeSubmit() {

    if ($('#newDelivery').hasClass('active')) {
        if (!validateNewDelivery()) {
            return false;
        }
    }

    if (!$('.pay_btn').hasClass('selected')) {
        Swal.fire({
            text: '결제방법을 선택해 주세요.',
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
                text: '결제방법을 선택해 주세요.',
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
            return false;
        }
    }

    if (!isOrderAgree()) {
        return false;
    }

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
    $("#point").val("");

    let ownedPoint = $("#ownedPoint");
    let pointValue = parseInt(ownedPoint.text().replace(/[^0-9]/g, ''), 10);
    ownedPoint.text(pointValue.toLocaleString());

    let availablePointText = $("#ownedPoint").text().replace(",", "");
    let availablePoint = parseInt(availablePointText);
    $("#availablePoint").text(availablePoint.toLocaleString());

    // 사용 가능한 적립금이 0이면 버튼 비활성화
    if (availablePoint === 0) {
        $("#useAll").prop("disabled", true).addClass("disabled");
        $("#point").prop("disabled", true).addClass("disabled");
    }

    $("#useAll").click(function () {
        if ($(this).text() === "모두 사용") {
            if (availablePoint < 1000) {
                Swal.fire({
                    text: '적립금은 1,000원 이상부터 사용 가능합니다.',
                    showConfirmButton: true,
                    confirmButtonText: '확인',
                    customClass: mySwal,
                    buttonsStyling: false
                });
                return;
            }

            if (availablePoint % 10 !== 0) {
                Swal.fire({
                    text: '적립금은 10원 단위로만 사용 가능합니다.',
                    showConfirmButton: true,
                    confirmButtonText: '확인',
                    customClass: mySwal,
                    buttonsStyling: false
                });
                return;
            }

            // 조건 통과 시 전체 사용 적용
            $("#point").val(availablePoint.toLocaleString());
            $("#ownedPoint").text("0");
            $(this).text("사용 안함");
        } else {
            $("#point").val("");
            $("#ownedPoint").text(availablePoint.toLocaleString());
            $(this).text("모두 사용");

            if (availablePoint > 0) {
                $("#point").prop("disabled", false);
            }
        }

        updateOrderSummary(updateDiscount(), parseInt($(".total.discount .content").text().replace(/[^0-9]/g, "")));
    });

    $("#point").on("input", function () {
        let point = $(this).val().replace(/[^0-9]/g, "");
        let pointInput = parseInt(point || '0');
        $(this).val(pointInput.toLocaleString());

        if ($("#useAll").text() === "사용 안함") {
            $("#useAll").text("모두 사용");
        }
    });

    $("#point").on("blur", function () {
        let point = $(this).val().replace(/[^0-9]/g, "");
        let pointInput = parseInt(point || '0');
        let ownedPointLeft = availablePoint - pointInput;

        if (pointInput > 0 && pointInput < 1000) {
            Swal.fire({
                text: '적립금은 1,000원 이상부터 사용 가능합니다.',
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
            $(this).val('');
            $("#ownedPoint").text(availablePoint.toLocaleString());
            return;
        }

        if (pointInput > availablePoint) {
            Swal.fire({
                text: '사용 가능한 적립금을 초과했습니다.',
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
            pointInput = availablePoint;
            $(this).val(pointInput.toLocaleString());
            ownedPointLeft = 0;
        } else {
            ownedPointLeft = Math.max(availablePoint - pointInput, 0);
        }

        if (pointInput % 10 !== 0) {
            Swal.fire({
                text: '10원 단위로만 사용 가능합니다.',
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
            pointInput = Math.floor(pointInput / 10) * 10;
            $(this).val(pointInput.toLocaleString());
        }


        $("#ownedPoint").text(ownedPointLeft.toLocaleString());

        // 버튼 상태 다시 확인
        if (availablePoint === 0) {
            $("#useAll").prop("disabled", true).addClass("disabled");
            $("#point").prop("disabled", true);
        } else {
            $("#useAll").prop("disabled", false).removeClass("disabled");
            $("#point").prop("disabled", false);
        }

        updateOrderSummary(updateDiscount(), parseInt($(".total.discount .content").text().replace(/[^0-9]/g, "")));
    });
}

// 주문 요약 업데이트
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

// 주문명 생성
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

function getTotalPrice() {
    let orderTotalPrice = parseInt($('#orderTotalPrice').text().replace(/[^0-9]/g, ''), 10);

    return orderTotalPrice
}