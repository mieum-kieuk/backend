$(document).ready(function () {

    let inquiryId = $('.qna_info').data('id');
    loadAnswer(inquiryId);

    $('#addAnswerBtn').on('click', function () {
        addAnswer(inquiryId);
    });

    $('#updateAnswerFormBtn').on('click', function () {
        updateAnswerForm(inquiryId);
    });

    $(document).on("click", "#updateAnswerBtn", function () {
        updateAnswer(inquiryId);
    });


    $('#deleteAnswerBtn').on('click', function () {
        deleteAnswer(inquiryId);
    });
});

let csrfHeader = $("meta[name='_csrf_header']").attr("content");
let csrfToken = $("meta[name='_csrf']").attr("content");

// 답변 작성
function addAnswer(inquiryId) {
    let content = $('#cmtInput').val().trim()

    if (content !== '') {
        $.ajax({
            type: 'POST',
            url: '/ajax/admin/inquiries/' + inquiryId + '/answer/add',
            contentType: 'application/json',
            data: content,
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
                    html: "답변을 작성하는 동안 오류가 발생했습니다.<br>다시 시도해 주세요.",
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

// 답변 조회
function loadAnswer(inquiryId) {
    $.ajax({
        type: 'GET',
        url: '/ajax/admin/inquiries/' + inquiryId + '/answer',
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        },
        success: function(data) {
            if (data !== '') {
                $('.comment .date').text(data['createdAt']);
                $('.comment .cmt_content').text(data['content']);
                $('.cmt_wrap').css('display', 'flex');
                $('.cmt_input').css('display', 'none');
            } else {
                $('.cmt_wrap').css('display', 'none');
                $('#cmtInput').css('display', 'block');
                $('#addCommentBtn').css('display', 'block');
            }
        },
        error: function() {
            Swal.fire({
                text: "답변을 불러오는 중 오류가 발생했습니다.",
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
        }
    });
}

// 답변 수정 폼 조회
function updateAnswerForm(inquiryId) {
    let commentElement = $('.edit_btn').closest('.comment');
    let commentText = commentElement.find('.cmt_content').text().trim();
    let textArea = `<textarea class="edit_textarea">${commentText}</textarea>`;
    let updateButton = `<button type="button" class="bnt1 update_btn" id="updateAnswerBtn">완료</button>`;
    commentElement.find('.cmt_content').html(textArea + updateButton);
}

// 답변 수정
function updateAnswer(inquiryId) {
    console.log("updateAnswer!!");
    let updatedContent = $('.edit_textarea').val().trim();
    if (updatedContent !== '') {
        $.ajax({
            type: 'POST',
            url: '/ajax/admin/inquiries/' + inquiryId + '/answer/edit',
            contentType: 'application/json',
            data: updatedContent,
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
                    html: "답변 수정 중 오류가 발생했습니다.<br>다시 시도해 주세요.",
                    showConfirmButton: true,
                    confirmButtonText: '확인',
                    customClass: mySwal,
                    buttonsStyling: false
                });
            }
        })
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

// 답변삭제
function deleteAnswer(inquiryId) {

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
                url: '/ajax/admin/inquiries/' + inquiryId + '/answer',
                async: false,
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(csrfHeader, csrfToken);
                },
                success: function (data) {
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
                        html: "답변 삭제 중 오류가 발생했습니다.<br>다시 시도해 주세요.",
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
