//유효성 검사
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

//공지사항 삭제
function deleteNotice(noticeId) {

    let csrfHeader = $("meta[name='_csrf_header']").attr("content");
    let csrfToken = $("meta[name='_csrf']").attr("content");

    if (confirm("삭제하시겠습니까?")) {
        $.ajax({
            type: 'DELETE',
            url: '/ajax/admin/notice/delete',
            async: false,
            data: {'noticeId': noticeId},
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken)
            },
            success: function (data) {
                if (data.code === 200) {
                    window.location.href = '/admin/notice';
                } else {
                    alert(data.message);
                }
            },
            error: function () {
                alert('삭제중 오류가 발생했습니다. 다시 시도해 주세요.');
            }
        })
    } else {
        return false;
    }
};
