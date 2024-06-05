function validateBeforeSubmit() {
    let titleValue = $('#title').val().trim();
    let contentValue = $('#content').val().trim();

    if (titleValue === '') {
        alert('제목을 작성해 주세요.');
        return false;
    }

    if (contentValue === '') {
        alert('내용을 작성해 주세요.');
        return false;
    }

    $('.submit_btn').prop('disabled', true);
    return true;
}

function deleteOk(noticeId) {
    if (confirm("공지사항을 삭제하시겠습니까?")) {
        window.location.href = '/admin/notice/' + noticeId + "/delete";
        return true;
    } else {
        return false;
    }
}
