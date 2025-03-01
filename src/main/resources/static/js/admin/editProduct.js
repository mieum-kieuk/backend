$(document).ready(function () {

    $('#name').on('change', function () {
        isNameValid();
    });

    let category = $('#category').val();
    if (category) {
        $('#category').addClass('selected');
    }

    $('#displayImageBtn').on('click', function () {
        $('#displayImage').click();
    });

    $('#hoverImageBtn').on('click', function () {
        $('#hoverImage').click();
    });

    $('#detailImagesBtn').on('click', function () {
        $('#detailImages').click();
    });

    // 섬네일 사진1 첨부
    $('#editProduct #displayImage').change(async function () {
        await updatePreviewContainer($(this), 'previewContainer1', '섬네일 사진1');
    });

    // 섬네일 사진2 첨부
    $('#editProduct #hoverImage').change(async function () {
        await updatePreviewContainer($(this), 'previewContainer2', '섬네일 사진2');
        $('#hoverImageDeleted').val('false');
    });

    // 상세 페이지 사진 첨부
    $('#detailImages').change(async function () {
        await handleDetailImagesChange();
    });
});

let csrfHeader = $("meta[name='_csrf_header']").attr("content");
let csrfToken = $("meta[name='_csrf']").attr("content");
let originalName = $('#name').val().trim();
let isAvailableName = true;

// 이름 검증
function isNameValid() {
    let name = $('#name').val().trim();
    let nameRegex = /^[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9\s]+$/;

    if (name === '') {
        return;
    }
    if (!nameRegex.test(name)) {
        return;
    }

    if (name === originalName) {
        isAvailableName = true;
    } else {
        $.ajax({
            type: 'POST',
            url: '/ajax/admin/product/check/name',
            async: false,
            data: {name: name},
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken);
            },
            success: function (result) {
                if (result.code == 200) {
                    isAvailableName = true;
                } else {
                    isAvailableName = false;
                }
            },
            error: function () {
                Swal.fire({
                    html: "상품명 중복 확인 중 오류가 발생했습니다.<br>다시 시도해 주세요.",
                    showConfirmButton: true,
                    confirmButtonText: '확인',
                    customClass: mySwal,
                    buttonsStyling: false
                });
            }
        });
    }

    return;
}

// 섬네일 사진1, 섬네일 사진2 유효성 검사 -> 첨부
async function updatePreviewContainer(input, containerId, thumbnailType) {
    let file = input[0].files[0];

    if (!file) return;

    let maxSizePerFile = 3 * 1024 * 1024; // 3MB
    let validFileType = 'image/jpeg';

    // 파일 형식 검사
    if (file.type !== validFileType) {
        Swal.fire({
            text: "JPG 형식의 이미지 파일만 첨부 가능합니다.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        input.val(''); // 입력 초기화
        return;
    }

    if (file.size > maxSizePerFile) {
        input.val('');
        let container = $('#' + containerId);
        let previewImages = container.find('.preview_images');
        previewImages.find('.preview_image').attr('src', '');
        container.find('.filename_container').empty();
        container.css('display', 'none');
        Swal.fire({
            text: thumbnailType + '의 크기가 3MB 이하여야 합니다.',
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return;
    }

    let reader = new FileReader();

    return new Promise((resolve) => {
        reader.onload = function (e) {
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

let existingFiles = []; //기존 파일 이름 목록 관리
let fileList = []; // 파일 목록

// 상세 페이지 사진 유효성 검사 -> 첨부
async function handleDetailImagesChange() {
    let previewContainer = $('#previewContainer3');
    let newFileArr = $('#detailImages')[0].files;
    let newFileSize = newFileArr.length;
    let originalFileSize = fileList.length; // fileList 배열의 길이를 사용
    let maxSizePerFile = 3 * 1024 * 1024;
    let invalidFileType = false;
    let exceedsMaxSize = false;
    let exceedsMaxFiles = originalFileSize + newFileSize;

    // 기존 파일 목록 업데이트
    existingFiles = Array.from($('#previewContainer3 .file_name')).map(fileName => {
        return $(fileName).text().replace('파일명: ', '').trim();
    });

    let validFiles = [];
    let hasDuplicate = false;

    for (let i = 0; i < newFileSize; i++) {
        let file = newFileArr[i];
        let isDuplicate = false;

        if (existingFiles.includes(file.name) || fileList.some(f => f.name === file.name)) {
            isDuplicate = true;
        }

        if (isDuplicate) {
            Swal.fire({
                text: '이미 첨부된 파일입니다.',
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
            hasDuplicate = true;
        } else {
            validFiles.push(file);
        }
    }

    // 유효성 검사
    for (let i = 0; i < validFiles.length; i++) {
        let file = validFiles[i];

        if (file.size > maxSizePerFile) {
            exceedsMaxSize = true;
        }
        if (file.type !== 'image/jpeg') {
            invalidFileType = true;
        }
    }

    if (exceedsMaxSize) {
        Swal.fire({
            text: "상세 페이지 사진 한 장의 크기가 3MB 이하여야 합니다.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        $('#detailImages').val('');
        return false;
    }

    if (exceedsMaxFiles > 20) {
        Swal.fire({
            text: "상세 페이지 사진은 최대 20장까지 가능합니다.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        $('#detailImages').val('');
        return false;
    }

    if (invalidFileType) {
        Swal.fire({
            text: "JPG 형식의 이미지 파일만 첨부 가능합니다.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        $('#detailImages').val('');
        return false;
    }

    // 기존 파일 목록에 새 파일 추가
    if (validFiles.length > 0) {
        validFiles.forEach(file => fileList.push(file));
    }

    // dataTransfer 업데이트
    let newDataTransfer = new DataTransfer();

    fileList.forEach(file => newDataTransfer.items.add(file));

    $('#detailImages')[0].files = newDataTransfer.files;

    // 미리보기 생성
    for (let i = 0; i < validFiles.length; i++) {
        await addImagePreview(previewContainer, validFiles[i]);
    }

    previewContainer.css('display', 'flex');
}

async function addImagePreview(container, file) {
    let reader = new FileReader();

    return new Promise((resolve) => {
        reader.onload = function (e) {
            let containerDiv = $('<div>').addClass('preview_image_container');
            let previewImages = $('<div>').addClass('preview_images');
            let previewImage = $('<img>').addClass('preview_image').attr('src', e.target.result);
            let filenameContainer = $('<div>').addClass('filename_container');
            let fileName = $('<span>').addClass('file_name').text(file.name);
            let deleteButton = $('<button>').addClass('delete_btn').append($('<span>').addClass('material-symbols-outlined').text('close'));

            // 기존 파일 배열에 새 파일 추가
            existingFiles.push(file.name);

            deleteButton.click(function () {
                // fileList에서 해당 파일 제거
                fileList = fileList.filter(f => f.name !== file.name);

                // dataTransfer 업데이트
                let newDataTransfer = new DataTransfer();
                fileList.forEach(f => newDataTransfer.items.add(f));
                $('#detailImages')[0].files = newDataTransfer.files;

                // 미리보기 이미지 제거
                containerDiv.remove();
            });

            filenameContainer.append($('<span>').text('파일명: '), fileName);
            containerDiv.append(previewImages.append(previewImage), filenameContainer, $('<div>').addClass('btn_wrap').append(deleteButton));
            container.append(containerDiv);

            resolve();
        };

        reader.readAsDataURL(file);
    });
}

// 폼 제출 전 유효성 검사
function validateBeforeSubmit() {
    let nameValue = $('#name').val().trim();
    let priceValue = $('#price').val().trim();
    let stockQuantityValue = $('#stockQuantity').val().trim();
    let displayImageValue = $('#displayImage')[0].files;
    let originalDisplayPreviewImage = $('#previewContainer1 .preview_image').attr('src');

    let hoverImageValue = $('#hoverImage')[0].files;
    let detailImagesValue = $('#detailImages')[0].files;

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
    } else if (!nameRegex.test(nameValue)) {
        Swal.fire({
            text: "상품명은 한글, 영문, 숫자, 공백만 허용됩니다.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    } else if (!isAvailableName) {
        Swal.fire({
            text: "이미 존재하는 상품명입니다.",
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

    if (displayImageValue.length === 0 && originalDisplayPreviewImage === '') {
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
    for (let i = 0; i < displayImageValue.length; i++) {
        let fileSize = displayImageValue[i].size;
        if (fileSize > maxSizePerFile) {
            Swal.fire({
                text: "섬네일 사진1의 크기가 3MB 이하여야 합니다.",
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
            $('#displayImage').val('');
            return false;
        }
    }

    for (let i = 0; i < hoverImageValue.length; i++) {
        let fileSize = hoverImageValue[i].size;
        if (fileSize > maxSizePerFile) {
            Swal.fire({
                text: "섬네일 사진2의 크기가 3MB 이하여야 합니다.",
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
            $('#hoverImage').val('');
            return false;
        }
    }
    for (let i = 0; i < detailImagesValue.length; i++) {
        let fileSize = detailImagesValue[i].size;
        if (fileSize > maxSizePerFile) {
            Swal.fire({
                text: "상세 페이지 사진 한 장의 크기가 3MB 이하여야 합니다.",
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
            $('#detailImages').val('');
            return false;
        }
    }

    if (detailImagesValue.length > 20) {
        Swal.fire({
            text: "상세 페이지 사진은 최대 20장까지 가능합니다.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        $('#detailImages').val('');
        return false;
    }
    $('.submit_btn').prop('disabled', true);
    return true;
}

$('.display_delete_btn').click(function () {
    let previewContainer = $(this).closest('.preview_container');
    previewContainer.find('.preview_image').attr('src', '');
    let fileInput = previewContainer.prev('.input_box_wrap').find('input[type="file"]');
    fileInput.val('');
    previewContainer.hide();
});

$('.hover_delete_btn').click(function () {
    let previewContainer = $(this).closest('.preview_container');
    previewContainer.find('.preview_image').attr('src', '');
    let fileInput = previewContainer.prev('.input_box_wrap').find('input[type="file"]');
    fileInput.val('');
    previewContainer.hide();

    $('#hoverImageDeleted').val('true');
});

$('.detail_delete_btn').click(function () {
    let previewContainer = $(this).closest('.preview_image_container');
    previewContainer.find('.preview_image').attr('src', '');
    let fileInput = previewContainer.prev('.input_box_wrap').find('input[type="file"]');
    fileInput.val('');
    previewContainer.find('.delete_file').remove();
    previewContainer.remove();
});

