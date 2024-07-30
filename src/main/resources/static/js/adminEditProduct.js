$(document).ready(function() {
    $('#displayImageBtn').on('click', function() {
        $('#displayImage').click();
    });
    $('#hoverImageBtn').on('click', function() {
        $('#hoverImage').click();
    });

    //섬네일 사진1 수정
    $('#editProduct #displayImage').change(async function() {
        await updatePreviewContainer($(this), 'previewContainer1', '섬네일 사진1');
        $('#displayImageUpdated').val('true');
    });

    //섬네일 사진2 수정
    $('#editProduct #hoverImage').change(async function() {
        await updatePreviewContainer($(this), 'previewContainer2', '섬네일 사진2');
        $('#hoverImageUpdated').val('true');
    });
});

//섬네일 사진1, 섬네일 사진2 유효성 검사 -> 첨부
async function updatePreviewContainer(input, containerId, thumbnailType) {
    let file = input[0].files[0];

    if (!file) return;

    let maxSizePerFile = 3 * 1024 * 1024; // 3MB

    if (file.size > maxSizePerFile) {
        input.val('');
        let container = $('#' + containerId);
        let previewImages = container.find('.preview_images');
        previewImages.find('.preview_image').attr('src', '');
        container.find('.filename_container').empty();
        container.css('display', 'none');
        alert(thumbnailType + `의 크기가 3MB 이하여야 합니다.`);
        return;
    }

    let reader = new FileReader();

    return new Promise((resolve) => {
        reader.onload = function(e) {
            let container = $('#' + containerId);
            let previewImages = container.find('.preview_images');

            let previewImage = previewImages.find('.preview_image');
            previewImage.attr('src', e.target.result);

            let filenameContainer = container.find('.filename_container');
            let fileName = $('<div>').addClass('file_name').text('파일명: ' + file.name);
            filenameContainer.empty().append(fileName);

            container.css('display', 'flex');
            resolve();
        };

        reader.readAsDataURL(file);
    });
}

//유효성 검사
function validateBeforeSubmit() {
    let nameValue = $('#name').val().trim();
    let priceValue = $('#price').val().trim();
    let stockQuantityValue = $('#stockQuantity').val().trim();
    let detailsValue = $('#details').val().trim();
    let sizeGuideValue = $('#sizeGuide').val().trim();
    let shippingValue = $('#shipping').val().trim();
    let noticeValue = $('#notice').val().trim();

    let displayImageValue = $('#displayImage')[0].files;
    let hoverImageValue = $('#hoverImage')[0].files;
    let detailsImagesValue = $('#detailsImages')[0].files;

    let nameRegex = /^[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9\s]+$/;
    if(nameValue === '') {
        alert('상품명을 입력해 주세요.');
        return false;
    } else if (!nameRegex.test(nameValue)) {
        alert('상품명은 한글, 영문, 숫자, 공백만 허용됩니다.');
        return false;
    }

    if (priceValue === '') {
        alert('상품 가격을 입력해 주세요.');
        return false;
    }
    if (parseInt(priceValue) < 0) {
        alert('유효한 상품 가격을 입력해 주세요.');
        return false;
    }

    if (stockQuantityValue === '') {
        alert('재고 수량을 입력해 주세요.');
        return false;
    }
    if (parseInt(stockQuantityValue) < 0) {
        alert('유효한 재고를 입력해 주세요.');
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

    if (displayImageValue.length === 0) {
        alert('섬네일 사진1을 첨부해 주세요.');
        return false;
    }

    let maxSizePerFile = 3 * 1024 * 1024;
    for (let i = 0; i < displayImageValue.length; i++) {
        let fileSize = displayImageValue[i].size;
        if (fileSize > maxSizePerFile) {
            alert('섬네일 사진1의 크기가 3MB 이하여야 합니다.');
            $('#displayImage1').val('');
            return false;
        }
    }

    for (let i = 0; i < hoverImageValue.length; i++) {
        let fileSize = hoverImageValue[i].size;
        if (fileSize > maxSizePerFile) {
            alert('섬네일 사진2의 크기가 3MB 이하여야 합니다.');
            $('#displayImage2').val('');
            return false;
        }
    }
    for (let i = 0; i < detailsImagesValue.length; i++) {
        let fileSize = detailsImagesValue[i].size;
        if (fileSize > maxSizePerFile) {
            alert('상세 페이지 사진 한 장의 크기가 3MB 이하여야 합니다.');
            $('#detailsImages').val('');
            return false;
        }
    }

    if (detailsImagesValue.length > 20) {
        alert('상세 페이지 사진은 최대 20장까지 가능합니다.');
        $('#detailsImages').val('');
        return false;
    }
    return true;
}

$('.display_delete_btn').click(function() {
    let previewContainer = $(this).closest('.preview_container');
    previewContainer.find('.preview_image').attr('src', '');
    let fileInput = previewContainer.prev('.input_box_wrap').find('input[type="file"]');
    fileInput.val('');
    previewContainer.hide();

    $('#displayImageUpdated').val('true');
});

$('.hover_delete_btn').click(function() {
    let previewContainer = $(this).closest('.preview_container');
    previewContainer.find('.preview_image').attr('src', '');
    let fileInput = previewContainer.prev('.input_box_wrap').find('input[type="file"]');
    fileInput.val('');
    previewContainer.hide();

    $('#hoverImageUpdated').val('true');
});