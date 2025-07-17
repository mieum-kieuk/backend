$(document).ready(function () {

    $(document).on("click", ".inquiry_items", function () {
        let inquiryItem = $(this);
        let currentContent = inquiryItem.next(".inquiry_content");
        if (currentContent.length > 0) {
            toggleContent(currentContent);
        }
    });

    // 삭제 버튼
    $(document).on("click", ".delete_btn", function () {
        let inquiryItem = $(this).closest(".inquiry_content").prev(".inquiry_items");
        let inquiryContent = inquiryItem.next(".inquiry_content");
        let inquiryId = $(this).data("id");

        deleteProductInquiry(inquiryId, inquiryItem, inquiryContent);
    });

});

// 상품문의 클릭 시 내용 보이게
function toggleContent(content) {
    if (content.is(":visible")) {
        content.children().css({
            visibility: "visible",
            position: "relative"
        });

        content.stop(true, true).slideUp("fast", function () {
            content.css("border-bottom", "");
            content.prev(".inquiry_items").css("border-bottom", "");
        });

    } else {
        $(".inquiry_content").not(content).stop(true, true).slideUp("fast").promise().done(function () {
            $(this).css("border-bottom", "").prev(".inquiry_items").css("border-bottom", "");

            $(this).children().css({
                visibility: "visible",
                position: "relative"
            });
        });

        content.stop(true, true).slideDown("fast", function () {
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
                    if (data.status === 200) {
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
