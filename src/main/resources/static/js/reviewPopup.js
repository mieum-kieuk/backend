$(document).ready(function() {

    let productId = $('#product').data('id');

    loadReviews(productId, 1);

    $(document).on("click", ".review_items", function () {
        let reviewItems = $(this);
        let currentContent = reviewItems.next(".review_content");
        let isWriter = reviewItems.hasClass('isWriter');

        if (currentContent.length > 0) {
            toggleContent(currentContent);
        }
    });
    $('.star_rating input').change(function() {
        let value = $(this).val();
        $('.star_rating .star').addClass('filled_star');
        $(this).prevAll('.star').removeClass('filled_star');
    });

    $('input[type="file"]').on('click', function(event) {
        let currentInput = $(this);
        let currentIndex = parseInt(currentInput.attr('id').replace('image', ''));

        // 이전 파일들이 선택되었는지 확인
        for (let i = 1; i < currentIndex; i++) {
            let previousInput = $('#image' + i);
            if (previousInput.length > 0 && previousInput[0].files.length === 0) {
                Swal.fire({
                    text: "첨부 파일 " + i + "을(를) 먼저 선택해 주세요.",
                    showConfirmButton: true,
                    confirmButtonText: '확인',
                    customClass: mySwal,
                    buttonsStyling: false
                });
                event.preventDefault();
                return;
            }
        }
    }).on('change', function() {
        // 파일 크기 검사
        let maxSizePerFile = 3 * 1024 * 1024; // 최대 파일 크기 설정 (1MB)
        let fileInput = $(this)[0];
        if (fileInput.files.length > 0) {
            let fileSize = fileInput.files[0].size;
            if (fileSize > maxSizePerFile) {
                Swal.fire({
                    text: "첨부 파일 " + fileInput.id.replace('image', '') + "의 크기가 1MB 이하여야 합니다.",
                    showConfirmButton: true,
                    confirmButtonText: '확인',
                    customClass: mySwal,
                    buttonsStyling: false
                });

                $(this).val(''); // 파일 선택 취소
            }
        }
    });

    // 수정 버튼
    $(document).on("click", "#reviewTable .edit_btn", function () {
        editReviewModal(this);
    });

    $('#reviewModal .close_btn').on('click', function () {
        closeReviewModal();
    });

    // 삭제 버튼
    $(document).on("click", ".delete_btn", function () {
        let reviewItem = $(this).closest(".review_content").prev(".review_items");
        let reviewContent = reviewItem.next(".review_content");
        let reviewId = $(this).data("id");

        deleteProductReview(productId, reviewId, reviewItem, reviewContent);
    });

    // 모달 닫기 버튼
    $('.close_btn').on('click', function () {
        $('#reviewModal').hide();

        // 모달이 닫히면 review_content 원래 상태로 복원
        let reviewContent = $('#reviewModal').closest('.review_content');
        reviewContent.find('.review_cont').css({
            visibility: "visible",
            position: "relative"
        });

        // 첫 번째 tr의 border-top을 원래대로 되돌리기
        $('#product .review_wrap.list .review_table tbody tr:first-child').css({
            'border-top': 'none'
        });
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

    $('#reviewBtn').on('click', function () {
        $.ajax({
            url: '/ajax/check/login',
            type: 'GET',
            success: function (result) {
                if (result.status == 401) {
                    Swal.fire({
                        html: result.message,
                        showConfirmButton: true,
                        confirmButtonText: '확인',
                        customClass: mySwal,
                        buttonsStyling: false,
                        preConfirm: () => {
                            window.location.href = '/login';
                        }
                    });
                } else if (result.status == 200) {
                    closeReviewModal();
                    openReviewModal();
                    let reviewModal = $('#reviewModal');
                    let reviewHead = $('.review_head');
                    reviewModal.insertAfter(reviewHead).css({
                        position: 'relative',
                    });

                    // 첫 번째 tr에 border-top 스타일 변경
                    $('#product .review_wrap.list .review_table tbody tr:first-child').css({
                        'border-top': '1px solid #d7d7d7'
                    });

                }
            },
            error: function () {
                Swal.fire({
                    text: "로그인 상태를 확인하는 중 오류가 발생했습니다.",
                    showConfirmButton: true,
                    confirmButtonText: '확인',
                    customClass: mySwal,
                    buttonsStyling: false
                });
            }
        });
    });

    $('#reviewModal .submit_btn').on('click', function () {
        let csrfToken = $("meta[name='_csrf']").attr("content");
        let csrfHeader = $("meta[name='_csrf_header']").attr("content");

        let title = $('#reviewModal #title').val().trim();
        let content = $('#reviewModal #content').val().trim();
        let reviewId = $('#reviewModal').data('id');

        if (validateBeforeReviewModalSubmit()) {
            let requestType = $(this).text() === '등록' ? 'POST' : 'PUT';
            let requestUrl = $(this).text() === '등록' ? '/ajax/reviews/add' : '/ajax/reviews/' + reviewId + '/edit';

            let requestData = {
                'productId': productId,
                'title': title,
                'content': content,
            };

            $.ajax({
                url: requestUrl,
                type: 'POST',
                data: requestData,
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(csrfHeader, csrfToken)
                },
                success: function () {
                    loadReviews(productId, 1);
                    closeReviewModal();
                },
                error: function (error) {
                    Swal.fire({
                        text: error.message,
                        showConfirmButton: true,
                        confirmButtonText: '확인',
                        customClass: mySwal,
                        buttonsStyling: false
                    });
                }
            });
        }
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
                        <tr class="review_items ${item.isWriter ? 'isWriter' : ''}">
                            <td class="title_td">
                                <div class="title">
                                    <span class="title">${reviewTitle}</span>
                                </div>
                            </td>
                            <td><span class="writer">${item.writerLoginId}</span></td>
                            <td><span class="date">${item.createdAt}</span></td>
                        </tr>
                        ${!item.isWriter ? '' :
                        `<tr class="review_content">
                            <td colspan="4">
                                <div class="review_cont" id="content">
                                    <div class="review_question">
                                        <div class="review_text">
                                            <div class="contents">
                                                <span class="content">${item.content}</span>
                                            </div>
                                            ${reviewImages}
                                        </div>
                                        <div class="menu">
                                            <button class="menu_toggle">
                                                <span class="material-symbols-outlined">more_horiz</span>
                                            </button>
                                            <div class="dropdown_menu">
                                                <ul>
                                                    <li>
                                                        <button type="button" class="edit_btn" data-id="${item.id}">수정</button>
                                                    </li>
                                                    <li>
                                                        <button type="button" class="delete_btn" data-id="${item.id}">삭제</button>
                                                    </li>
                                                </ul>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </td>
                        </tr>
                    `}`;
                    tbody.append(reviewItem);
                });

                // 수정 버튼 클릭 이벤트 바인딩
                // $('.edit_btn').off('click').on('click', function () {
                //     editReviewModal(this);
                // });

            } else {
                tbody.empty();
                let noDataMessage = `
                    <tr id="noDataMessage">
                        <td colspan="4">등록된 상품후기가 없습니다.</td>
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

// 상품후기 폼 열기
function openReviewModal() {
    let reviewModal = $("#reviewModal");
    reviewModal.removeData('id');

    reviewModal.css("display", "flex");
    $('#reviewModal .submit_btn').prop('disabled', false);
    $("#reviewModal .submit_btn").text("등록");
}

function closeReviewModal() {
    $('#reviewModal').hide();
    $('#reviewModal').removeData('id');
    $('#reviewModal #title').val('');
    $('#reviewModal #content').val('');
    $("#reviewModal .submit_btn").text("등록");

    // review_content 원래 상태로 복원
    let reviewContent = $('#reviewModal').closest('.review_content');
    reviewContent.find('.review_cont').css({
        visibility: "visible",
        position: "relative"
    });

    $('#product .review_wrap.list .review_table tbody tr:first-child').css({
        'border-top': 'none'
    });
}

// 상품후기 수정 폼
function editReviewModal(editButton) {
    let reviewModal = $('#reviewModal');
    let reviewId = $(editButton).data('id');
    let reviewTitle = $(editButton).closest('tr.review_content').prev('.review_items').find('.title_td .title').text().trim();
    let reviewContent = $(editButton).closest('.review_cont').find('.review_text').text().trim();
    let ratingStars = $(editButton).closest('tr.review_content').prev('.review_items').find('.star_rating .star').length;

    // 모달 세팅
    reviewModal.data('id', reviewId);
    reviewModal.find('#title').val(reviewTitle);
    reviewModal.find('#content').val(reviewContent);
    reviewModal.find(".submit_btn").text("완료");
    reviewModal.find(`input[name="rating"][value="${ratingStars}"]`).prop("checked", true);

    let reviewCont = $(editButton).closest('.review_cont').parent();

    reviewCont.append(reviewModal.detach());
    reviewCont.css("position", "relative");

    reviewCont.children().not(reviewModal).css({
        visibility: "hidden",
        position: "absolute"
    });

    reviewModal.css({
        top: 0,
        left: 0,
        width: '100%',
        display: 'flex',
        zIndex: 10
    }).show();

    reviewModal.find('.submit_btn').prop('disabled', false);
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

// 상세페이지 후기 삭제
function deleteProductReview(productId, reviewId, reviewItem, reviewContent) {
    let csrfToken = $("meta[name='_csrf']").attr("content");
    let csrfHeader = $("meta[name='_csrf_header']").attr("content");

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
                url: '/ajax/reviews',
                async: false,
                data: {'reviewId': reviewId},
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(csrfHeader, csrfToken)
                },
                success: function (data) {
                    if (data.status === 200) {
                        reviewItem.remove();
                        reviewContent.remove();
                        loadReviews(productId, 1);
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
function validateBeforeReviewModalSubmit() {
    let productElement = $('#productId');
    if (productElement.length > 0) {
        let product = productElement.val().trim();
        if (product === '') {
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
    let filledStars = $('.star_rating .star.filled_star').length; // 채워진 별의 개수를 가져옴

    if (filledStars === 0) {
        Swal.fire({
            text: "별점을 입력해주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });

        return false;
    }
    let title = $('#title').val().trim(); // 제목 입력값 가져오기
    let content = $('#content').val().trim(); // 리뷰 입력값 가져오기

    if (title === '') {
        Swal.fire({
            text: "제목을 작성해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }

    if (content === '') {
        Swal.fire({
            text: "리뷰를 작성해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });// 리뷰가 비어있을 경우 경고창 표시
        return false;
    }

    $('#reviewModal .submit_btn').prop('disabled', true);
    return true;
}