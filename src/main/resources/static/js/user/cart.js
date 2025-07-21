$(document).ready(function () {

    const wrapper = $('.cart_wrap');
    const width = $(window).innerWidth() - 400;
    wrapper.css('min-width', width + 'px');

    updateTotalPrice();
    updateShippingFee();

    // 전체 선택 체크박스 변경 시 총 가격, 배송비 업데이트
    $('#checkAll').change(function () {
        let isChecked = $(this).prop('checked');

        $('.cart_checkbox').not('.sold_out').prop('checked', isChecked);

        updateTotalPrice();
        updateCheckAllState();
        updatePurchaseButton();

        if (!isChecked) {
            updateShippingFee(0);
        }
    });

    // 체크박스 변경 시 총 가격, 구매 버튼 상태 업데이트
    $('.cart_checkbox').change(function () {
        let validCheckboxes = $('.cart_content .cart_checkbox').not('.sold_out');
        let allChecked = validCheckboxes.length > 0 &&
            validCheckboxes.filter(':checked').length === validCheckboxes.length;

        $('#checkAll').prop('checked', allChecked);

        updateCheckAllState();
        updateTotalPrice();
        updatePurchaseButton();
    });

    // 수량 1일 때 감소 버튼 비활성화
    $('.quant_input').each(function () {
        let currentValue = parseInt($(this).val());
        let decreaseBtn = $(this).siblings('.decrease');
        if (currentValue === 1) {
            decreaseBtn.addClass('disabled');
        }
    });

    $('#deleteBtn').click(function () {
        deleteProducts();
    });

    $('#deleteSoldOutBtn').click(function () {
        deleteSoldOutProducts();
    });

    $('.remove_btn').on('click', function () {
        let productId = $(this).data('id');
        deleteProduct(productId);
    });

    $('#checkoutBtn').click(async function () {
        checkout();
    });

    updateCheckAllState();
    updatePurchaseButton();
});

// 전체체크 업데이트
function updateCheckAllState() {
    const validCheckboxes = $('.cart_content .cart_checkbox').not('.sold_out');

    if (validCheckboxes.length === 0) {
        $('#checkAll').prop('checked', false).prop('disabled', true);
    } else {
        const allChecked = validCheckboxes.filter(':checked').length === validCheckboxes.length;
        $('#checkAll').prop('checked', allChecked).prop('disabled', false);
    }
}

// 개별 가격 업데이트
function updateItemPrice(cartItem) {
    let quantity = parseInt(cartItem.find('.quant_input').val());
    let originalPrice = parseFloat(cartItem.find('.item.info .original_price #productPrice').text().replace(/원|,/g, ''));

    let discountPriceElement = cartItem.find('.item.info .sale_price #productDiscountPrice');
    let discountPrice = 0;
    if (discountPriceElement.length > 0) {
        discountPrice = parseFloat(discountPriceElement.text().replace(/원|,/g, ''));
    } else {
        discountPrice = originalPrice;
    }

    let itemTotalPrice = quantity * discountPrice;
    cartItem.find('.item.price .sale_price').text(addCommas(itemTotalPrice) + '원');
}

// 가격 업데이트
function updateTotalPrice() {

    let totalPrice = 0;
    let cartProductPrice = 0;
    let cartDiscountPrice = 0;

    // 선택된 상품의 가격 합계 및 할인 금액 계산
    $('.cart_item').each(function () {
        let isChecked = $(this).find('.cart_checkbox').prop('checked');
        if (isChecked) {
            let quantity = parseInt($(this).find('.quant_input').val());
            let originalPrice = parseFloat($(this).find('.item.info .original_price #productPrice').text().replace('원', '').replace(',', ''));

            let discountPriceElement = $(this).find('.item.info .sale_price #productDiscountPrice');
            let discountPrice = 0;
            if (discountPriceElement.length > 0) {
                // 할인가가 있는 경우
                discountPrice = parseFloat(discountPriceElement.text().replace('원', '').replace(',', ''));
                $(this).find('.item.price #productSalePrice').text(addCommas(discountPrice * quantity) + '원');
            } else {
                // 할인가가 없는 경우
                discountPrice = originalPrice;
                let salePrice = originalPrice;
                $(this).find('.item.price #productSalePrice').text(addCommas(salePrice * quantity) + '원');
            }

            let itemTotalPrice = quantity * discountPrice; // 할인된 가격으로 계산
            let itemOriginalPrice = quantity * originalPrice;
            let itemDiscountPrice = itemOriginalPrice - itemTotalPrice;

            totalPrice += itemTotalPrice;
            cartProductPrice += itemOriginalPrice; // 할인 적용 전 가격 합계
            cartDiscountPrice += itemDiscountPrice;
        }

    });

    // 상품 합계 업데이트
    $('.total.price .content').text(addCommas(cartProductPrice) + '원');
    // 상품 할인 금액 업데이트
    $('.total.discount .content').text('-' + addCommas(cartDiscountPrice) + '원');
    if (cartDiscountPrice === 0) {
        $('.total.discount .content').text('0원');
    } else {
        $('.total.discount .content').text('-' + addCommas(cartDiscountPrice) + '원');
    }

    // 결제 예정 금액 계산 및 업데이트
    let paymentAmount = cartProductPrice - cartDiscountPrice;
    paymentAmount += updateShippingFee();
    $('.total_price .content').text(addCommas(paymentAmount) + '원');
}

// 배송비 업데이트
function updateShippingFee() {
    let shippingFee = 3000; // 기본 배송비는 3000원으로 설정

    let cartProductPrice = parseInt($('#cartProductPrice').text().replace('원', '').replace(',', ''));
    let cartDiscountPrice = parseInt($('#cartDiscountPrice').text().replace('원', '').replace(',', ''));

    // 아무 상품도 선택되지 않았을 때 배송비는 0원
    if (cartProductPrice === 0) {
        shippingFee = 0;
    }

    // 장바구니 총 가격에서 배송비를 제외한 금액이 50000원 이상이면 배송비는 0원
    else if (cartProductPrice + cartDiscountPrice >= 50000) {
        shippingFee = 0;
    }

    // 배송비를 HTML에 반영
    $('#shippingFee').text(addCommas(shippingFee) + '원');

    return shippingFee; // 배송비 반환
}

// 구매하기 버튼 상태 업데이트
function updatePurchaseButton() {
    let cartItems = $('.cart_item');
    let validItems = cartItems.filter(function () {
        return !$(this).hasClass('sold_out') &&
            $(this).find('.cart_checkbox').prop('checked');
    });

    let hasValidItem = validItems.length > 0;

    $('.submit_btn')
        .prop('disabled', !hasValidItem)
        .toggleClass('disabled', !hasValidItem);
}

// 콤마 추가
function addCommas(num) {
    return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

// 상품 개수 감소
function decreaseCount(productId) {
    let csrfToken = $("meta[name='_csrf']").attr("content");
    let csrfHeader = $("meta[name='_csrf_header']").attr("content");

    let decreaseBtn = $('#decreaseBtn' + productId);
    let input = decreaseBtn.siblings('.quant_input');
    let currentValue = parseInt(input.val());
    let cartItem = decreaseBtn.closest('.cart_item');

    if (!cartItem.hasClass('sold_out') && currentValue > 1) {
        $.ajax({
            type: 'POST',
            url: '/ajax/cart/decrease',
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken);
            },
            data: {'productId': productId},
            success: function (result) {
                if (result.status == 200) {
                    input.val(currentValue - 1);
                    if (currentValue === 2) {
                        decreaseBtn.addClass('disabled');
                    }
                    updateItemPrice(cartItem);
                    updateTotalPrice();
                } else {
                    Swal.fire({
                        html: result.message.replace('\n', '<br>'),
                        showConfirmButton: true,
                        confirmButtonText: '확인',
                        customClass: mySwal,
                        buttonsStyling: false
                    });
                }
            },
            error: function () {
                Swal.fire({
                    html: '수량 변경 중 문제가 발생했습니다.<br>다시 시도해주세요.',
                    showConfirmButton: true,
                    confirmButtonText: '확인',
                    customClass: mySwal,
                    buttonsStyling: false
                });
            }
        })
    } else {
        decreaseBtn.addClass('disabled');
    }
}

// 상품 개수 증가
function increaseCount(productId) {
    let csrfToken = $("meta[name='_csrf']").attr("content");
    let csrfHeader = $("meta[name='_csrf_header']").attr("content");

    let increaseBtn = $('#increaseBtn' + productId);
    let input = increaseBtn.siblings('.quant_input');
    let currentValue = parseInt(input.val());
    let cartItem = increaseBtn.closest('.cart_item');
    let decreaseBtn = increaseBtn.siblings('.decrease');

    if (!cartItem.hasClass('sold_out')) {
        $.ajax({
            type: 'POST',
            url: '/ajax/cart/increase',
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken);
            },
            data: {'productId': productId},
            success: function (result) {
                if (result.status == 200) {
                    input.val(currentValue + 1);
                    if (currentValue === 1) {
                        decreaseBtn.removeClass('disabled');
                    }
                    updateItemPrice(cartItem);
                    updateTotalPrice();
                } else {
                    Swal.fire({
                        html: result.message.replace('\n', '<br>'),
                        showConfirmButton: true,
                        confirmButtonText: '확인',
                        customClass: mySwal,
                        buttonsStyling: false
                    });
                }
            },
            error: function (xhr) {
                let result = JSON.parse(xhr.responseText);
                if(xhr.status == 400) {
                    Swal.fire({
                        html: result.message.replace('\n', '<br>'),
                        showConfirmButton: true,
                        confirmButtonText: '확인',
                        customClass: mySwal,
                        buttonsStyling: false
                    });
                } else {
                    Swal.fire({
                        html: '수량 변경 중 문제가 발생했습니다.<br>다시 시도해주세요.',
                        showConfirmButton: true,
                        confirmButtonText: '확인',
                        customClass: mySwal,
                        buttonsStyling: false
                    });
                }
            }
        })
    } else {
        increaseBtn.addClass('disabled');
    }
}

// x 로 단건 삭제
function deleteProduct(productId) {
    let csrfToken = $("meta[name='_csrf']").attr("content");
    let csrfHeader = $("meta[name='_csrf_header']").attr("content");

    let productIds = []
    productIds.push(productId);

    Swal.fire({
        text: '삭제하시겠습니까?',
        showCancelButton: true,
        cancelButtonText: '아니요',
        confirmButtonText: '예',
        customClass: mySwalConfirm,
        reverseButtons: true,
        buttonsStyling: false,
    }).then((result) => {
        if (result.isConfirmed) {
            $.ajax({
                type: 'DELETE',
                url: '/ajax/cart',
                contentType: 'application/json',
                data: JSON.stringify(productIds),
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(csrfHeader, csrfToken);
                },
                success: function (result) {
                    if (result.status == 200) {
                        location.reload();
                    } else {
                        Swal.fire({
                            html: result.message.replace('\n', '<br>'),
                            showConfirmButton: true,
                            confirmButtonText: '확인',
                            customClass: mySwal,
                            buttonsStyling: false
                        });
                    }
                },
                error: function () {
                    Swal.fire({
                        html: '삭제 중 문제가 발생하였습니다.<br>다시 시도해주세요.',
                        showConfirmButton: true,
                        confirmButtonText: '확인',
                        customClass: mySwal,
                        buttonsStyling: false
                    });
                }
            });
        }
    });
}

// 상품 여러개 삭제
function deleteProducts() {
    let csrfToken = $("meta[name='_csrf']").attr("content");
    let csrfHeader = $("meta[name='_csrf_header']").attr("content");

    let productIds = [];
    let checkboxes = $('.cart_content input[type=checkbox]:checked');

    if (checkboxes.length == 0) {
        Swal.fire({
            text: '삭제할 상품을 선택해 주세요.',
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    } else {
        Swal.fire({
            text: checkboxes.length === 1 ? '선택하신 상품을 삭제하시겠습니까?' : checkboxes.length + '개의 상품을 삭제하시겠습니까?',
            showCancelButton: true,
            cancelButtonText: '아니요',
            confirmButtonText: '예',
            customClass: mySwalConfirm,
            reverseButtons: true,
            buttonsStyling: false,
        }).then((result) => {
            if (result.isConfirmed) {
                checkboxes.each(function () {
                    let productId = $(this).attr('id').split('checkbox')[1];
                    productIds.push(productId);
                });

                $.ajax({
                    type: 'DELETE',
                    url: '/ajax/cart',
                    contentType: 'application/json',
                    data: JSON.stringify(productIds),
                    beforeSend: function (xhr) {
                        xhr.setRequestHeader(csrfHeader, csrfToken);
                    },
                    success: function (result) {
                        if (result.status == 200) {
                            location.reload();
                        } else {
                            Swal.fire({
                                html: result.message.replace('\n', '<br>'),
                                showConfirmButton: true,
                                confirmButtonText: '확인',
                                customClass: mySwal,
                                buttonsStyling: false
                            });
                        }
                    },
                    error: function () {
                        Swal.fire({
                            html: '삭제 중 문제가 발생했습니다.<br>다시 시도해주세요.',
                            showConfirmButton: true,
                            confirmButtonText: '확인',
                            customClass: mySwal,
                            buttonsStyling: false
                        });
                    }
                });
            }
        });
    }
}

// 솔드아웃된 상품 삭제
function deleteSoldOutProducts() {
    let csrfToken = $("meta[name='_csrf']").attr("content");
    let csrfHeader = $("meta[name='_csrf_header']").attr("content");

    let productIds = [];
    let checkboxes = $('.cart_checkbox.sold_out');

    if (checkboxes.length == 0) {
        Swal.fire({
            text: '품절 상품이 없습니다.',
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    } else {
        Swal.fire({
            text: '품절 상품을 삭제하시겠습니까?',
            showCancelButton: true,
            cancelButtonText: '아니요',
            confirmButtonText: '예',
            closeOnConfirm: false,
            closeOnCancel: true,
            customClass: mySwalConfirm,
            reverseButtons: true,
            buttonsStyling: false,
        }).then((result) => {
            if (result.isConfirmed) {
                checkboxes.each(function (v) {
                    let productId = checkboxes[v].id.split('checkbox')[1];
                    productIds.push(productId);
                });

                $.ajax({
                    type: 'DELETE',
                    url: '/ajax/cart',
                    contentType: 'application/json',
                    data: JSON.stringify(productIds),
                    beforeSend: function (xhr) {
                        xhr.setRequestHeader(csrfHeader, csrfToken);
                    },
                    success: function (result) {
                        if (result.status == 200) {
                            location.reload();
                        } else {
                            Swal.fire({
                                html: result.message.replace('\n', '<br>'),
                                showConfirmButton: true,
                                confirmButtonText: '확인',
                                customClass: mySwal,
                                buttonsStyling: false
                            });
                        }
                    },
                    error: function () {
                        Swal.fire({
                            html: '삭제 중 문제가 발생했습니다.<br>다시 시도해주세요.',
                            showConfirmButton: true,
                            confirmButtonText: '확인',
                            customClass: mySwal,
                            buttonsStyling: false
                        });
                    }
                });
            }
        });
    }
}

// 구매하기
async function checkout() {
    try {
        let csrfToken = $("meta[name='_csrf']").attr("content");
        let csrfHeader = $("meta[name='_csrf_header']").attr("content");

        let productIds = $('.cart_content input[type=checkbox]:checked').map(function () {
            return this.id.split('checkbox')[1];
        }).get();

        const response = await $.ajax({
            type: 'POST',
            url: '/ajax/order/validate',
            contentType: 'application/json',
            data: JSON.stringify(productIds),
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken);
            }
        });

        if (response.status === 200) {
            window.location.href = '/order/checkout';
        } else {
            Swal.fire({
                html: response.message.replace('\n', '<br>'),
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
        }
    } catch (error) {
        let errorMessage = '요청 사항 진행 중 문제가 발생했습니다.<br>다시 시도해 주세요.';
        if (error.responseJSON && error.responseJSON.status === 400) {
            errorMessage = error.responseJSON.message;
        }

        Swal.fire({
            html: errorMessage,
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
    }
};