$(document).ready(function () {

    let inquiryId = $('.inquiry_info').data('id');
    loadAnswer(inquiryId);

    $('#cmtInput').on('keydown', function (e) {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            $('#addAnswerBtn').click();
        }
    });

    $(document).on('keydown', '#cmtEditInput', function (e) {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            $('#updateAnswerBtn').click();
        }
    });


    $('#addAnswerBtn').on('click', function () {
        addAnswer(inquiryId);
    });

    $('#updateAnswerFormBtn').on('click', function () {
        updateAnswerForm(inquiryId);
    });

    $(document).on("click", "#updateAnswerBtn", function () {
        updateAnswer(inquiryId);
        $('.edit_btn').closest('.comment').css('border', '1px solid #d7d7d7');
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
                if(data.status == 200) {
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
                $('.comment .cmt_content').html(data['content'].replace(/\n/g, '<br>'));
                $('.cmt_wrap').css('display', 'flex');
                $('.cmt_input').css('display', 'none');
            } else {
                $('.cmt_wrap').css('display', 'none');
                $('#cmtInput').css('display', 'flex');
                $('#addCommentBtn').css('display', 'flex');
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
function updateAnswerForm() {
    let commentElement = $('.edit_btn').closest('.comment');
    let commentHtml = commentElement.find('.cmt_content').html();
    let commentText = commentHtml.replace(/<br\s*\/?>/gi, '\n').trim();
    let textArea = `<textarea id="cmtEditInput" class="edit_textarea">${commentText}</textarea>`;
    let updateButton = `<button type="button" class="bnt1 update_btn" id="updateAnswerBtn">완료</button>`;
    commentElement.css('border', 'none');
    commentElement.find('.cmt_content').html(textArea + updateButton);
}

// 답변 수정
function updateAnswer(inquiryId) {
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
                if (data.status === 200) {
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
                    if (data.status === 200) {
                        $('.cmt_input').css('display', 'flex');
                        clearAnswerContent()
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
function clearAnswerContent() {
    $('.comment .date').text('');
    $('.comment .cmt_content').text('');
    $('.cmt_wrap').hide();
}