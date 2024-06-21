$(document).ready(function () {
    // 팝업 창을 띄우는 함수
    $('#popupBtn').click(function () {
        window.open("/shop/products/search", "_blank", "width=600px,height=450px");
    });

    // 상품문의
    let inquiryModal = $("#inquiryModal");
    let closeBtn = $(".close");

    closeBtn.click(function () {
        inquiryModal.hide();
    });

    $(window).click(function (event) {
        if (event.target.id === "inquiryModal") {
            inquiryModal.hide();
        }
    });

    $(".qna_items").click(function () {
        let currentContent = $(this).next(".qna_content");

        if (currentContent.is(":visible")) {
            currentContent.slideUp("fast");
        } else {
            $(".qna_content").not(currentContent).slideUp("fast").promise().done(function () {
                // slideUp이 완료된 후에 현재 클릭된 항목을 열거나 닫습니다
                currentContent.slideDown();
            });
        }
    });

    $(".edit_btn").click(function () {
        let questionText = $(this).closest(".qna_question").find(".qna_text").text();
        let answerText = $(this).closest(".qna_content").find(".qna_answer").text();

        $("#edit_title").val(questionText.trim());
        $("#edit_content").val(answerText.trim());

        $("#submitBtn").hide();
        $("#editSubmitBtn").show();

        $("#inquiryModal").css("display", "flex");
    });
});

let csrfHeader = $("meta[name='_csrf_header']").attr("content");
let csrfToken = $("meta[name='_csrf']").attr("content");

//답변 작성
function addAnswer(inquiryId) {

    let content = $('#cmtInput').val().trim()

    if (content !== '') {
        // if ($('.comment .cmt_content').text().trim() !== '') {
        //     if (!confirm('이미 작성된 답변이 있습니다. 기존의 답변을 덮어쓰시겠습니까?')) {
        //         return;
        //     }
        // }

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
                    alert(data.message);
                }
            },
            error: function () {
                alert('요청을 처리하는 동안 오류가 발생했습니다. 다시 시도해 주세요.');
            }
        });
    } else {
        alert('답변을 작성해 주세요.');
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
            alert('댓글을 불러오는 중 오류가 발생했습니다.');
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
                    alert(data.message);
                }
            },
            error: function () {
                alert('수정 중 오류가 발생했습니다. 다시 시도해 주세요.');
            }
        })
    } else {
        alert('답변을 입력해 주세요.');
    }
}

// 답변삭제
function deleteAnswer(inquiryId, answerId) {

    console.log('deleteAnswer!!');

    if (confirm("삭제하시겠습니까?")) {
        $.ajax({
            type: 'DELETE',
            url: '/ajax/admin/product/inquiry/delete',
            async: false,
            data: {'answerId' : answerId},
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken);
            },
            success: function (data) {
                if (data.code === 200) {
                    $('#cmtInput').css('display', 'block');
                    $('#addCommentBtn').css('display', 'block');
                    loadAnswer(inquiryId);
                } else {
                    alert(data.message);
                }
            },
            error: function () {
                alert('삭제 중 오류가 발생했습니다. 다시 시도해 주세요.');
            }
        })
    } else {
        return false;
    }
}