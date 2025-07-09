$(document).ready(function () {

    let productId = $('#product').data('id');

    popupButton();

    $('.delete_btn').on('click', function () {
        let reviewId = $(this).data('id');
        deleteReview(productId, reviewId);
    });

    if ($('.review_list #noDataMessage').length > 0) {
        $('.footer').addClass('fixed');
    }

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
        let maxSizePerFile = 3 * 1024 * 1024; // 최대 파일 크기 설정
        let fileInput = $(this)[0];
        if (fileInput.files.length > 0) {
            let fileSize = fileInput.files[0].size;
            if (fileSize > maxSizePerFile) {
                Swal.fire({
                    text: "첨부 파일 " + fileInput.id.replace('image', '') + "의 크기가 3MB 이하여야 합니다.",
                    showConfirmButton: true,
                    confirmButtonText: '확인',
                    customClass: mySwal,
                    buttonsStyling: false
                });

                $(this).val('');
            }
        }
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

let csrfHeader = $("meta[name='_csrf_header']").attr("content");
let csrfToken = $("meta[name='_csrf']").attr("content");

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

// 상품 문의 삭제
function deleteReview(productId, reviewId) {

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
                data: {'reviewId': reviewId},
                async: false,
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(csrfHeader, csrfToken)
                },
                success: function (data) {
                    window.location.href = '/community/reviews';
                },
                error: function () {
                    Swal.fire({
                        html: "삭제 중 오류가 발생했습니다.<br>다시 시도해 주세요.",
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

// 폼 제출 전 유효성 검사
function validateBeforeSubmit() {
    let selectedProduct = $('#productName').text().length;
    let filledStars = $('.star_rating .star.filled_star').length; // 채워진 별의 개수
    let title = $('#title').val().trim();
    let content = $('#content').val().trim();

    if (selectedProduct === 0) {
        Swal.fire({
            text: "상품을 선택해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }

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

    $('.submit_btn').prop('disabled', true);
    return true;
}
