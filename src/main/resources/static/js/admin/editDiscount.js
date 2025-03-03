$(document).ready(function () {
    popupButton();
    addItems();
    updateNoDataMessage();

    window.updateParentNoDataMessage = function() {
        updateNoDataMessage();
    };

    const productList = $('.list_content');
    const width = $(window).innerWidth() - 490;
    productList.css('min-width', width + 'px');

    // 할인명 중복 검사
    $('#name').on('keyup', function () {
        isNameValid();
    });

    if ($('.period').length) {
        $('#startDate, #expireDate').datepicker({
            dateFormat: 'yy-mm-dd',
            minDate: 0  // 과거 날짜 선택 불가
        });
    }
    $("select").each(function () {
        if ($(this).find("option:selected").val() !== "") {
            $(this).addClass("selected");
        }
    });

    $("select").on("change", function () {
        if ($(this).find("option:selected").val() !== "") {
            $(this).addClass("selected");
        } else {
            $(this).removeClass("selected");
        }
    });

    // 체크박스 상태 변경
    $('#discountList .list.discount').on('click', '.item.check input[type="checkbox"]', function() {
        $('.list.discount .item.check input[type="checkbox"]').not(this).prop('checked', false);
    });

    $('.list_head .item.check input[type="checkbox"]').on('click', function () {
        let isChecked = $(this).prop('checked').attr('id').split('checkbox')[1];
        $('.list .item.check input[type="checkbox"]').prop('checked', isChecked);
    });

    $('.list.discount').on('change', '.item.check input[type="checkbox"]', function () {
        updateSelectAllCheckbox();
    });

});
function updateNoDataMessage() {
    const productList = $('.list.discount.product');
    const listItems = productList.find('.list_item');
    const noDataMessage = productList.find('#noDataMessage');

    const itemCount = listItems.filter(':not(#noDataMessage)').length;

    if (itemCount > 0) {
        noDataMessage.hide();
    } else {
        noDataMessage.show();
    }
}

// 선택된 체크박스 상태에 따라 전체 선택 체크박스 업데이트
function updateSelectAllCheckbox() {
    let discountCheckboxes = $('.list.discount .item.check input[type="checkbox"]');
    let totalCheckboxes = discountCheckboxes.length;
    let checkedCheckboxes = discountCheckboxes.filter(':checked').length;

    $('.list_head .item.check input[type="checkbox"]').prop('checked', totalCheckboxes === checkedCheckboxes);
}

let popup = null;
let selectedProductIds = [];


// 팝업 열기 버튼
function popupButton() {
    $('#popupBtn').click(function () {
        $(this).prop('disabled', true);

        getProducts()
            .then(([data, selectedProductIds]) => {
                openPopup(data, selectedProductIds);
            })
            .catch(error => {
                Swal.fire({
                    text: "상품을 불러오는 데 실패했습니다.",
                    showConfirmButton: true,
                    confirmButtonText: '확인',
                    customClass: mySwal,
                    buttonsStyling: false
                });
            })
            .finally(() => {
                $('#popupBtn').prop('disabled', false);
            });
    });
}

let csrfHeader = $("meta[name='_csrf_header']").attr("content");
let csrfToken = $("meta[name='_csrf']").attr("content");

// 선택 상품 불러오기
function getProducts() {
    selectedProductIds = $('.list.product .list_item')
        .map(function () {
            return $(this).data('id'); // 상품 ID 가져오기
        })
        .get();

    return new Promise((resolve, reject) => {
        $.ajax({
            type: 'GET',
            url: '/ajax/admin/products/search',
            data: {
                'selectedProductIds': selectedProductIds
            },  // 서버에 선택된 상품 아이디들 전송
            success: function (data) {
                resolve([data, selectedProductIds]);  // 요청 성공 시 받은 데이터를 resolve로 반환
            },
            error: function () {
                reject("상품을 불러오는 데 실패했습니다.");  // 실패 시 reject로 에러 메시지 반환
            }
        });
    });
}

// 팝업 창 열기
function openPopup(data, selectedProductIds) {
    if (popup && !popup.closed) {
        popup.focus();
        return;
    }
    const width = (screen.width) / 2;
    const height = screen.height;
    const left = (screen.width - width) / 2;
    const top = (screen.height - height) / 2;
    popup = window.open("/admin/products/search", "_blank", `width=${width},height=${height},left=${left},top=${top},resizable=yes,scrollbars=yes`);

    // 팝업 창이 완전히 로드된 후에 renderProducts를 호출하여 데이터를 전달
    popup.onload = function () {
        popup.renderProducts(data, selectedProductIds); // 데이터를 전달하여 팝업에서 제품을 렌더링
        popup.renderPagination(data.totalElements, data.pageable.pageNumber + 1, data.pageable.pageSize); // 페이지네이션 호출
    }
}

// 팝업 창에서 선택 상품 부모 창에 추가
function addItems() {
    $('.add_btn').on('click', function () {
        let selectedItem = $('.search_result .list.product .list_item').filter(':has(input:checked)');
        let selectedItemList = [];

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

        selectedItem.each(function () {
            let productId = $(this).data('id');
            let productName = $(this).find('.item.name').text();
            let productPrice = $(this).find('.item.price').text();
            let productImage = $(this).find('.item.img img').attr('src');

            selectedItemList.push({
                productId: productId,
                productName: productName,
                productPrice: productPrice,
                productImage: productImage,
            });

            let newItemHtml = `
                <div class="list_item" data-id="${productId}">
                    <div class="item check">
                        <input type="checkbox">
                    </div>
                    <div class="item img"><img src="${productImage}" alt="상품 이미지"></div>
                    <div class="item name">${productName}</div>
                    <div class="item price">${productPrice}</div>
                </div>
            `;

            if (window.opener) {
                $(window.opener.document).find('.input_box_wrap.product .list_content .list.product').append(newItemHtml);
            }
        });

        window.close();
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
                <div class="item name">${item.productName}</div>
                <div class="item price">${item.productPrice}</div>
            </div>
        `;
        listContainer.append(newItemHtml);
    });
}

// 선택 상품 삭제
$('.input_box_wrap.product #deleteBtn').click(function () {
    let listItems = $('.list.product .list_item:not(#noDataMessage)');
    let checkedItems = $('input[type="checkbox"]:checked'); // 선택된 체크박스

    if (listItems.length === 0) { // 상품이 없는 경우
        Swal.fire({
            text: "삭제할 상품이 없습니다.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }
    if (checkedItems.length === 0) {
        Swal.fire({
            text: "삭제할 상품을 선택해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }

    Swal.fire({
        text: '할인을 적용하지 않으시겠습니까?',
        showCancelButton: true,
        cancelButtonText: '아니요',
        confirmButtonText: '예',
        customClass: mySwalConfirm,
        reverseButtons: true,
        buttonsStyling: false,
    }).then((result) => {
        if (result.isConfirmed) {
            // 선택된 상품 삭제
            checkedItems.each(function () {
                $(this).closest('.list.product .list_item').remove();
            });
            $('input[type="checkBox"]').prop('checked', false);
        }
    });
});

let originalName = $('#editDiscountForm #name').val().trim();

async function isNameValid() {
    let name = $('#name').val().trim();
    let nameRegex = /^[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9\s]+$/;
    console.log("name:", name); // 추가

    if (name === '') {
        return false;
    }
    if (!nameRegex.test(name)) {
        return false;
    }

    console.log("originalName:", originalName);

    if (name === originalName) {
        return true;  // 원래 이름과 같으면 중복 아님
    }

    try {
        const result = await $.ajax({
            type: 'POST',
            url: '/ajax/admin/discount/check/name',
            data: {'name': name},
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken);
            }
        });

        if (result.code === 200) {
            return true;  // 중복되지 않음
        } else {
            return false;  // 중복된 할인명
        }
    } catch (error) {
        Swal.fire({
            html: "할인명 중복 확인 중 오류가 발생했습니다.<br>다시 시도해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;  // 오류 발생
    }
}

// 폼 제출 전 유효성 검사
async function validateBeforeSubmit() {
    let discountName = $('#name').val().trim();
    let nameRegex = /^[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9\s]+$/;
    let discountPercent = $('#discountPercent').val().trim();
    let startDate = $('#startDate').val();
    let startHour = $('#startHour').val();
    let startMin = $('#startMin').val();
    let expireDate = $('#expireDate').val();
    let expireHour = $('#expireHour').val();
    let expireMin = $('#expireMin').val();

    selectedProductIds = $('.list.product .list_item')
        .map(function () {
            return $(this).data('id'); // 상품 ID 가져오기
        })
        .get();

    $('input[name="productIds"]').val(selectedProductIds);

    if (discountName === '') {
        Swal.fire({
            text: "할인명을 입력해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    } else if (!nameRegex.test(discountName)) {
        Swal.fire({
            text: "할인명은 한글, 영문, 숫자, 공백만 허용됩니다.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }

    // 이름 중복 확인
    let isValidName = await isNameValid(); // 비동기 중복 검사
    if (!isValidName) {
        Swal.fire({
            text: "이미 존재하는 할인명입니다.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }

    if (startDate === '') {
        Swal.fire({
            text: "시작일을 지정해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }
    if (startHour === '' || startMin === '') {
        Swal.fire({
            text: "시작시간을 지정해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }
    if (expireDate === '') {
        Swal.fire({
            text: "종료일을 지정해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }
    if (expireHour === '' || expireMin === '') {
        Swal.fire({
            text: "종료시간을 지정해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }

    let startDateParts = startDate.split('-');  // [yyyy, mm, dd]
    let startDateTime = new Date(startDateParts[0], startDateParts[1] - 1, startDateParts[2], startHour, startMin);

    let expireDateParts = expireDate.split('-');  // [yyyy, mm, dd]
    let expireDateTime = new Date(expireDateParts[0], expireDateParts[1] - 1, expireDateParts[2], expireHour, expireMin);

    if (startDateTime >= expireDateTime) {
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

    return true;
}