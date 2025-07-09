$(document).ready(function() {

    let productId = $('#product').data('id');

    loadReviews(productId, 1);

    $(document).on("click", ".review_items", function () {
        let reviewItems = $(this);
        let currentContent = reviewItems.next(".review_content");

        if (currentContent.length > 0) {
            toggleContent(currentContent);
        }
    });

    $(document).on('click', '.page', function () {
        let currentPage = parseInt($(this).data('page')); // 숫자 타입으로 변환
        $('.page').removeClass('active');  // 기존의 .active 클래스 제거
        $(this).addClass('active');  // 클릭된 페이지에 .active 클래스 추가
        loadReviews(productId, currentPage);
    });

    $(document).on('click', '.prev_first, .next_last', function () {
        let currentPage = $(this).data('page');
        loadReviews(productId, currentPage);
    });

});

function loadReviews(productId, currentPage) {
    $.ajax({
        type: 'GET',
        url: '/ajax/reviews/' + productId + '?page=' + currentPage,
        success: function (data) {
            renderPagination(data.totalElements, currentPage);
            let totalCount = $('.totalCount');
            totalCount.text(data.totalElements);
            let reviewTable = $('#reviewTable');
            let tbody = reviewTable.find('tbody');

            if (data && data.content.length > 0) {
                tbody.empty();
                data.content.forEach(function (item) {
                    let reviewTitle = item.title;
                    let reviewImages = '';

                    if (item.imageUrls && item.imageUrls.length > 0) {
                        reviewImages = `<div class="review_img">` +
                            item.imageUrls.map((url, index) =>
                                `<img src="${url}" alt="첨부 이미지${index + 1}" id="image${index + 1}">`
                            ).join('') +
                            `</div>`;
                    }

                    let reviewItem = `
                        <tr class="review_items">
                            <td class="title_td">
                                 <span class="title">${reviewTitle}</span>
                            </td>
                            <td><span class="views">${item.views || 0}</span></td>
                            <td><span class="date">${item.createdAt}</span></td>
                        </tr>
                        <tr class="review_content">
                            <td colspan="3">
                                <div class="review_cont" id="content">
                                    <a href="../shop/product_details.html">
                                        <div id="orderItem" class="review_item">
                                            <div class="thumbnail">
                                                <img id="orderItemImg" class="item_img" src="${item.productImageUrl}" alt="상품 이미지">
                                            </div>
                                            <div class="description">
                                                <span id="orderItemName" class="item_name">${item.productName}</span>
                                                <span id="orderItemPrice" class="item_price">${item.productPrice}원</span>
                                            </div>
                                        </div>
                                    </a>
                                    <div class="review_cont_head">
                                        <div class="review_text">${item.content}</div>
                                        <div class="menu">
                                            <button class="menu_toggle">
                                                <img src="../../../images/more_horiz.svg">
                                            </button>
                                        </div>
                                    </div>
                                    ${reviewImages}
                                </div>
                            </td>
                        </tr>`;

                    tbody.append(reviewItem);
                });
            } else {
                tbody.empty();
                let noDataMessage = `
                    <tr id="noDataMessage">
                        <td colspan="3">등록된 상품후기가 없습니다.</td>
                    </tr>
                `;
                tbody.append(noDataMessage);
            }
        },
        error: function () {
            Swal.fire({
                text: "상품 후기를 불러오는 데 실패했습니다.",
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
        }
    });
}

// 페이지네이션 렌더링
function renderPagination(totalElements, currentPage) {
    let paginationSize = 5; // 한 페이지네이션에 보여줄 페이지 번호 수
    const totalPages = Math.ceil(totalElements / 10);  // 총 페이지 수

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
            <img src="../../images/keyboard_arrow_left.svg" alt="이전 페이지">
        </a></li>
       
    `);
    } else {
        pagination.append(`
        <li><a class="prev_first disabled"><img src="../../images/keyboard_arrow_left.svg" alt="이전 페이지"></a></li>
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
            <img src="../../images/keyboard_arrow_right.svg" alt="다음 페이지">
        </a></li>
    `);
    } else {
        pagination.append(`
        <li><a class="next_last disabled"><img src="../../images/keyboard_arrow_right.svg" alt="다음 페이지"></a></li>
    `);
    }

}

// 상품후기 클릭 시 내용 보이게
function toggleContent(content) {
    if (content.is(":visible")) {
        content.find("#reviewModal").hide();
        content.children().css({
            visibility: "visible",
            position: "relative"
        });

        content.stop(true,true).slideUp("fast", function () {
            content.css("border-bottom", "");
            content.prev(".review_items").css("border-bottom", "");
        });

    } else {
        $(".review_content").not(content).stop(true,true).slideUp("fast").promise().done(function () {
            $(this).css("border-bottom", "").prev(".review_items").css("border-bottom", "");

            // 기존 review_content 내부 내용을 다시 보이게 설정
            $(this).children().css({
                visibility: "visible",
                position: "relative"
            });
        });

        content.stop(true,true).slideDown("fast", function () {
            content.css("border-bottom", "1px solid #333");
            content.prev(".review_items").css("border-bottom", "1px solid #333");
        });
    }
}