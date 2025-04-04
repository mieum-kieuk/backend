$(document).ready(function () {

    let productId = $('#product').data('id');

    $(document).on("click", ".inquiry_items", function () {
        let inquiryItems = $(this);
        let currentContent = inquiryItems.next(".inquiry_content");
        let isSecret = inquiryItems.find('.material-symbols-outlined.secretItem').length > 0;
        let isWriter = inquiryItems.hasClass('isWriter');

        if (currentContent.length > 0) {
            if (isSecret) {
                if (isWriter) {
                    toggleContent(currentContent);
                } else {
                }
            } else {
                toggleContent(currentContent);
            }
        } else {
        }
    });

    // 수정 버튼
    $(document).on("click", ".edit_btn", function () {
        editInquiryModal(this);
    });

    $('#inquiryModal .close_btn').on('click', function () {
        closeInquiryModal();
    });

    // 삭제 버튼
    $(document).on("click", ".delete_btn", function () {
        let inquiryItem = $(this).closest(".inquiry_content").prev(".inquiry_items");
        let inquiryContent = inquiryItem.next(".inquiry_content");
        let inquiryId = $(this).data("id");

        deleteProductInquiry(inquiryId, inquiryItem, inquiryContent);
    });

    // 모달 닫기 버튼
    $('.close_btn').on('click', function () {
        $('#inquiryModal').hide();

        // 모달이 닫히면 inquiry_content 원래 상태로 복원
        let inquiryContent = $('#inquiryModal').closest('.inquiry_content');
        inquiryContent.find('.inquiry_cont').css({
            visibility: "visible",
            position: "relative"
        });

        // 첫 번째 tr의 border-top을 원래대로 되돌리기
        $('.inquiry_table tbody tr:first-child').css({
            'border-top': 'none'
        });
    });

    $('#inquiryModal .submit_btn').on('click', function () {
        let csrfToken = $("meta[name='_csrf']").attr("content");
        let csrfHeader = $("meta[name='_csrf_header']").attr("content");

        let title = $('#inquiryModal #title').val().trim();
        let content = $('#inquiryModal #content').val().trim();
        let isSecret = $('#inquiryModal #secret').prop('checked');
        let inquiryId = $('#inquiryModal').data('id');

        if (validateBeforeInquiryModalSubmit()) {

            let requestData = {
                'productId': productId,
                'title': title,
                'content': content,
                'isSecret': isSecret
            };

            $.ajax({
                url: '/ajax/inquiries/' + inquiryId + '/edit',
                type: 'POST',
                data: requestData,
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(csrfHeader, csrfToken)
                },
                success: function () {
                    closeInquiryModal();
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

// 상품문의 폼 열기
function openInquiryModal() {
    let inquiryModal = $("#inquiryModal");
    inquiryModal.removeData('id');

    inquiryModal.css("display", "flex");
    $('#inquiryModal .submit_btn').prop('disabled', false);
    $("#inquiryModal .submit_btn").text("등록");
}

function closeInquiryModal() {
    $('#inquiryModal').hide();
    $('#inquiryModal').removeData('id');
    $('#inquiryModal #title').val('');
    $('#inquiryModal #content').val('');
    $('#inquiryModal #secret').prop('checked', false);
    $("#inquiryModal .submit_btn").text("등록");

    // inquiry_content 원래 상태로 복원
    let inquiryContent = $('#inquiryModal').closest('.inquiry_content');
    inquiryContent.find('.inquiry_cont').css({
        visibility: "visible",
        position: "relative"
    });

    $('.inquiry_table tbody tr:first-child').css({
        'border-top': 'none'
    });
}

// 비밀글 찾기
function findSecretItem(editButton) {
    let inquiryItems = $(editButton).closest('.inquiry_content').prev('.inquiry_items');
    let secretItem = inquiryItems.find('.material-symbols-outlined.secretItem');
    return secretItem.length > 0;
}

// 상품문의 수정 폼
function editInquiryModal(editButton) {
    let inquiryModal = $('#inquiryModal');
    let inquiryId = $(editButton).data('id');

    let inquiryTitle = $(editButton).closest('.inquiry_cont').find('.question').text().trim();
    let inquiryContent = $(editButton).closest('.inquiry_cont').find('.content').text().trim();
    let isSecret = findSecretItem(editButton);

    $('#inquiryModal').data('id', inquiryId);
    $('#inquiryModal #title').val(inquiryTitle);
    $('#inquiryModal #content').val(inquiryContent);
    $("#inquiryModal .submit_btn").text("완료");
    $('#inquiryModal #secret').prop('checked', isSecret);

    let inquiryCont = $(editButton).closest('.inquiry_cont').parent();

    inquiryCont.append(inquiryModal.detach());
    inquiryCont.css("position", "relative");
    inquiryCont.children().not(inquiryModal).css({
        visibility: "hidden",
        position: "absolute"
    });

    inquiryModal.css({
        top: 0,
        left: 0,
        width: '100%',
        display: 'flex',
        zIndex: 10,
    }).show();

    $('#inquiryModal .submit_btn').prop('disabled', false);
}

// 상품문의 클릭 시 내용 보이게
function toggleContent(content) {
    if (content.is(":visible")) {
        content.find("#inquiryModal").hide();
        content.children().css({
            visibility: "visible",
            position: "relative"
        });

        content.stop(true,true).slideUp("fast", function () {
            content.css("border-bottom", "");
            content.prev(".inquiry_items").css("border-bottom", "");
        });

    } else {
        $(".inquiry_content").not(content).stop(true,true).slideUp("fast").promise().done(function () {
            $(this).css("border-bottom", "").prev(".inquiry_items").css("border-bottom", "");

            // 기존 inquiry_content 내부 내용을 다시 보이게 설정
            $(this).children().css({
                visibility: "visible",
                position: "relative"
            });
        });

        content.stop(true,true).slideDown("fast", function () {
            content.css("border-bottom", "1px solid #333");
            content.prev(".inquiry_items").css("border-bottom", "1px solid #333");
        });
    }
}

// 상세페이지 문의 삭제
function deleteProductInquiry(inquiryId, inquiryItem, inquiryContent) {
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
                data: {'inquiryId': inquiryId},
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(csrfHeader, csrfToken)
                },
                success: function (data) {
                    if (data.code === 200) {
                        inquiryItem.remove(); // 질문 행 삭제
                        inquiryContent.remove(); // 내용 행 삭제
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

// 상세페이지 문의 제출 전 유효성 검사
function validateBeforeInquiryModalSubmit() {
    let title = $('#inquiryModal #title').val().trim();
    let content = $('#inquiryModal #content').val().trim();

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

    $('#inquiryModal .submit_btn').prop('disabled', true);
    return true;
}