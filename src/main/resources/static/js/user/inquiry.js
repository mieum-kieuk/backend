$(document).ready(function () {
    // 팝업 창을 띄우는 함수
    $('#popupBtn').click(function () {
        window.open("/products/search", "_blank", "width=600px,height=450px");
    });
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
//답변 작성
function addAnswer(inquiryId) {

    let content = $('#cmtInput').val().trim()

    if (content !== '') {
        if ($('.comment .cmt_content').text().trim() !== '') {
            if (!confirm('이미 작성된 답변이 있습니다. 기존의 답변을 덮어쓰시겠습니까?')) {
                return;
            }
        }

        $.ajax({
            type: 'POST',
            url: '/ajax/admin/product/inquiry/' + inquiryId + '/add',
            contentType: 'application/json;charset=utf-8',
            data: JSON.stringify(content),
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken);
            },
            success: function (data) {
                if(data.code == 200) {
                    $('#cmtInput').val('');
                    $('#cmtInput').css('display', 'none');
                    $('#addCommentBtn').css('display', 'none');
                    loadAnswer(inquiryId);
                } else {
                    Swal.fire({
                        text: data.message,
                        showConfirmButton: true,
                        confirmButtonText: '확인',
                        customClass: mySwal,
                        buttonsStyling: false
                    });
                }
            },
            error: function () {
                Swal.fire({
                    html: "요청을 처리하는 동안 오류가 발생했습니다.<br>다시 시도해 주세요.",
                    showConfirmButton: true,
                    confirmButtonText: '확인',
                    customClass: mySwal,
                    buttonsStyling: false
                });
            }
        });
    } else {
        Swal.fire({
            text: "답변을 작성해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
    }
}

//답변 조회
function loadAnswer(inquiryId) {
    console.log('loadAnswer!!');

    $.ajax({
        type: 'GET',
        url: '/ajax/admin/product/inquiry/' + inquiryId,
        dataType: 'json',
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        },
        success: function(data) {
            if (data['content'] !== null) {
                $('.comment .writer').text(data['name']);
                $('.comment .date').text(data['createdAt']);
                $('.comment .cmt_content').text(data['content']);
                $('.comment .edit_btn').attr('onclick', 'editAnswerForm(' + inquiryId + ',' + data['id'] + ')');
                $('.comment .delete_btn').attr('onclick', 'deleteAnswer(' + inquiryId + ',' +data['id'] + ')');
                $('.cmt_wrap').css('display', 'flex');
            } else {
                $('.cmt_wrap').css('display', 'none');
            }
        },
        error: function() {
            Swal.fire({
                text: "댓글을 불러오는 중 오류가 발생했습니다.",
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
        }
    });
}

//답변 수정 폼 조회
function editAnswerForm(inquiryId, answerId) {
    console.log('editAnswerForm!!');
    let commentElement = $('.edit_btn').closest('.comment');
    let commentText = commentElement.find('.cmt_content').text().trim();
    let textArea = `<textarea class="edit_textarea">${commentText}</textarea>`;
    let updateButton = `<button type="button" class="bnt1 update_btn" onclick="updateAnswer(` + inquiryId + `,` + answerId + `)">완료</button>`;
    commentElement.find('.cmt_content').html(textArea + updateButton);
}

//답변 수정
function updateAnswer(inquiryId, answerId) {
    console.log('update answer!!!');

    let updatedContent = $('.edit_textarea').val().trim();
    if (updatedContent !== '') {
        $.ajax({
            type: 'POST',
            url: '/ajax/admin/product/inquiry/edit',
            contentType: 'application/json;charset=utf-8',
            async: false,
            data: JSON.stringify({'answerId' : answerId, 'content' : updatedContent}),
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken);
            }, success: function (data) {
                if (data.code === 200) {
                    loadAnswer(inquiryId);
                } else {
                    Swal.fire({
                        text: data.message,
                        showConfirmButton: true,
                        confirmButtonText: '확인',
                        customClass: mySwal,
                        buttonsStyling: false
                    });
                }
            },
            error: function () {
                Swal.fire({
                    html: "수정 중 오류가 발생했습니다.<br>다시 시도해 주세요.",
                    showConfirmButton: true,
                    confirmButtonText: '확인',
                    customClass: mySwal,
                    buttonsStyling: false
                });
            }
        })
    } else {
        Swal.fire({
            text: "답변을 입력해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
    }
}

function validateBeforeSubmit() {
    let title = $('#title').val().trim();
    let content = $('#content').val().trim();

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

// 답변삭제
function deleteAnswer(inquiryId, answerId) {

    console.log('deleteAnswer!!');

    Swal.fire({
        text: "삭제하시겠습니까?",
        showCancelButton: true,
        cancelButtonText: '아니요',
        confirmButtonText: '예',
        closeOnConfirm: false,
        closeOnCancel: true,
        customClass: mySwalConfirm,
        reverseButtons: true,
        buttonsStyling: false,
    }).then((result) => {
        if (result.isConfirmed) {
            $.ajax({
                type: 'DELETE',
                url: '/ajax/admin/product/inquiry/delete',
                async: false,
                data: {'answerId': answerId},
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(csrfHeader, csrfToken);
                },
                success: function (data) {
                    if (data.code === 200) {
                        $('#cmtInput').css('display', 'block');
                        $('#addCommentBtn').css('display', 'block');
                        loadAnswer(inquiryId);
                    } else {
                        Swal.fire({
                            text: data.message,
                            showConfirmButton: true,
                            confirmButtonText: '확인',
                            customClass: mySwal,
                            buttonsStyling: false
                        });
                    }
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
            });
        }
    });
}
