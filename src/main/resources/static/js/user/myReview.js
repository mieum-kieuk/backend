$(document).ready(function () {

    let productId = $('#product').data('id');
    popupButton();

    $('#reviewBtn').on('click', function () {
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
    })

$(document).on("click", ".review_items", function () {
    let reviewItems = $(this);
    let currentContent = reviewItems.next(".review_content");

    if (currentContent.length > 0) {
        toggleContent(currentContent);
    }
});
$('.star_rating input').change(function () {
    let value = $(this).val();
    $('.star_rating .star').addClass('filled_star');
    $(this).prevAll('.star').removeClass('filled_star');
});

$('input[type="file"]').on('click', function (event) {
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
}).on('change', function () {
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

$('.image_input').on('change', function () {
    let input = $(this);
    let previewContainer = $(this).closest('#reviewModal').find("#previewContainer");

    let files = input[0].files;
    if (files.length) {
        addImagePreview(previewContainer, input, files[0]);
    }
});


// 수정 버튼
$(document).on("click", ".edit_btn", function () {
    resetReviewModal();
    editReviewModal(this);
});

$('#reviewModal .close_btn').on('click', function () {
    closeReviewModal();
});

// 삭제 버튼
$(document).on("click", ".dropdown_menu .delete_btn", function () {
    let reviewItem = $(this).closest(".review_content").prev(".review_items");
    let reviewContent = reviewItem.next(".review_content");
    let reviewId = $(this).data("id");

    deleteProductReview(reviewId, reviewItem, reviewContent);
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
    $('.review_table tbody tr:first-child').css({
        'border-top': 'none'
    });
});

});

let popup = null;

// 팝업 열기 버튼
function popupButton() {
    $('#popupBtn').click(function () {
        $(this).prop('disabled', true);

        getProducts()
            .then((data) => {
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
// 상품 불러오기
function getProducts() {
    return new Promise((resolve, reject) => {
        $.ajax({
            type: 'GET',
            url: '/ajax/products/search',
            success: function (data) {
                resolve(data);  // 요청 성공 시 받은 데이터를 resolve로 반환
            },
            error: function () {
                reject("상품을 불러오는 데 실패했습니다.");  // 실패 시 reject로 에러 메시지 반환
            }
        });
    });
}

// 팝업 창 열기
function openPopup(data) {
    if (popup && !popup.closed) {
        popup.focus();
        return;
    }
    const width = (screen.width) / 2;
    const height = screen.height;
    const left = (screen.width - width) / 2;
    const top = (screen.height - height) / 2;
    popup = window.open("/products/search", "_blank", `width=${width},height=${height},left=${left},top=${top},resizable=yes,scrollbars=yes`);

    // 팝업 창이 완전히 로드된 후에 renderProducts를 호출하여 데이터를 전달
    popup.onload = function () {
        popup.renderProducts(data); // 데이터를 전달하여 팝업에서 제품을 렌더링
        popup.renderPagination(data.totalElements, data.pageable.pageNumber + 1, data.pageable.pageSize); // 페이지네이션 호출
    }
}

// 팝업 창에서 선택한 상품 부모 창에 추가
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
                                 <span class="title">${reviewTitle}</span>
                            </td>
                            <td><span class="views">${item.views || 0}</span></td>
                            <td><span class="date">${item.createdAt}</span></td>
                        </tr>
                        ${!item.isWriter ? '' :
                        `<tr class="review_content">
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
                                    ${reviewImages}
                                </div>
                            </td>
                        </tr>`}`;

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

// 상품후기 폼 초기화
function resetReviewModal() {
    let reviewModal = $('#reviewModal');
    reviewModal.removeData('id');
    reviewModal.find('#title').val('');
    reviewModal.find('#content').val('');
    reviewModal.find('input[name="rating"]').prop('checked', false);
    reviewModal.find('.preview_container').empty();
    reviewModal.find('input[type="file"]').val('');
    $('#productSelect').show();
    reviewModal.find(".submit_btn").text("등록");
    reviewModal.hide();
}

// 상품후기 폼 열기
function openReviewModal() {
    resetReviewModal();
    let reviewModal = $("#reviewModal");
    reviewModal.removeData('id');

    reviewModal.css("display", "flex");
    $('#reviewModal .submit_btn').prop('disabled', false);
    $("#reviewModal .submit_btn").text("등록");

    $('#productSelect').show();

}

function closeReviewModal() {
    $('#reviewModal').hide();
    $('#reviewModal').removeData('id');
    $('#reviewModal #title').val('');
    $('#reviewModal #content').val('');
    $('#reviewModal #secret').prop('checked', false);
    $("#reviewModal .submit_btn").text("등록");

    // review_content 원래 상태로 복원
    let reviewContent = $('#reviewModal').closest('.review_content');
    reviewContent.find('.review_cont').css({
        visibility: "visible",
        position: "relative"
    });

    $('.review_table tbody tr:first-child').css({
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
    $('#productSelect').hide();

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

        content.stop(true, true).slideUp("fast", function () {
            content.css("border-bottom", "");
            content.prev(".review_items").css("border-bottom", "");
        });

    } else {
        $(".review_content").not(content).stop(true, true).slideUp("fast").promise().done(function () {
            $(this).css("border-bottom", "").prev(".review_items").css("border-bottom", "");

            // 기존 review_content 내부 내용을 다시 보이게 설정
            $(this).children().css({
                visibility: "visible",
                position: "relative"
            });
        });

        content.stop(true, true).slideDown("fast", function () {
            content.css("border-bottom", "1px solid #333");
            content.prev(".review_items").css("border-bottom", "1px solid #333");
        });
    }
}

// 상품후기 첨부파일 미리보기 생성
function addImagePreview(previewContainer, input, file) {
    let reader = new FileReader();

    reader.onload = function (e) {
        // 미리보기 이미지 컨테이너 생성
        let containerDiv = $('<div>').addClass('preview_image_container');
        let previewImage = $('<img>').addClass('preview_image').attr('src', e.target.result);
        let deleteButton = $('<button>').addClass('delete_btn').append($('<span>').addClass('material-symbols-outlined').text('close'));

        // 삭제 버튼 클릭 시 미리보기 이미지 삭제 및 input 값 비우기
        deleteButton.click(function () {
            containerDiv.remove();
            input.val(''); // input 값 비우기
        });

        // 미리보기 이미지와 삭제 버튼을 컨테이너에 추가
        containerDiv.append(previewImage, $('<div>').addClass('btn_wrap').append(deleteButton));

        // 컨테이너에 추가
        previewContainer.append(containerDiv);
    };

    reader.readAsDataURL(file);
}

// 상세페이지 후기 삭제
function deleteProductReview(reviewId, reviewItem, reviewContent) {
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
                url: '/ajax/inquiries',
                async: false,
                data: {'reviewId': reviewId},
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(csrfHeader, csrfToken)
                },
                success: function (data) {
                    if (data.code === 200) {
                        reviewItem.remove(); // 질문 행 삭제
                        reviewContent.remove(); // 내용 행 삭제
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

// 상세페이지 후기 제출 전 유효성 검사
function validateBeforeReviewModalSubmit() {
    let title = $('#reviewModal #title').val().trim();
    let content = $('#reviewModal #content').val().trim();
    let filledStars = $('.star_rating .star.filled_star').length;

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
            text: "내용을 작성해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }

    $('#reviewModal .submit_btn').prop('disabled', true);
    return true;
}