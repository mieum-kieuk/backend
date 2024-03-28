function validateBeforeSubmit() {
    let nameValue = $('#name').val().trim();
    let priceValue = $('#price').val().trim();
    let stockQuantityValue = $('#stockQuantity').val().trim();
    let detailsValue = $('#details').val().trim();
    let sizeGuideValue = $('#sizeGuide').val().trim();
    let shippingValue = $('#shipping').val().trim();
    let noticeValue = $('#notice').val().trim();

    // 파일 업로드 요소의 값을 가져오기 위해 해당 요소에 접근합니다.
    let displayImageValue = $('#editDisplayImage')[0].files;
    let imageFilesValue = $('#editImageFiles')[0].files;

    let nameRegex = /^[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9\s]+$/;
    if (!nameRegex.test(nameValue)) {
        alert('상품명은 한글, 영문, 숫자, 공백만 허용됩니다.');
        return false;
    }

    if (parseFloat(priceValue) < 1) {
        alert('상품 가격을 입력해 주세요.');
        return false;
    }

    if (isNaN(stockQuantityValue) || stockQuantityValue === '') {
        alert('재고 수량을 입력해 주세요.');
        return false;
    }

    if (detailsValue === '') {
        alert('상세 정보를 입력해 주세요.');
        return false;
    }

    if (sizeGuideValue === '') {
        alert('상품 크기를 입력해 주세요.');
        return false;
    }

    if (shippingValue === '') {
        alert('배송 정보를 입력해 주세요.');
        return false;
    }

    if (noticeValue === '') {
        alert('주의 사항을 입력해 주세요.');
        return false;
    }

    if (displayImageValue.length === 0) { // 파일이 선택되지 않았을 때
        alert('섬네일 사진을 선택해주세요.');
        return false;
    }

    if (imageFilesValue.length > 0) {
        let maxSize = 20 * 1024 * 1024;
        let fileSize = imageFilesValue[0].size;
        if (fileSize > maxSize) {
            alert('파일 크기가 너무 큽니다. 20MB 이하의 파일을 업로드해주세요.');
            return false;
        }
    }

    return true;
}

let deleteButton = $('<button>').addClass('delete_btn').click(function() {
    $(this).closest('.preview_image_container').remove(); // 부모 요소 삭제
});

let container1 = $('#previewContainer1');
let image1 = container1.find('.preview_image');
let filename1 = container1.find('.file_name');

$('#displayImage1').change(function() {
    if (this.files && this.files[0]) {
        let imageFile = this.files[0];
        let reader = new FileReader();

        reader.onload = function(e) {
            image1.attr('src', e.target.result);
            filename1.text(imageFile.name);
        };

        reader.readAsDataURL(this.files[0]);
    }
    container1.css('display', 'flex');
});

let container2 = $('#previewContainer2');
let image2 = container2.find('.preview_image');
let filename2 = container2.find('.file_name');

$('#displayImage2').change(function() {
    if (this.files && this.files[0]) {
        let imageFile = this.files[0];
        let reader = new FileReader();

        reader.onload = function(e) {
            image2.attr('src', e.target.result);
            filename2.text(imageFile.name);
        };

        reader.readAsDataURL(this.files[0]);
    }
    container2.css('display', 'flex');
});

$('#detailsImages').change(function() {
    let files = this.files; // 선택된 파일 목록 가져오기
    let previewContainer = $('#previewContainer3');

    previewContainer.empty();

    for (let i = 0; i < files.length; i++) {
        let file = files[i];

        // FileReader 객체 생성
        let reader = new FileReader();

        reader.onload = function(e) {
            // 미리보기 이미지 및 파일명 생성
            let previewImage = $('<img>').addClass('preview_image').attr('src', e.target.result);
            let fileNameSpan = $('<span>').addClass('file_name').text(file.name);

            // 삭제 버튼 생성 및 이벤트 핸들러 등록
            let deleteButton = $('<button>').addClass('delete_btn').click(function() {
                $(this).closest('.preview_image_container').remove(); // 부모 요소 삭제
            });
            let deleteSpan = $('<span>').addClass('material-symbols-outlined').text('close');
            deleteButton.append(deleteSpan);

            // 삭제 버튼을 감싸는 div 생성
            let btnWrap = $('<div>').addClass('btn_wrap').append(deleteButton);

            // 미리보기 컨테이너에 이미지, 파일명, 삭제 버튼 추가
            let previewDiv = $('<div>').addClass('preview_image_container').append(previewImage, fileNameSpan, btnWrap);
            previewContainer.append(previewDiv);
        };

        // 파일 읽기 요청
        reader.readAsDataURL(file);
    }

    // 미리보기 컨테이너 표시
    previewContainer.css('display', 'flex');
});
