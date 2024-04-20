$(document).ready(function() {

    $('#secret').change(function() {
        if ($(this).is(':checked')) {
            $('#password').prop('required', true);
        }
    });

    $('#public').change(function() {
        if ($(this).is(':checked')) {
            $('#password').prop('required', false);
        }
    });
    $('#submitBtn').click(function () {

        if (!validateBeforeSubmit()) {
            return false;
        } else {
            $('#addQnaForm').submit();
        }
    });
    $('#qnaPw .submit_btn').click(function(event) {
        var password = $('#password').val().trim();

        if (password === '') {
            event.preventDefault();
            alert('비밀번호를 입력해 주세요.');
        }
    });
    function validateBeforeSubmit() {
        // let titleValue = $('#title').val().trim();
        let contentValue = $('#content').val().trim();

        if (contentValue === '') {
            alert('내용을 작성해 주세요.');
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
        let isSecret = $('input[name="isSecret"]:checked').val();
        if (isSecret === 'true') {
            let password = $('#password').val();
            if (password === '') {
                alert('비밀번호를 입력해 주세요.');
            }
        }
        return true;
    }
});