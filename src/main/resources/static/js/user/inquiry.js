$(document).ready(function () {

    popupButton();
    addItem();

    if ($('.qna_list #noDataMessage').length > 0) {
        $('.footer').addClass('fixed');
    }

    // 상품후기, 상품문의
    let inquiryModal = $("#inquiryModal");
    let closeBtn = $(".close");
    let cancelBtn = $(".cancel_btn");

    // 모달 닫기
    closeBtn.click(function() {
        inquiryModal.hide();
    });
    cancelBtn.click(function() {
        inquiryModal.hide();
    });
    // 외부 클릭 시 모달 닫기
    $(window).click(function(event) {
        if (event.target.id === "inquiryModal") {
            inquiryModal.hide();
        }
    });

    $(".qna_items").click(function() {
        let currentContent = $(this).next(".qna_content");
        toggleContent(currentContent);
    });

    $(" .qna_table .edit_btn").click(function() {
        let questionText = $(this).closest(".qna_question").find(".qna_text").text();
        let answerText = $(this).closest(".qna_content").find(".qna_answer").text();

        $("#edit_title").val(questionText.trim());
        $("#edit_content").val(answerText.trim());

        $("#inquiryModal .submit_btn").text("완료");
        showInquiryModal();
    });


});

let popup = null;
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

function toggleContent(content) {
    if (content.is(":visible")) {
        content.slideUp("fast", function() {
            content.css("border-bottom", "");
            content.prev(".qna_items").css("border-bottom", "");
        });
    } else {
        $(".qna_content").not(content).slideUp("fast").promise().done(function() {
            $(this).css("border-bottom", "").prev(".qna_items").css("border-bottom", "");
            content.slideDown().css("border-bottom", "1px solid #333");
            content.prev(".qna_items").css("border-bottom", "1px solid #333");
        });
    }
}

let csrfHeader = $("meta[name='_csrf_header']").attr("content");
let csrfToken = $("meta[name='_csrf']").attr("content");

function showInquiryModal() {
    let inquiryModal = $("#inquiryModal");
    inquiryModal.css("display", "flex");

}

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
    return true;
}


