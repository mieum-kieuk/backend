$(document).ready(function() {
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
