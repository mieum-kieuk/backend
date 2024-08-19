$(document).ready(function() {
    // 유효성 검사 함수
    function validateBeforeSubmit() {
        let nameValue = $('#name').val().trim();
        let categoryValue = $('#category').val();
        let priceValue = $('#price').val().trim();
        let stockQuantityValue = $('#stockQuantity').val().trim();
        let detailsValue = $('#details').val().trim();
        let sizeGuideValue = $('#sizeGuide').val().trim();
        let shippingValue = $('#shipping').val().trim();
        let noticeValue = $('#notice').val().trim();


        let displayImageValue1 = $('#displayImage1')[0].files;
        let displayImageValue2 = $('#displayImage2')[0].files;Swal.fire({
            text: thumbnailType + '의 크기가 3MB 이하여야 합니다.',
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        let imageFilesValue = $('#detailsImages')[0].files;


        let nameRegex = /^[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9\s]+$/;
        if (nameValue === '') {
            Swal.fire({
                text: "상품명을 입력해 주세요.",
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
            return false;
        }
        if (!nameRegex.test(nameValue)) {
            Swal.fire({
                text: "상품명은 한글, 영문, 숫자, 공백만 허용됩니다.",
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
            return false;
        }

        if (categoryValue === '') {
            Swal.fire({
                text: "카테고리를 선택해 주세요.",
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
            return false;
        }

        if (priceValue === '') {
            Swal.fire({
                text: "상품 가격을 입력해 주세요.",
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
            return false;
        }
        if (parseInt(priceValue) < 0) {
            Swal.fire({
                text: "유효한 상품 가격을 입력해 주세요.",
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
            return false;
        }

        if (stockQuantityValue === '') {
            Swal.fire({
                text: "재고 수량을 입력해 주세요.",
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
            return false;
        }
        if (parseInt(stockQuantityValue) < 0) {
            Swal.fire({
                text: "유효한 재고를 입력해 주세요.",
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
            return false;
        }

        if (detailsValue === '') {
            Swal.fire({
                text: "상세 정보를 입력해 주세요.",
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
            return false;
        }

        if (sizeGuideValue === '') {
            Swal.fire({
                text: "상품 크기를 입력해 주세요.",
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
            return false;
        }

        if (shippingValue === '') {
            Swal.fire({
                text: "배송 정보를 입력해 주세요.",
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
            return false;
        }

        if (noticeValue === '') {
            Swal.fire({
                text: "주의 사항을 입력해 주세요.",
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
            return false;
        }

        if (displayImageValue1.length === 0) {
            Swal.fire({
                text: "섬네일 사진1을 첨부해 주세요.",
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
            return false;
        }

        let maxSizePerFile = 3 * 1024 * 1024;
        for (let i = 0; i < displayImageValue1.length; i++) {
            let fileSize = displayImageValue1[i].size;
            if (fileSize > maxSizePerFile) {
                Swal.fire({
                    text: "섬네일 사진의 크기가 3MB 이하여야 합니다.",
                    showConfirmButton: true,
                    confirmButtonText: '확인',
                    customClass: mySwal,
                    buttonsStyling: false
                });
                return false;
            }
        }

        for (let i = 0; i < displayImageValue2.length; i++) {
            let fileSize = displayImageValue2[i].size;
            if (fileSize > maxSizePerFile) {
                Swal.fire({
                    text: "섬네일 사진의 크기가 3MB 이하여야 합니다.",
                    showConfirmButton: true,
                    confirmButtonText: '확인',
                    customClass: mySwal,
                    buttonsStyling: false
                });
                return false;
            }
        }
        for (let i = 0; i < imageFilesValue.length; i++) {
            let fileSize = imageFilesValue[i].size;
            if (fileSize > maxSizePerFile) {
                Swal.fire({
                    text: "첨부파일 하나의 크기가 3MB 이하여야 합니다.",
                    showConfirmButton: true,
                    confirmButtonText: '확인',
                    customClass: mySwal,
                    buttonsStyling: false
                });
                return false;
            }
        }

        let totalSizeLimit = 20 * 1024 * 1024;
        let totalSize = 0;
        for (let i = 0; i < imageFilesValue.length; i++) {
            totalSize += imageFilesValue[i].size;
        }
        if (totalSize > totalSizeLimit) {
            Swal.fire({
                text: "첨부파일 전체의 크기가 20MB 이하여야 합니다.",
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
            return false;
        }

        return true;
    }
});
//섬네일 사진 1 조회
function loadDisplayImage(result) {
    let prevContainer = $('#previewContainer1');
    let previewImage = prevContainer.find('.preview_image');
    let fileName = prevContainer.find('.file_name');
    let deleteBtn = prevContainer.find('.delete_btn');

    let id = result['id'];
    let uploadImageName = result['uploadImageName'];
    let storeImageName = result['storeImageName'];

    previewImage.attr('src', '/productImages/' + storeImageName);
    fileName.text('파일명: ' + uploadImageName);
    deleteBtn.attr('onclick', 'deleteDisplayImage(' + id + ')');

    prevContainer.show();
}

//섬네일 사진 2 조회
function loadHoverImage(result) {
    let prevContainer = $('#previewContainer2');
    let previewImage = prevContainer.find('.preview_image');
    let fileName = prevContainer.find('.file_name');
    let deleteBtn = prevContainer.find('.delete_btn');

    let id = result['id'];
    let uploadImageName = result['uploadImageName'];
    let storeImageName = result['storeImageName'];

    previewImage.attr('src', '/productImages/' + storeImageName);
    fileName.text('파일명: ' + uploadImageName);
    deleteBtn.attr('onclick', 'deleteHoverImage(' + id + ')');

    prevContainer.show();
}

//상세 페이지 사진
function loadDetailsImages(result) {
    let prevContainer = $('#previewContainer3');

    let id = result['id'];
    let uploadImageName = result['uploadImageName'];
    let storeImageName = result['storeImageName'];

    let containerDiv = $('<div>').addClass('preview_image_container').attr('id', 'productImage' + id);
    let previewImages = $('<div>').addClass('preview_images');
    let previewImage = $('<img>').addClass('preview_image').attr('src', '/productImages/' + storeImageName);
    let fileName = $('<span>').addClass('file_name').text('파일명: ' + uploadImageName);
    let deleteButton = $('<button>').addClass('delete_btn').attr('type', 'button').attr('onclick', 'deleteDetailsImage(' + id + ')').append($('<span>').addClass('material-symbols-outlined').text('close'));

    prevContainer.append(containerDiv.append(previewImages.append(previewImage), fileName, $('<div>').addClass('btn_wrap').append(deleteButton)));
    prevContainer.show();
}

//섬네일 사진1 삭제
function deleteDisplayImage(productImageId) {
    $.ajax({
        type: 'post',
        url: '/admin/productImages/' + productImageId + '/delete',
        success: function () {
            let previewContainer = $('#previewContainer1');
            previewContainer.find('.preview_image').attr('src', '');
            previewContainer.attr('src', '');
            previewContainer.prev('.input_box_wrap').find('input[type="file"]').val('');
            previewContainer.hide();

            $('#isDisplayImageChanged').val(true);
        },
        error: function (result) {
            let message = '[' + result['code'] + '] ' + result['message'];
            Swal.fire({
                text: message,
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
        }
    }).done(
        Swal.fire({
            text: "섬네일 사진1이 삭제되었습니다.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        }));
}

//섬네일 사진2 삭제
function deleteHoverImage(productImageId) {
    $.ajax({
        type: 'post',
        url: '/admin/productImages/' + productImageId + '/delete',
        success: function () {
            let previewContainer = $('#previewContainer2');
            previewContainer.find('.preview_image').attr('src', '');
            previewContainer.attr('src', '');
            previewContainer.prev('.input_box_wrap').find('input[type="file"]').val('');
            previewContainer.hide();
        },
        error: function (result) {
            let message = '[' + result['code'] + '] ' + result['message'];
            Swal.fire({
                text: message,
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
        }
    }).done(
        Swal.fire({
            text: "섬네일 사진2가 삭제되었습니다.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        }));
}

//상세 페이지 사진 삭제
function deleteDetailsImage(productImageId) {
    $.ajax({
        type: 'post',
        url: '/admin/productImages/' + productImageId + '/delete',
        success: function () {
            let previewContainer = $('#productImage' + productImageId);
            previewContainer.remove();
        },
        error: function (result) {
            let message = '[' + result['code'] + '] ' + result['message'];
            Swal.fire({
                text: message,
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
        }
    }).done(
    Swal.fire({
        text: "상세 페이지 첨부파일이 삭제되었습니다.",
        showConfirmButton: true,
        confirmButtonText: '확인',
        customClass: mySwal,
        buttonsStyling: false
    }));
}

// detailsImages 새로운 첨부 파일 추가
$('#detailsImages').change(function() {
    let files = this.files;
    let previewContainer = $('#newPreviewContainer3');

    previewContainer.find('.preview_image_container').remove();

    if (files.length === 0) {
        previewContainer.css('display', 'none'); // 파일이 없으면 미리보기 숨기기
        return;
    }

    for (let i = 0; i < files.length; i++) {
        let file = files[i]; // 현재 파일 가져오기

        addImagePreview(previewContainer, file);
    }

    previewContainer.css('display', 'flex');
});

function addImagePreview(container, file) {
    let reader = new FileReader();

    reader.onload = function(e) {

        let containerDiv = $('<div>').addClass('preview_image_container');
        let previewImages = $('<div>').addClass('preview_images');
        let previewImage = $('<img>').addClass('preview_image').attr('src', e.target.result);
        let fileName = $('<span>').addClass('file_name').text('파일명: ' + file.name);
        let deleteButton = $('<button>').addClass('delete_btn').attr('type', 'button').append($('<span>').addClass('material-symbols-outlined').text('close'));

        deleteButton.click(function() {
            containerDiv.remove();
        });

        containerDiv.append(previewImages.append(previewImage), fileName, $('<div>').addClass('btn_wrap').append(deleteButton));
        container.append(containerDiv);
    };

    reader.readAsDataURL(file);
}

//섬네일 사진 1 수정
$('#displayImage1').change(function() {
    updatePreviewContainer($(this), 'previewContainer1');
    $('#isDisplayImageChanged').val(true);
});

$('#displayImage2').change(function() {
    updatePreviewContainer($(this), 'previewContainer2');
});

//섬네일 사진 2 수정
function updatePreviewContainer(input, containerId) {
    let file = input[0].files[0];

    if (!file) return;

    let reader = new FileReader();

    reader.onload = function(e) {
        let container = $('#' + containerId); // containerId로 jQuery 객체 생성
        let previewImages = container.find('.preview_images');
        let previewImage = previewImages.find('.preview_image');
        let fileName = container.find('.file_name');

        previewImage.attr('src', e.target.result);
        fileName.text('파일명: ' + file.name);

        container.css('display', 'flex');
    };

    reader.readAsDataURL(file);
}