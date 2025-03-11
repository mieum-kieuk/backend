$(document).ready(function () {
    addItem();

    // 체크박스 상태 변경
    $('.list.product').on('click', '.item.check input[type="checkbox"]', function() {
        $('.list.product .item.check input[type="checkbox"]').not(this).prop('checked', false);
    });

    // 페이지당 상품 수 변경 시 페이지 로드 기능
    $('#limit').on('change', function() {
        let limit = $('#limit').val();
        loadPage(1, limit);
    });

    // 문의 팝업 제출 시 체크박스 초기화
    $('#inquiryPopup .submit_btn').on('click', function() {
        $('.search_result .product .check input[type="checkbox"]').prop('checked', false);
        let limit = $('#limit').val();
        loadPage(1, limit);
    });

    $(document).on('click', '.list_item a', function(event) {
        event.preventDefault(); // 기본 동작 방지
        let checkbox = $(this).closest('.list_item').find('input[type=checkbox]');
        $('.list_item input[type=checkbox]').prop('checked', false);
        checkbox.prop('checked', true);
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
function addItem() {
    $('.add_btn').on('click', function () {
        let selectedItem = $('.search_result .list.product .list_item').filter(':has(input:checked)');

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

        let productId = selectedItem.data('id');
        let productName = selectedItem.find('.item.name').text();
        let productPrice = selectedItem.find('.item.price').text();
        let productImage = selectedItem.find('.item.img img').attr('src');


        if (window.opener) {
            let addInquiry = window.opener.document;
            $(addInquiry).find('#productId').val(productId);
            $(addInquiry).find('#productName').text(productName);
            $(addInquiry).find('#productPrice').text(productPrice);
            $(addInquiry).find('#productImage').attr('src', productImage);

            $(addInquiry).find('.popup_btn.info').css('display', 'flex');
            $(addInquiry).find('.popup_btn.info').attr('href', '/products/' + productId);
        }


        window.close();
    });
}

// 상품 목록 렌더링
function renderProducts(data) {
    let productList = $('.search_result .list.product');
    productList.empty();

    $('.totalProducts').text(data.totalElements);

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

// 페이지 로드
function loadPage(currentPage, limit) {

    return new Promise((resolve, reject) => {
        let keyword = $('#keyword').val();
        let category = $('#category').val();

        $.ajax({
            type: 'GET',
            url: '/ajax/products/search',
            data: {
                'keyword': keyword,
                'category': category,
                'page': currentPage,
                'limit': limit
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

// 부모창에서 사용할 함수 등록
window.renderProducts = renderProducts;
window.renderPagination = renderPagination;

