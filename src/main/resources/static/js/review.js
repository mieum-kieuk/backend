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
            alert('제목을 입력해 주세요.');
            return false;
        }

        if (contentValue === '') {
            alert('리뷰를 작성해 주세요.');
            return false;
        }

        let imageFiles = [
            $('#image1')[0].files,
            $('#image2')[0].files,
            $('#image3')[0].files,
            $('#image4')[0].files,
            $('#image5')[0].files
        ];

        let maxSizePerFile = 1 * 1024 * 1024;

        for (let i = 0; i < imageFiles.length; i++) {
            if (imageFiles[i].length > 0) {
                let fileSize = imageFiles[i][0].size;
                if (fileSize > maxSizePerFile) {
                    alert('첨부 파일' + (i+1) + '의 크기가 1MB 이하여야 합니다.');
                    return false;
                }
            }
        }

        return true;
    }
});
