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
            console.log('image' + i + ':', previousInput[0].files);
            if (previousInput.length > 0 && previousInput[0].files.length === 0) {
                Swal.fire({
                    text: "첨부 파일 " + i + "을(를) 먼저 선택해 주세요.",
                    showConfirmButton: true,
                    confirmButtonText: '확인',
                    customClass: mySwal,
                    buttonsStyling: false
                });
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
                Swal.fire({
                    text: "첨부 파일 " + fileInput.id.replace('image', '') + "의 크기가 1MB 이하여야 합니다.",
                    showConfirmButton: true,
                    confirmButtonText: '확인',
                    customClass: mySwal,
                    buttonsStyling: false
                });

                $(this).val(''); // 파일 선택 취소
            }
        }
    });

    // 상품후기, 상품문의
    let reviewModal = $("#reviewModal");
    let closeBtn = $(".close");
    let cancelBtn = $(".cancel_btn");


    // 모달 닫기
    closeBtn.click(function() {
        reviewModal.hide();
    });
    cancelBtn.click(function() {
        reviewModal.hide();
    });
    // 외부 클릭 시 모달 닫기
    $(window).click(function(event) {
         if (event.target.id === "reviewModal") {
            reviewModal.hide();
        }
    });
    $(".review_items").click(function() {
            let currentContent = $(this).next(".review_content");
            toggleContent(currentContent);
        });

    $(".review_content .edit_btn").click(function() {
        let reviewText = $(this).closest(".review_text_wrap").find(".review_text").text().trim();
        let reviewRating = $(this).closest(".review_cont").find(".star.filled_star").length;

        $("#content").val(reviewText);
        $("input[name='rating'][value='" + reviewRating + "']").prop('checked', true);

        $("#reviewModal .submit_btn").text("완료");

        showReviewModal();
    });
});
function showReviewModal() {
    let reviewModal = $("#reviewModal");
    reviewModal.css("display", "flex");

}
function toggleContent(content) {
    if (content.is(":visible")) {
        content.slideUp("fast", function() {
            content.css("border-bottom", "");
            content.prev(".review_items").css("border-bottom", "");
        });
    } else {
        $(".review_content").not(content).slideUp("fast").promise().done(function() {
            $(this).css("border-bottom", "").prev(".review_items").css("border-bottom", "");
            content.slideDown().css("border-bottom", "1px solid #333");
            content.prev(".review_items").css("border-bottom", "1px solid #333");

        });
    }
}
function validateReviewBeforeSubmit() {
    let productElement = $('#productId');
    if (productElement.length > 0) {
        let product = productElement.val().trim();
        if (product === '') {
            Swal.fire({
                text: "상품을 선택해 주세요.",
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
            return false;
        }
    }
    let filledStars = $('.star_rating .star.filled_star').length; // 채워진 별의 개수를 가져옴

    if (filledStars === 0) {
        Swal.fire({
            text: "별점을 입력해주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });

        return false;
    }
    let titleValue = $('#title').val().trim(); // 제목 입력값 가져오기
    let contentValue = $('#content').val().trim(); // 리뷰 입력값 가져오기

    if (titleValue === '') {
        Swal.fire({
            text: "제목을 작성해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });// 제목이 비어있을 경우 경고창 표시
        return false;
    }

    if (contentValue === '') {
        Swal.fire({
            text: "리뷰를 작성해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });// 리뷰가 비어있을 경우 경고창 표시
        return false;
    }


    return true; // 모든 조건을 통과하면 true 반환
}