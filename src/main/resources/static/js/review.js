$(document).ready(function() {

    $('.star_rating input').change(function() {
        let value = $(this).val();
        $('.star_rating .star').addClass('filled_star');
        $(this).prevAll('.star').removeClass('filled_star');
    });

    $('input[type="file"]').on('click', function(event) {
        let currentInput = $(this);
        let currentIndex = parseInt(currentInput.attr('id').replace('image', ''));

        // 이전 파일들이 선택되었는지 확인
        for (let i = 1; i < currentIndex; i++) {
            let previousInput = $('#image' + i);
            if (previousInput.val() === '') {
                alert('첨부 파일' + i + '을(를) 먼저 선택해 주세요.');
                event.preventDefault();
                return;
            }
        }
    }).on('change', function() {
        // 파일 크기 검사
        let maxSizePerFile = 1 * 1024 * 1024; // 최대 파일 크기 설정 (1MB)
        let fileInput = $(this)[0];
        if (fileInput.files.length > 0) {
            let fileSize = fileInput.files[0].size;
            if (fileSize > maxSizePerFile) {
                alert('첨부 파일 ' + fileInput.id.replace('image', '') + '의 크기가 1MB 이하여야 합니다.');
                $(this).val(''); // 파일 선택 취소
            }
        }
    });

});
function validateBeforeSubmit() {
    let product = $('#productId').val().trim();

    if (product === '') {
        alert('상품을 선택해 주세요.');
        return false;
    }
    let filledStars = $('.star_rating .star.filled_star').length; // 채워진 별의 개수를 가져옴

    if (filledStars === 0) {
        alert('별점을 입력해주세요.'); // 별점이 입력되지 않은 경우 경고창 표시
        return false;
    }
    let titleValue = $('#title').val().trim(); // 제목 입력값 가져오기
    let contentValue = $('#content').val().trim(); // 리뷰 입력값 가져오기

    if (titleValue === '') {
        alert('제목을 작성해 주세요.'); // 제목이 비어있을 경우 경고창 표시
        return false;
    }

    if (contentValue === '') {
        alert('리뷰를 작성해 주세요.'); // 리뷰가 비어있을 경우 경고창 표시
        return false;
    }


    return true; // 모든 조건을 통과하면 true 반환
}