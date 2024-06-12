$(document).ready(function() {
    initializeDropdownMenus();

    // 팝업 창을 띄우는 함수
    $('#popupBtn').click(function() {
        window.open("/shop/products/search", "_blank", "width=600px,height=450px");
    });
});

function initializeDropdownMenus() {
    // Document 클릭 이벤트
    $(document).on('click', function(event) {
        closeDropdownMenus(event);
    });

    // 메뉴 토글 클릭 이벤트
    $(document).on('click', '.menu_toggle', function() {
        toggleDropdownMenu($(this));
    });

    $(document).on('click', '.edit_btn', function() {
        editComment($(this));
    });

    $(document).on('click', '.delete_btn', function() {
        deleteComment($(this));
    });
}

function closeDropdownMenus(event) {
    var dropdownMenus = $('.dropdown_menu');

    // 메뉴 토글 또는 드롭다운 메뉴 내부를 클릭한 경우, 함수 종료
    if ($(event.target).closest('.menu_toggle, .dropdown_menu').length) {
        return;
    }

    // 모든 드롭다운 메뉴를 닫기
    dropdownMenus.removeClass('show');
}

function toggleDropdownMenu(toggleElement) {
    var dropdownMenu = toggleElement.siblings('.dropdown_menu');

    // 다른 모든 드롭다운 메뉴를 닫기
    $('.dropdown_menu').not(dropdownMenu).removeClass('show');

    // 해당 드롭다운 메뉴 토글
    dropdownMenu.toggleClass('show');
}

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