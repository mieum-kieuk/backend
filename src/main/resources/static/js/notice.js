$(document).ready(function () {
    $('#submitBtn').click(function () {

        if (!validateBeforeSubmit()) {
            return false;
        } else {
            $('#addReviewForm').submit();
        }
    });

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

        return true;
    }
});

function deleteOk(noticeId) {
    if (confirm("정말 삭제하시겠습니까?\n한번 삭제한 게시글은 복구할 수 없습니다.")) {
        window.location.href = '/community/notice/' + noticeId + "/delete";
        alert("공지사항이 삭제되었습니다.");
        return true;
    } else {
        return false;
    }
}
