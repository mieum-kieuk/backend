//유효성 검사
function validateBeforeSubmit() {
    let titleValue = $('#title').val().trim();
    let contentValue = $('#content').val().trim();
    let contentValueLength = contentValue.length;

    if (titleValue === '') {
        alert('제목을 작성해 주세요.');
        return false;
    }

    if (contentValue === '') {
        alert('내용을 작성해 주세요.');
        return false;
    }

    if (contentValueLength > 2000) {
        alert('내용은 최대 2000자까지 작성할 수 있습니다.');
        return false;
    }

    $('.submit_btn').prop('disabled', true);
    return true;
}

//공지사항 삭제
function deleteNotice(noticeId) {

    let csrfHeader = $("meta[name='_csrf_header']").attr("content");
    let csrfToken = $("meta[name='_csrf']").attr("content");

    if (confirm("삭제하시겠습니까?")) {
        $.ajax({
            type: 'DELETE',
            url: '/ajax/admin/notice/delete',
            async: false,
            data: {noticeId: noticeId},
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken)
            },
            success: function (result) {
                if (result.code === 200) {
                    window.location.href = '/admin/notice';
                } else {
                    alert(reult.message);
                }
            },
            error: function () {
                alert('삭제 중 오류가 발생했습니다. 다시 시도해 주세요.');
            }
        })
    } else {
        return false;
    }
};
