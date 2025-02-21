$(document).ready(function () {
    popupButton();
    addItems();

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
    // 전체 선택
    $('.select_all_btn').on('click', function () {
        selectAllCheckboxes();
    });

    // 선택 해제
    $('.deselect_all_btn').on('click', function () {
        deselectAllCheckboxes();
    });

    $('.list.product').on('change', '.item.check input[type="checkbox"]', function () {
        updateSelectAllCheckbox();
    });

    // 체크박스 상태 변경
    $('.list_head.product .item.check input[type="checkbox"]').on('click', function () {
        let isChecked = $(this).prop('checked');
        $('.list.product .item.check input[type="checkbox"]').prop('checked', isChecked);
    });

    $('#limit').on('change', function() {
        let limit = $('#limit').val();
        loadPage(1, limit);
    });

    $('#discountPopup .submit_btn').on('click', function() {
        let limit = $('#limit').val();
        loadPage(1, limit);
    });

    $(document).on('click', '.page', function () {
        let currentPage = $(this).data('page');
        let limit = $('#limit').val();
        $('.page').removeClass('active');  // 기존의 .active 클래스 제거
        $(this).addClass('active');  // 클릭된 페이지에 .active 클래스 추가

        loadPage(currentPage, limit);
    });
    $(document).on('click', '.prev_first, .next_last', function () {
        let currentPage = $(this).data('page');
        let limit = $('#limit').val();

        loadPage(currentPage, limit);
    });
});

let popup = null;
let selectedProductIds = [];

function popupButton() {
    $('#popupBtn').click(function () {
        $(this).prop('disabled', true);

        getProducts()
            .then(data => {
                openPopup(data);
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
                'selectedProductIds': selectedProductIds},  // 서버에 선택된 상품 아이디들 전송
            success: function (data) {
                resolve(data);  // 요청 성공 시 받은 데이터를 resolve로 반환
            },
            error: function () {
                reject("상품을 불러오는 데 실패했습니다.");  // 실패 시 reject로 에러 메시지 반환
            }
        });
    });
}

function openPopup(data) {
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
        popup.renderProducts(data); // 데이터를 전달하여 팝업에서 제품을 렌더링
        popup.renderPagination(data.totalElements, data.pageable.pageNumber + 1, data.pageable.pageSize); // 페이지네이션 호출
    }
}

function renderProducts(data) {
    let productList = $('.search_result .list.product');
    productList.empty();    // 기존 목록을 비움

    $('.totalProducts').text(data.totalElements);

    data.content.forEach(function (item) {
        let newItemHtml = `
                <div class="list_item" data-id="${item.id}">
                    <div class="item check">
                        <input type="checkbox" name="checkBox">
                    </div>
                    <div class="item img"><img src="${item.displayImageUrl}" alt="상품 이미지"></div>
                    <div class="item name">${item.name}</div>
                    <div class="item price">${item.price}</div>
                </div>
            `;
        productList.append(newItemHtml);
    });
}

function renderPagination(totalElements, currentPage, limit) {
    // window.pageSize = limit;  // 한 페이지당 상품 수
    let paginationSize = 5; // 한 페이지네이션에 보여줄 페이지 번호 수
    const totalPages = Math.ceil(totalElements / limit);  // 총 페이지 수

    let pagination = $('#pagination');
    pagination.empty(); // 기존 페이지네이션 초기화

    let totalGroups = Math.ceil(totalPages / paginationSize); // 페이지 그룹 수

    // 현재 페이지가 속한 그룹 계산
    let currentGroup = Math.floor((currentPage - 1) / paginationSize) + 1;

    // 이전 페이지 버튼
    if (currentGroup > 1) {
        let prevPage = (currentGroup - 1) * paginationSize;
        pagination.append(`
        <li><a class="prev_first" data-page="${prevPage}">
            <span class="material-symbols-outlined">navigate_before</span>
        </a></li>
    `);
    } else {
        pagination.append(`
        <li><a class="prev_first disabled"><span class="material-symbols-outlined">navigate_before</span></a></li>
    `);
    }

    let startPage = (currentGroup - 1) * paginationSize + 1;  // 그룹 내 첫 번째 페이지
    let endPage = Math.min(currentGroup * paginationSize, totalPages);  // 그룹 내 마지막 페이지

    for (let i = startPage; i <= endPage; i++) {
        let isActive = (currentPage === i) ? 'active' : '';
        pagination.append(`
            <li><a class="page ${isActive}" data-page="${i}">${i}</a></li>
        `);
    }

    // 다음 페이지 버튼
    if (currentGroup < totalGroups) {
        let nextPage = (currentGroup * paginationSize) + 1;
        pagination.append(`
        <li><a class="next_last" data-page="${nextPage}">
            <span class="material-symbols-outlined">navigate_next</span>
        </a></li>
    `);
    } else {
        pagination.append(`
        <li><a class="next_last disabled"><span class="material-symbols-outlined">navigate_next</span></a></li>
    `);
    }
}

// 전체 선택
function selectAllCheckboxes() {
    $('.list.product .item.check input[type="checkbox"]').prop('checked', true);
    $('.list_head.product .item.check input[type="checkbox"]').prop('checked', true);
}

// 선택 해제
function deselectAllCheckboxes() {
    $('.list.product .item.check input[type="checkbox"]').prop('checked', false);
    $('.list_head.product .item.check input[type="checkbox"]').prop('checked', false);
}

// 선택된 체크박스 상태에 따라 전체 선택 체크박스 업데이트 함수
function updateSelectAllCheckbox() {
    let totalCheckboxes = $('.list.product .item.check input[type="checkbox"]').length;
    let checkedCheckboxes = $('.list.product .item.check input[type="checkbox"]:checked').length;
    $('.list_head.product .item.check input[type="checkbox"]').prop('checked', totalCheckboxes === checkedCheckboxes);
}

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

$('.input_box_wrap.product #deleteBtn').click(function () {
    let checkbox = $('input[type="checkbox"]:checked');  // 선택된 체크박스들

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
            customClass: mySwalConfirm,
            reverseButtons: true,
            buttonsStyling: false,
        }).then((result) => {
            if (result.isConfirmed) {
                // 선택된 상품 삭제
                checkbox.each(function () {
                    $(this).closest('.list.product .list_item').remove();
                });
                $('input[type="checkBox"]').prop('checked', false);
            }
        });
    }
});

function validateBeforeSubmit() {
    let discountName = $('#name').val().trim();
    let discountPercent = $('#discountPercent').val().trim();
    let startDate = $('#startDate').val();
    let startHour = $('#startHour').val();
    let startMin = $('#startMin').val();
    let expireDate = $('#expireDate').val();
    let expireHour = $('#expireHour').val();
    let expireMin = $('#expireMin').val();

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
//상품 할인 단건 삭제
function deleteDiscount(discountId) {

    let csrfHeader = $("meta[name='_csrf_header']").attr("content");
    let csrfToken = $("meta[name='_csrf']").attr("content");

    Swal.fire({
        text: "삭제하시겠습니까?",
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
                url: '/ajax/admin/discount/delete',
                async: false,
                data: {'discountId': discountId},
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(csrfHeader, csrfToken)
                },
                success: function (data) {
                    if (data.code === 200) {
                        window.location.href = '/admin/discount';
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
            customClass: mySwalConfirm,
            reverseButtons: true,
            buttonsStyling: false,
        }).then((result) => {
            if (result.isConfirmed) {
                checkboxes.each(function () {
                    let discountId = checkboxes.id.split('checkbox')[1];
                    discountIds.push(discountId);
                });

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
                            window.location.href = '/admin/discount';
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

function loadPage(currentPage, limit) {
    return new Promise((resolve, reject) => {
        let keyword = $('#keyword').val();
        let category = $('#category').val();

        $.ajax({
            type: 'GET',
            url: '/ajax/admin/products/search',
            data: {
                'keyword': keyword,
                'category': category,
                'page': currentPage,
                'limit': limit,
                'selectedProductIds': selectedProductIds,
            },
            success: function (data) {
                renderProducts(data);
                renderPagination(data.totalElements, currentPage, limit);
                resolve(data); // 성공 시 resolve 호출
            },
            error: function () {
                reject("상품을 불러오는 데 실패했습니다."); // 실패 시 reject 호출
            }
        });
    });
}