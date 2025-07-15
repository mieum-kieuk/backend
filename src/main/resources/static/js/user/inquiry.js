$(document).ready(function () {

    let productId = $('#product').data('id');

    popupButton();

    $('.delete_btn').on('click', function () {
        let inquiryId = $(this).data('id');
        deleteInquiry(productId, inquiryId);
    });

    if ($('.inquiry_list #noDataMessage').length > 0) {
        $('.footer').addClass('fixed');
    }


    $('#writeBtn').on('click', function () {

        $.ajax({
            type: 'GET',
            url: '/ajax/login/status',
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken);
            },
            success: function (result) {
                location.href = '/community/inquiries/add';
            },
            error: function (xhr) {
                if (xhr.status == 401) {
                    Swal.fire({
                        text: '로그인이 필요한 기능입니다.',
                        showCancelButton: true,
                        cancelButtonText: '취소',
                        confirmButtonText: '로그인',
                        customClass: mySwalConfirm,
                        reverseButtons: true,
                        buttonsStyling: false,
                    }).then((result) => {
                        if (result.isConfirmed) {
                            location.href = '/login';
                        }
                    });
                }
            }
        })
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
function deleteInquiry(productId, inquiryId) {

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
                data: {'inquiryId': inquiryId},
                async: false,
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(csrfHeader, csrfToken)
                },
                success: function (data) {
                    window.location.href = '/community/inquiries';
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
