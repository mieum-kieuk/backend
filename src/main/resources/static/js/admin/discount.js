$(document).ready(function () {
    setupDateTimePicker();
    setupDate();
    selectCheckbox();
    selectCheckboxes();
    productOptionToggle();
    popupButton();
    addItems();
    $('.submit_btn').click(function (event) {
        if (!validateBeforeSubmit()) {
            event.preventDefault();
        }
    });
});

let discountPopup = null;

function setupDateTimePicker() {
    $('.datetimepicker').datetimepicker({
        format: 'Y-m-d H:i',
        step: 5,
        minDate: 0,
        minTime: 0,
        onShow: function (ct) {
            this.setOptions({
                minTime: 0 // 선택된 날짜가 오늘인 경우, 현재 시간 이후만 선택 가능
            });
        }
    });
}

function setupDate() {
    let currentDate = new Date();
    let minDate = currentDate.toISOString().slice(0, 16);
    $('#startedAt').attr('min', minDate);
    $('#expiredAt').attr('min', minDate);
}

function selectCheckbox() {
    $('.search_result input[type="checkbox"]').on('change', function () {
        let isChecked = $(this).prop('checked');

        if (isChecked) {
            $('.search_result input[type="checkbox"]').not(this).prop('checked', false);
        }
    });
}

function selectCheckboxes() {
    $('#selectAll').click(function () {
        let isChecked = $(this).prop('checked');
        $('.result_table input[type="checkbox"]').prop('checked', isChecked);
        $('.list_content input[type="checkbox"]').prop('checked', isChecked);
    });

    $('.result_table').on('change', 'input[type="checkbox"]', function () {
        let totalCheck = $('.result_table tbody input[type="checkbox"]').length;
        let checkedBox = $('.result_table tbody input[type="checkbox"]:checked').length;
        $('#selectAll').prop('checked', totalCheck === checkedBox);
    });

    $('.list_content').on('change', 'input[type="checkbox"]', function () {
        let totalCheck = $('.list_content .list input[type="checkbox"]').length;
        let checkedBox = $('.list_content .list input[type="checkbox"]:checked').length;
        $('#selectAll').prop('checked', totalCheck === checkedBox);
    });
}

function productOptionToggle() {
    $('input[name="productOption"]').on('change', function () {
        if ($(this).val() === 'specific') {
            $('.input_box_wrap.product').show();
        } else {
            $('.input_box_wrap.product').hide();
        }
    });
}

function popupButton() {
    $('#popupBtn').click(function () {
        openDiscountPopup();
    });
}

function openDiscountPopup() {
    if (discountPopup && !discountPopup.closed) {
        discountPopup.focus();
        return;
    }
    discountPopup = window.open("./discount_popup.html", "_blank", "width=600,height=450");
}

function closeDiscountPopup() {
    if (discountPopup && !discountPopup.closed) {
        discountPopup.close();
    }
}

function addItems() {
    $('.add_btn').on('click', function () {
        let item = $('.search_result .list.product .list_item').has('input:checked').closest('.list_item');
        let selectedItem = [];

        if (selectedItem.length === 0) {
            Swal.fire({
                text: "상품을 선택해 주세요.",
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
            return;
        }

        item.each(function () {
            let productName = $(this).find('.item.title').text();
            let productPrice = $(this).find('.item.price').text();
            let productImage = $(this).find('.item.img img').attr('src');

            selectedItem.push({
                productName: productName,
                productPrice: productPrice,
                productImage: productImage,
            });
        });

        $.ajax({
            type: 'POST',
            url: '/ajax/',
            data:
            success: function (response) {
                if (response.hasDiscount) {
                    Swal.fire({
                            text: "이미 적용된 할인이 있습니다. 변경하시겠습니까?",
                        showCancelButton: true,
                        cancelButtonText: '아니요',
                        confirmButtonText: '예',
                        customClass: mySwalConfirm,
                        reverseButtons: true,
                        buttonsStyling: false
                    }).then((result) => {
                        if (result.isConfirmed) {
                            if (window.opener) {
                                window.opener.getItem(selectedItem);
                                window.close();
                            }
                        }
                    });
                } else {
                    if (window.opener) {
                        window.opener.getItem(selectedItem);
                        window.close();
                    }
                }
            },
            error: function () {
                Swal.fire({
                    html: "오류가 발생했습니다.<br>다시 시도해 주세요.",
                    showConfirmButton: true,
                    confirmButtonText: '확인',
                    customClass: mySwal,
                    buttonsStyling: false
                });
            }
        });
    });
}

window.getItem = function (items) {
    let listContainer = $('.input_box_wrap.product .list.product');
    items.forEach(function (item) {
        let newItemHtml = `
            <div class="list_item">
                <div class="item check">
                    <input type="checkbox" name="checkBox">
                </div>
                <div class="item img"><img src="${item.productImage}" alt="상품 이미지"></div>
                <div class="item title">${item.productName}</div>
                <div class="item price">${item.productPrice}</div>
            </div>
        `;
        listContainer.append(newItemHtml);
    });
}
$('.input_box_wrap.product #deleteBtn').click(function () {
    let productId = [];
    let checkbox = $('input[name=checkBox]:checked');
    let productItems = $('.list.product').children();
    if (productItems.length === 0) {
        Swal.fire({
            text: "삭제할 상품이 없습니다.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }
    if (checkbox.length === 0) {
        Swal.fire({
            text: "삭제할 상품을 선택해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    } else {
        Swal.fire({
            text: '상품을 삭제하시겠습니까?',
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
                checkbox.each(function () {
                    let productId = $(this).attr('id').split('checkbox')[1];
                    productId.push(productId);
                });

                $.ajax({
                    type: 'DELETE',
                    url: '/ajax/admin/',
                    contentType: 'application/json',
                    data: JSON.stringify(),
                    beforeSend: function (xhr) {
                        xhr.setRequestHeader(csrfHeader, csrfToken);
                    },
                    success: function (data) {
                        if (data.code === 200) {
                            window.location.reload();
                        } else {
                            Swal.fire({
                                text: data.message,
                                showConfirmButton: true,
                                confirmButtonText: '확인',
                                customClass: mySwal,
                                buttonsStyling: false
                            });
                        }
                    },
                    error: function () {
                        Swal.fire({
                            html: "삭제 중 문제가 발생했습니다.<br>다시 시도해 주세요.",
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
});

function validateBeforeSubmit() {
    let discountName = $('#name').val().trim();
    let discountPercent = $('#discountPercent').val().trim();
    let productOption = $('input[name="productOption"]:checked').val();
    let startedAt = $('#startedAt').val().trim();
    let expiredAt = $('#expiredAt').val().trim();

    if (discountName === '') {
        Swal.fire({
            text: "할인명을 입력해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }
    if (startedAt === '' && expiredAt === '') {
        Swal.fire({
            text: "할인 기간을 지정해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }
    if (startedAt === '') {
        Swal.fire({
            text: "시작일시를 지정해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }
    if (expiredAt === '') {
        Swal.fire({
            text: "종료일시를 지정해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }

    let startDate = new Date(startedAt);
    let endDate = new Date(expiredAt);

    if (startDate >= endDate) {
        Swal.fire({
            text: "시작일시는 종료일시보다 이전이어야 합니다.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }
    if (discountPercent === '') {
        Swal.fire({
            text: "할인율을 입력해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }

    let discountPercentRegex = /^(?:1|[1-9]\d?|100)$/;
    if (!discountPercentRegex.test(discountPercent)) {
        Swal.fire({
            text: "1부터 100 사이의 값을 입력해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }

    if (!productOption) {
        Swal.fire({
            text: "적용 상품을 선택해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }
    if (productOption === 'specific') {
        let listItems = $('.list_item');
        if (listItems.length === 0) {
            Swal.fire({
                text: "상품을 선택해 주세요.",
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
            return false;
        }
    }

    return true;
}

//상품 할인 단건 삭제
function deleteDiscount(discountId) {

    let csrfHeader = $("meta[name='_csrf_header']").attr("content");
    let csrfToken = $("meta[name='_csrf']").attr("content");

    Swal.fire({
        text: "삭제하시겠습니까?",
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
            $.ajax({
                type: 'DELETE',
                url: '/ajax/admin/discount/delete',
                async: false,
                data: {'discountId': discountId},
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(csrfHeader, csrfToken)
                },
                success: function (data) {
                    if (data.code === 200) {
                        window.location.href = '/admin/discounts';
                    } else {
                        Swal.fire({
                            text: data.message,
                            showConfirmButton: true,
                            confirmButtonText: '확인',
                            customClass: mySwal,
                            buttonsStyling: false
                        });
                    }
                },
                error: function () {
                    Swal.fire({
                        html: "삭제중 오류가 발생했습니다.<br>다시 시도해 주세요.",
                        showConfirmButton: true,
                        confirmButtonText: '확인',
                        customClass: mySwal,
                        buttonsStyling: false
                    });
                }
            })
        }
    });
};

//상품 할인 여러건 삭제
$('#deleteDiscountsBtn').click(function () {

    let discountIds = [];
    let checkboxes = $('input[name=checkbox]:checked');
    let csrfHeader = $("meta[name='_csrf_header']").attr("content");
    let csrfToken = $("meta[name='_csrf']").attr("content");

    if (checkboxes.length == 0) {
        Swal.fire({
            text: "삭제할 할인을 선택해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    } else {
        Swal.fire({
            text: checkboxes.length + '개의 상품을 삭제하시겠습니까?',
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
                $(checkboxes.each(function (v) {
                    let discountId = checkboxes[v].id.split('checkbox')[1];
                    discountIds.push(discountId);
                }));

                $.ajax({
                    type: 'DELETE',
                    url: '/ajax/admin/discounts/delete',
                    async: false,
                    contentType: 'application/json',
                    data: JSON.stringify(discountIds),
                    beforeSend: function (xhr) {
                        xhr.setRequestHeader(csrfHeader, csrfToken)
                    },
                    success: function (data) {
                        if (data.code === 200) {
                            window.location.href = '/admin/discounts';
                        } else {
                            Swal.fire({
                                text: data.message,
                                showConfirmButton: true,
                                confirmButtonText: '확인',
                                customClass: mySwal,
                                buttonsStyling: false
                            });
                        }
                    },
                    error: function () {
                        Swal.fire({
                            html: "삭제중 오류가 발생했습니다.<br>다시 시도해 주세요.",
                            showConfirmButton: true,
                            confirmButtonText: '확인',
                            customClass: mySwal,
                            buttonsStyling: false
                        });
                    }
                })
            }
        });
    }
});
