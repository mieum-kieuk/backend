$(document).ready(function () {

    //등록, 수정: 셀렉트 박스 css
    $('.select_wrap select').on('change', function () {
        if ($(this).val() === '') {
            $(this).removeClass('selected');
        } else {
            $(this).addClass('selected');
        }
    });
    // 체크박스 전체 선택
    selectCheckboxes();
    $('#displayImageBtn').on('click', function () {
        $('#displayImage').click();
    });
    $('#hoverImageBtn').on('click', function () {
        $('#hoverImage').click();
    });
    $('#detailsImagesBtn').on('click', function () {
        $('#detailsImages').click();
    });
    //등록, 수정: 섬네일 사진1 첨부
    $('#addProduct #displayImage').change(async function () {
        await updatePreviewContainer($(this), 'previewContainer1', '섬네일 사진1');
    });

    //등록, 수정: 섬네일 사진2 첨부
    $('#addProduct #hoverImage').change(async function () {
        await updatePreviewContainer($(this), 'previewContainer2', '섬네일 사진2');
    });

    //등록, 수정: 상세 페이지 사진 첨부
    $('#detailsImages').change(async function () {
        await handleDetailsImagesChange();
    });

    //등록, 수정: X(삭제) 클릭 시 첨부된 이미지 삭제
    $('.delete_btn').click(function () {
        let previewContainer = $(this).closest('.preview_container');
        previewContainer.find('.preview_image').attr('src', '');

        let fileInput = previewContainer.prev('.input_box_wrap').find('input[type="file"]');
        fileInput.val('');

        previewContainer.hide();
    });
});

function selectCheckboxes() {
    $('#selectAll').click(function () {
        let isChecked = $(this).prop('checked');
        $('.list_content input[type="checkbox"]').prop('checked', isChecked);
    });

    $('.list_content').on('change', 'input[type="checkbox"]', function () {
        let totalCheck = $('.list_content .list input[type="checkbox"]').length;
        let checkedBox = $('.list_content .list input[type="checkbox"]:checked').length;
        $('#selectAll').prop('checked', totalCheck === checkedBox);
    });
}

//등록, 수정: 섬네일 사진1, 섬네일 사진2 유효성 검사 -> 첨부
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

let dataTransfer = new DataTransfer();

//등록, 수정: 상세 페이지 사진 유효성 검사 -> 첨부
async function handleDetailsImagesChange() {
    let previewContainer = $('#previewContainer3');
    let newFileArr = $('#detailsImages')[0].files;

    let newFileSize = newFileArr.length;
    let originalFileSize = dataTransfer.files.length;
    let maxSizePerFile = 3 * 1024 * 1024;

    let exceedsMaxSize = false;
    let exceedsMaxFiles = originalFileSize + newFileSize > 20;

    for (let i = 0; i < newFileSize; i++) {
        let fileSize = newFileArr[i].size;

        if (fileSize > maxSizePerFile) {
            exceedsMaxSize = true;
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
        $('#detailsImages').val('');
        return false;
    }

    if (exceedsMaxFiles) {
        Swal.fire({
            text: "상세 페이지 사진은 최대 20장까지 가능합니다.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        $('#detailsImages').val('');
        return false;
    }

    // 기존 파일 목록 가져오기
    let existingFiles = Array.from(dataTransfer.files);
    let newFileNames = Array.from(newFileArr).map(file => file.name);

    // 중복 파일 검사
    for (let i = 0; i < newFileNames.length; i++) {
        if (existingFiles.some(file => file.name === newFileNames[i])) {
            Swal.fire({
                text: '이미 추가된 파일입니다.',
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
            $('#detailsImages').val('');
            return false;
        }
    }

    for (let i = 0; i < newFileSize; i++) {
        dataTransfer.items.add(newFileArr[i]);
    }
    $('#detailsImages')[0].files = dataTransfer.files;

    for (let i = 0; i < newFileSize; i++) {
        await addImagePreview(previewContainer, newFileArr[i]);
    }

    previewContainer.css('display', 'flex');
}

//등록, 수정: 상세 이미지 사진 첨부시 뷰 생성
async function addImagePreview(container, file) {
    let reader = new FileReader();

    return new Promise((resolve) => {
        reader.onload = function (e) {

            container.find('.preview_image_container:has(.preview_image[src=""])').remove();

            let containerDiv = $('<div>').addClass('preview_image_container');
            let previewImages = $('<div>').addClass('preview_images');
            let previewImage = $('<img>').addClass('preview_image').attr('src', e.target.result);
            let filenameContainer = $('<div>').addClass('filename_container');
            let fileName = $('<span>').addClass('file_name').text('파일명: ' + file.name);
            let deleteButton = $('<button>').addClass('delete_btn').append($('<span>').addClass('material-symbols-outlined').text('close'));

            deleteButton.click(function () {

                for (let i = 0; i < dataTransfer.items.length; i++) {
                    if (dataTransfer.items[i].getAsFile().name === file.name) {
                        dataTransfer.items.remove(i);
                        break;
                    }
                }

                $('#detailsImages')[0].files = dataTransfer.files;
                containerDiv.remove();
            });

            filenameContainer.append($('<span>').text('파일명: '), fileName);
            containerDiv.append(previewImages.append(previewImage), fileName, $('<div>').addClass('btn_wrap').append(deleteButton));
            container.append(containerDiv);

            resolve();
        };

        reader.readAsDataURL(file);
    });
}

let isNameChecked = false;
let isAvailableName = false;

$(document).ready(function () {
    $('#name').on('focusout', function () {
        isNameValid();
    });
});

//유효성 검사
function validateBeforeSubmit() {
    let nameValue = $('#name').val().trim();
    let categoryValue = $('#category').val();
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
    } else if (!isNameChecked)  {
        Swal.fire({
            text: '상품명 중복검사를 해주세요.',
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

    if (displayImageValue.length === 0) {
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
    for (let i = 0; i < detailsImagesValue.length; i++) {
        let fileSize = detailsImagesValue[i].size;
        if (fileSize > maxSizePerFile) {
            Swal.fire({
                text: "상세 페이지 사진 한 장의 크기가 3MB 이하여야 합니다.",
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
            $('#detailsImages').val('');
            return false;
        }
    }

    if (detailsImagesValue.length > 20) {
        Swal.fire({
            text: "상세 페이지 사진은 최대 20장까지 가능합니다.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        $('#detailsImages').val('');
        return false;
    }
    return true;
}

let csrfHeader = $("meta[name='_csrf_header']").attr("content");
let csrfToken = $("meta[name='_csrf']").attr("content");

function isNameValid() {
    let name = $('#name').val().trim();
    let nameRegex = /^[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9\s]+$/;

    if (name === '') {
        return;
    }
    if (!nameRegex.test(name)) {
        return;
    }

    $.ajax({
        type: 'POST',
        url: '/ajax/admin/product/check/name',
        async: false,
        data: {name: name},
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        },
        success: function (result) {
            isNameChecked = true;
            if (result.code == 200) {
                isAvailableName = true;
                $('#nameMsg').text(result.message);
                $('#nameMsg').removeClass('error').addClass('success');
            } else {
                isAvailableName = false;
                $('#nameMsg').text(result.message);
                $('#nameMsg').removeClass('success').addClass('error');
            }
        },
        error: function () {
            isAvailableName = false;
            $('#nameMsg').text('중복 확인 중 오류가 발생했습니다. 다시 시도해 주세요.');
            $('#nameMsg').removeClass('success').addClass('error');
        }
    });

    return;
}

//상품 단건 삭제
function deleteProduct(productId) {
    Swal.fire({
        text: "삭제하시겠습니까?",
        showCancelButton: true,
        cancelButtonText: '아니요',
        confirmButtonText: '예',
        customClass: mySwalConfirm,
        reverseButtons: true,
        buttonsStyling: false,
    }).then((result) => {
        if (result.isConfirmed) {
            $.ajax({
                type: 'DELETE',
                url: '/ajax/admin/product',
                async: false,
                data: {productId: productId},
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(csrfHeader, csrfToken)
                },
                success: function (result) {
                    if (result.code == 200) {
                        window.location.href = '/admin/product';
                    } else {
                        Swal.fire({
                            text: result.message,
                            showConfirmButton: true,
                            confirmButtonText: '확인',
                            customClass: mySwal,
                            buttonsStyling: false
                        });
                    }
                },
                error: function () {
                    Swal.fire({
                        html: "삭제 중 문제가 발생했습니다.<br>다시 시도해 주세요.",
                        showConfirmButton: true,
                        confirmButtonText: '확인',
                        customClass: mySwal,
                        buttonsStyling: false
                    });
                }
            });
        }
    });
}

//상품 여러건 삭제
$('#deleteProductsBtn').click(function () {

    let productIds = [];
    let checkboxes = $('input[name=checkbox]:checked');

    if (checkboxes.length == 0) {
        Swal.fire({
            text: "삭제할 상품을 선택해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    } else {
        Swal.fire({
            text: checkboxes.length + '개의 상품을 삭제하시겠습니까?',
            showCancelButton: true,
            cancelButtonText: '아니요',
            confirmButtonText: '예',
            customClass: mySwalConfirm,
            reverseButtons: true,
            buttonsStyling: false,
        }).then((result) => {
            if (result.isConfirmed) {
                $(checkboxes.each(function (v) {
                    let productId = checkboxes[v].id.split('checkbox')[1];
                    productIds.push(productId);
                }));

                $.ajax({
                    type: 'DELETE',
                    url: '/ajax/admin/products',
                    async: false,
                    contentType: 'application/json',
                    data: JSON.stringify(productIds),
                    beforeSend: function (xhr) {
                        xhr.setRequestHeader(csrfHeader, csrfToken)
                    },
                    success: function (data) {
                        if (data.code === 200) {
                            window.location.reload();
                        } else {
                            Swal.fire({
                                text: data.message,
                                showConfirmButton: true,
                                confirmButtonText: '확인',
                                customClass: mySwal,
                                buttonsStyling: false
                            });
                        }
                    },
                    error: function () {
                        Swal.fire({
                            html: "삭제중 문제가 발생했습니다.<br>다시 시도해 주세요.",
                            showConfirmButton: true,
                            confirmButtonText: '확인',
                            customClass: mySwal,
                            buttonsStyling: false
                        });
                    }
                });
            }
        });
    }
});
