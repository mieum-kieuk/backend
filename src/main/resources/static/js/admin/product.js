$(document).ready(function () {

    //등록, 수정: 셀렉트 박스 css
    $('.select_wrap select').on('change', function () {
        if ($(this).val() === '') {
            $(this).removeClass('selected');
        } else {
            $(this).addClass('selected');
        }
    });

    selectCheckboxes();

    // 상품명 중복 검사
    $('#name').on('change', function () {
        isNameValid();
    });

    // 대분류 선택 시 소분류 업데이트
    $("#parentCategory").on("change", function () {
        let parentId = $(this).val();
        loadChildCategory(parentId);
    });

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
    $('#addProduct #displayImage').change(async function () {
        await updatePreviewContainer($(this), 'previewContainer1', '섬네일 사진1');
    });

    // 섬네일 사진2 첨부
    $('#addProduct #hoverImage').change(async function () {
        await updatePreviewContainer($(this), 'previewContainer2', '섬네일 사진2');
    });

    // 상세 페이지 사진 첨부
    $('#detailImages').change(async function () {
        await handleDetailImagesChange();
    });

    // X(삭제) 클릭 시 첨부된 이미지 삭제
    $('.delete_btn').click(function () {
        let previewContainer = $(this).closest('.preview_container');
        previewContainer.find('.preview_image').attr('src', '');

        let fileInput = previewContainer.prev('.input_box_wrap').find('input[type="file"]');
        fileInput.val('');
        fileInput.trigger('change');

        previewContainer.hide();
    });
});

// 체크박스 전체 선택
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

// 섬네일 사진1, 섬네일 사진2 유효성 검사 -> 첨부
async function updatePreviewContainer(input, containerId, thumbnailType) {
    let file = input[0].files[0];
    let invalidFileType = false;

    // 파일 선택 취소 시 미리보기 초기화
    if (!file) {
        let container = $('#' + containerId);
        let previewImages = container.find('.preview_images');
        previewImages.find('.preview_image').attr('src', '');
        container.find('.filename_container').empty();
        container.css('display', 'none');
        return;
    }

    let maxSizePerFile = 3 * 1024 * 1024; // 3MB
    let validFileTypes = ['image/jpeg', 'image/png', 'image/jpg'];

    if (!validFileTypes.includes(file.type)) {
        invalidFileType = true;
    }

    if (invalidFileType) {
        Swal.fire({
            text: "JPG, JPEG, PNG 형식의 파일만 첨부 가능합니다.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        input.val('');
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

let dataTransfer = new DataTransfer();
let existingFiles = []; // 기존 파일 목록 관리

// 상세 페이지 사진 유효성 검사 -> 첨부
async function handleDetailImagesChange() {
    let previewContainer = $('#previewContainer3');
    let newFileArr = $('#detailImages')[0].files;
    let newFileSize = newFileArr.length;
    let originalFileSize = dataTransfer.files.length;
    let maxSizePerFile = 3 * 1024 * 1024;
    let validFileTypes = ['image/jpeg', 'image/png', 'image/jpg'];
    let invalidFileType = false;
    let exceedsMaxSize = false;
    let exceedsMaxFiles = originalFileSize + newFileSize;

    // 기존 파일 목록 업데이트
    existingFiles = Array.from($('#previewContainer3 .file_name')).map(fileName => {
        return $(fileName).text().replace('파일명: ', '').trim();
    });

    // 만 파일을 제외한 새로운 파일만 처리
    let filesToAdd = [];
    for (let i = 0; i < newFileSize; i++) {
        let file = newFileArr[i];

        if (file.size > maxSizePerFile) {
            exceedsMaxSize = true;
        }
        if (!validFileTypes.includes(file.type)) {
            invalidFileType = true;
        }

        // 중복 파일 검사
        if (existingFiles.includes(file.name)) {
            Swal.fire({
                text: '이미 첨부된 파일은 제외하고 새로운 파일만 첨부됩니다.',
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
        } else {
            filesToAdd.push(file);
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
            text: "JPG, JPEG, PNG 형식의 파일만 첨부 가능합니다.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        $('#detailImages').val('');
        return false;
    }

    // 새로운 파일 목록을 기존 목록에 추가
    for (let file of filesToAdd) {
        dataTransfer.items.add(file);
        existingFiles.push(file.name); // 기존 파일 리스트에 새 파일 추가
    }

    $('#detailImages')[0].files = dataTransfer.files;

    // 미리보기 생성
    for (let file of filesToAdd) {
        await addImagePreview(previewContainer, file);
    }

    previewContainer.css('display', 'flex');
}

// 상세 이미지 사진 첨부시 뷰 생성
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
            let deleteButton = $('<button>').addClass('delete_btn').append($('<img>').attr('src', '../../../../images/close.svg').addClass('icon'));

            // 기존 파일 배열에 새 파일 추가
            existingFiles.push(file.name);

            deleteButton.click(function () {
                // 기존 파일 목록에서 해당 파일 제거
                existingFiles = existingFiles.filter(existingFile => existingFile !== file.name);

                // dataTransfer 파일 목록에서 해당 파일 제거
                let newFiles = Array.from($('#detailImages')[0].files).filter(f => f.name !== file.name);

                // dataTransfer 객체를 갱신하고 file input에 반영
                let newDataTransfer = new DataTransfer();
                newFiles.forEach(f => newDataTransfer.items.add(f));
                $('#detailImages')[0].files = newDataTransfer.files;

                // 미리보기 이미지 제거
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

// 폼 제출 전 유효성 검사
function validateBeforeSubmit() {
    let nameValue = $('#name').val().trim();
    let categoryValue = $('#category').val();
    let priceValue = $('#price').val().trim();
    let stockQuantityValue = $('#stockQuantity').val().trim();
    let displayImageValue = $('#displayImage')[0].files;
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
    } else if (!isNameChecked) {
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
            text: "0보다 큰 숫자를 입력해 주세요.",
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
            text: "0보다 큰 숫자를 입력해 주세요.",
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

let csrfHeader = $("meta[name='_csrf_header']").attr("content");
let csrfToken = $("meta[name='_csrf']").attr("content");

let isNameChecked = false;
let isAvailableName = false;

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

    $.ajax({
        type: 'GET',
        url: '/api/admin/products/name/exists?name=' + encodeURIComponent(name),
        success: function (resp) {
            isNameChecked = true;
            isAvailableName = resp.available ? true : false;
        },
        error: function (xhr) {
            const resp = xhr.responseJSON;
            let errorMessage = "상품명 중복 확인 중 오류가 발생했습니다.<br>다시 시도해 주세요.";

            if (xhr.status === 400) errorMessage = resp?.message || '잘못된 요청입니다.';
            else if (xhr.status === 403) errorMessage = resp?.message || '접근 권한이 없습니다.';

            Swal.fire({
                html: errorMessage.replace(/\n/g, '<br>'),
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
        }
    });

    return;
}

// 상품 단건 삭제
$('#deleteProductBtn').click(function () {
    let productId = $('#deleteProductBtn').val();
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
                url: '/api/admin/product',
                data: {'productId': productId},
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(csrfHeader, csrfToken)
                },
                success: function (result) {
                    if (result.status == 200) {
                        window.location.href = '/admin/products';
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
});

// 상품 여러건 삭제
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
                    url: '/api/admin/products',
                    async: false,
                    contentType: 'application/json',
                    data: JSON.stringify(productIds),
                    beforeSend: function (xhr) {
                        xhr.setRequestHeader(csrfHeader, csrfToken)
                    },
                    success: function (data) {
                        if (data.status === 200) {
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

function loadChildCategory(parentId) {
    const childCategory = $("#childCategory");
    childCategory.empty().append('<option value="" selected>소분류를 선택해 주세요.</option>');

    $.ajax({
        url: '/api/admin/categories/' + parentId + '/children',
        method: "GET",
        dataType: "json",
        success: function (res) {
            if (res && res.length > 0) {
                res.forEach(function (child) {
                    childCategory.append(`<option value="${child.id}">${child.name}</option>`);
                });
                childCategory.prop("disabled", false);
            } else {
                childCategory.prop("disabled", true);
            }
        },
        error: function (xhr) {
            const resp = xhr.responseJSON;
            let errorMessage = "카테고리 조회 중 오류가 발생했습니다.<br>다시 시도해 주세요.";

            if (xhr.status === 403) errorMessage = resp?.message || '접근 권한이 없습니다.';
            else if (xhr.status === 404) errorMessage = resp?.message || '상위 카테고리가 존재하지 않습니다.';

            Swal.fire({
                html: errorMessage.replace(/\n/g, '<br>'),
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
        }
    });
}
