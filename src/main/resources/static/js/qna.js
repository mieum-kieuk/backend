$(document).ready(function() {

    //팝업 창을 띄우는 함수
    $('#popupBtn').click(function() {
        window.open("/shop/products/search", "_blank", "width=600px,height=450px");
    });

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
            alert('비밀번호를 입력해 주세요.');
            return false;
        }
    });

    $('input[type="file"]').on('click', function(event) {
        var currentInput = $(this);
        var currentIndex = parseInt(currentInput.attr('id').replace('image', ''));

        for (var i = 1; i < currentIndex; i++) {
            var previousInput = $('#image' + i);
            if (previousInput.val() === '') {
                alert('첨부 파일' + i + '을(를) 먼저 선택해 주세요.');
                event.preventDefault();
                return;
            }
        }
    });

    function validateBeforeSubmit() {
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
                return false;
            }
        }
        return true;
    }
});

function deleteOk(qnaId) {
    if (confirm("정말 삭제하시겠습니까?\n한번 삭제한 게시글은 복구할 수 없습니다.")) {
        window.location.href = '/community/qna/' + qnaId + "/delete";
        alert("Q&A가 삭제되었습니다.");
        return true;
    } else {
        return false;
    }
}
