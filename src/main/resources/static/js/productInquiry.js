$(document).ready(function() {
    // 팝업 창을 띄우는 함수
    $('#popupBtn').click(function() {
        window.open("/shop/products/search", "_blank", "width=600px,height=450px");
    });
    // 상품문의
    var inquiryModal = $("#inquiryModal");
    var closeBtn = $(".close");

    closeBtn.click(function() {
        inquiryModal.hide();
    });

    $(window).click(function(event) {
        if (event.target.id === "inquiryModal") {
            inquiryModal.hide();
        }
    });

    $(".qna_items").click(function() {
        let currentContent = $(this).next(".qna_content");

        // 이미 열려 있는 항목을 클릭한 경우 닫기
        if (currentContent.is(":visible")) {
            currentContent.slideUp("fast");
        } else {
            // 먼저 모든 qna_content를 닫습니다
            $(".qna_content").not(currentContent).slideUp("fast").promise().done(function() {
                // slideUp이 완료된 후에 현재 클릭된 항목을 열거나 닫습니다
                currentContent.slideDown();
            });
        }
    });


    $(".edit_btn").click(function() {
        var questionText = $(this).closest(".qna_question").find(".qna_text").text();
        var answerText = $(this).closest(".qna_content").find(".qna_answer").text();

        $("#edit_title").val(questionText.trim());
        $("#edit_content").val(answerText.trim());

        $("#submitBtn").hide();
        $("#editSubmitBtn").show();

        $("#inquiryModal").css("display", "flex");
    });
});

function submitComment() {
    let commentText = $('#cmtInput').val().trim();
    let today = new Date().toISOString().slice(0, 10); // 현재 날짜
    if (commentText !== '') {
        let newComment = `
                    <div class="comment">
                        <div class="cmt_info">
                            <span class="id">관리자</span>
                            <span class="date">${today}</span>
                            <div class="btn_wrap details right">
                               <div class="menu">
                                    <button class="menu_toggle">
                                        <span class="material-symbols-outlined">more_horiz</span>
                                    </button>
                                    <div class="dropdown_menu">
                                        <ul>
                                            <li>
                                                <button type="button" class="edit_btn">수정</button>
                                            </li>
                                            <li>
                                                <button type="button" class="delete_btn">삭제</button>
                                            </li>
                                        </ul>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="cmt_content">
                            <span>${commentText}</span>
                        </div>
                    </div>
                `;
        $('.cmt_wrap').append(newComment);
        $('#cmtInput').val(''); // 입력 필드 초기화
    } else {
        alert('댓글을 입력해 주세요.');
    }
}

function editComment(editButton) {
    let commentElement = editButton.closest('.comment');
    let commentText = commentElement.find('.cmt_content span').text().trim();
    let textArea = `<textarea class="edit_textarea">${commentText}</textarea>`;
    let updateButton = `<button type="button" class="bnt1 update_btn" onclick="updateComment($(this))">완료</button>`;
    commentElement.find('.cmt_content').html(textArea + updateButton);
}

function updateComment(updateButton) {
    let commentElement = updateButton.closest('.comment');
    let updatedText = commentElement.find('.edit_textarea').val().trim();
    if (updatedText !== '') {
        let newContent = `<span>${updatedText}</span>`;
        commentElement.find('.cmt_content').html(newContent);
    } else {
        alert('댓글을 입력해 주세요.');
    }
}

function deleteComment(deleteButton) {
    let commentElement = deleteButton.closest('.comment');
    commentElement.remove();
}

function validateBeforeSubmit() {
    let content = $('#content').val().trim();
    let product = $('#productId').val().trim();
    let title = $('#title').val().trim();

    if (product === '') {
        alert('상품을 선택해 주세요.');
        return false;
    }

    if (title === '') {
        alert('제목을 작성해 주세요.');
        return false;
    }

    if (content === '') {
        alert('내용을 작성해 주세요.');
        return false;
    }
    return true;
}

function deleteOk(qnaId) {
    if (confirm("정말 삭제하시겠습니까?\n한번 삭제한 게시글은 복구할 수 없습니다.")) {
        window.location.href = '/community/inquiry/' + qnaId + "/delete";
        return true;
    } else {
        return false;
    }
}