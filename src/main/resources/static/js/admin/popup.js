$(document).ready(function () {
    addItems();

    // 전체 선택
    $('.select_all_btn').on('click', function () {
        selectAllCheckboxes();
    });

    // 선택 해제
    $('.deselect_all_btn').on('click', function () {
        deselectAllCheckboxes();
    });

    // 체크박스 상태 변경
    $('.list.product').on('change', '.item.check input[type="checkbox"]', function () {
        updateSelectAllCheckbox();
    });

    $('.list_head.product .item.check input[type="checkbox"]').on('click', function () {
        let isChecked = $(this).prop('checked');
        $('.list.product .item.check input[type="checkbox"]').prop('checked', isChecked);
        updateSelectAllCheckbox();
    });

    // 페이지당 상품 수 변경 시 페이지 로드 기능
    $('#limit').on('change', function() {
        let limit = $('#limit').val();
        loadPage(1, limit);
    });

    // 할인 팝업 제출 시 체크박스 초기화
    $('#discountPopup .submit_btn').on('click', function() {
        $('.search_result .product .check input[type="checkbox"]').prop('checked', false);
        let limit = $('#limit').val();
        loadPage(1, limit);
    });

    $(document).on('click', '.list_item a', function(event) {
        event.preventDefault(); // 기본 동작 방지
        let checkbox = $(this).closest('.list_item').find('input[type=checkbox]');
        checkbox.prop('checked', !checkbox.prop('checked'));
    });

    // 페이지네이션 클릭 시 페이지 로드
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

// 팝업에서 상품 추가
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
                    <a>
                    <div class="item img"><img src="${productImage}" alt="상품 이미지"></div>
                    <div class="item name">${productName}</div>
                    <div class="item price">${productPrice}</div>
                    </a>
                </div>
            `;

            if (window.opener) {
                $(window.opener.document).find('.input_box_wrap.product .list_content .list.product').append(newItemHtml);
            }
        });

        window.close();
    });
}

// 상품 목록 렌더링
function renderProducts(data, selectedProductIds) {
    let productList = $('.search_result .list.product');
    productList.empty();    // 기존 목록을 비움

    $('.totalProducts').text(data.totalElements);
    $('#productIds').val(selectedProductIds);

    data.content.forEach(function (item) {
        let newItemHtml = `
                <div class="list_item" data-id="${item.id}">
                    <div class="item check">
                        <input type="checkbox" name="checkBox">
                    </div>
                    <a>
                    <div class="item img"><img src="${item.displayImageData}" alt="상품 이미지"></div>
                    <div class="item name">${item.name}</div>
                    <div class="item price">${item.price}</div>
                    </a>
                </div>
            `;
        productList.append(newItemHtml);
    });
}

// 페이지네이션 렌더링
function renderPagination(totalElements, currentPage, limit) {
    let paginationSize = 5; // 한 페이지네이션에 보여줄 페이지 번호 수
    const totalPages = Math.ceil(totalElements / limit);  // 총 페이지 수

    if (totalPages === 0) {
        $('#pagination').empty();
        return;
    }

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

// 선택된 체크박스 상태에 따라 전체 선택 체크박스 업데이트
function updateSelectAllCheckbox() {
    let productCheckboxes = $('.list.product .item.check input[type="checkbox"]');
    let totalCheckboxes = productCheckboxes.length;
    let checkedCheckboxes = productCheckboxes.filter(':checked').length;

    $('.list_head.product .item.check input[type="checkbox"]').prop('checked', totalCheckboxes === checkedCheckboxes);
}

// 페이지 로드
function loadPage(currentPage, limit) {
    return new Promise((resolve, reject) => {
        let keyword = $('#keyword').val();
        let category = $('#category').val();
        let selectedProductIds = $('#productIds').val();

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
                renderProducts(data, selectedProductIds);
                renderPagination(data.totalElements, currentPage, limit);
                resolve(data); // 성공 시 resolve 호출
            },
            error: function () {
                reject("상품을 불러오는 데 실패했습니다."); // 실패 시 reject 호출
            }
        });
    });
}

// 부모창에서 사용할 함수 등록
window.renderProducts = renderProducts;
window.renderPagination = renderPagination;