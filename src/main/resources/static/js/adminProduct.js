$(document).ready(function() {

    //초기화 버튼
    $('.btn_wrap .reset_btn').click(function () {
        $('#searchKeyword').val(''); // 검색어 입력 초기화
        $('select').each(function() {
            $(this).val($(this).find('option:first').val()); // select 요소 초기화
        });

    });

    //등록, 수정: 셀렉트 박스 css
    $('.select_wrap select').on('change', function() {
        if ($(this).val() === '') {
            $(this).removeClass('selected');
        } else {
            $(this).addClass('selected');
        }
    });

    //목록: 체크박스 전체 선택
    $('#selectAll').click(function () {
        if ($(this).prop('checked')) {
            $('tbody input[type="checkbox"]').prop('checked', true);
        } else {
            $('tbody input[type="checkbox"]').prop('checked', false);
        }
    });

    //등록, 수정: 섬네일 사진1 첨부
    $('#displayImage1').change(async function() {
        await updatePreviewContainer($(this), 'previewContainer1', '섬네일 사진1');
    });

    //등록, 수정: 섬네일 사진2 첨부
    $('#displayImage2').change(async function() {
        await updatePreviewContainer($(this), 'previewContainer2', '섬네일 사진2');
    });

    //등록, 수정: 상세 페이지 사진 첨부
    $('#detailsImages').change(async function() {
        await handleDetailsImagesChange(this.files);
    });

    //등록, 수정: X(삭제) 클릭 시 첨부된 이미지 삭제
    $('.delete_btn').click(function() {
        let previewContainer = $(this).closest('.preview_container');
        previewContainer.find('.preview_image').attr('src', '');

        let fileInput = previewContainer.prev('.input_box_wrap').find('input[type="file"]');
        fileInput.val('');

        previewContainer.hide();
    });
});

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

//등록, 수정: 상세 페이지 사진 유효성 검사 -> 첨부
async function handleDetailsImagesChange(files) {
    let previewContainer = $('#previewContainer3');
    previewContainer.find('.preview_image_container').remove();

    if (files.length === 0) {
        previewContainer.css('display', 'none');
        return;
    }

    let totalSize = 0;
    let maxSizePerFile = 3 * 1024 * 1024;

    let exceedsMaxSize = false;
    let exceedsMaxFiles = files.length > 20;

    for (let i = 0; i < files.length; i++) {
        let fileSize = files[i].size;
        totalSize += fileSize;

        if (fileSize > maxSizePerFile) {
            exceedsMaxSize = true;
        }
    }

    if (exceedsMaxSize) {
        alert('상세 페이지 사진 한 장의 크기가 3MB 이하여야 합니다.');
        $('#detailsImages').val('');
        return false;
    }

    if (exceedsMaxFiles) {
        alert('상세 페이지 사진은 최대 20장까지 가능합니다.');
        $('#detailsImages').val('');
        return false;
    }

    for (let i = 0; i < files.length; i++) {
        await addImagePreview(previewContainer, files[i]);
    }

    previewContainer.css('display', 'flex');
}

//수정: 섬네일 사진 1 조회
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

//수정: 섬네일 사진 2 조회
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

//수정: 상세 페이지 사진
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

//수정: 섬네일 사진1 삭제
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
            alert(message);
        }
    }).done(alert('섬네일 사진1이 삭제되었습니다.'));
}

//수정: 섬네일 사진2 삭제
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
            alert(message);
        }
    }).done(alert('섬네일 사진2이 삭제되었습니다.'));
}

//수정: 상세 페이지 사진 삭제
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
            alert(message);
        }
    }).done(alert('상세 페이지 첨부파일이 삭제되었습니다.'));
}


//등록, 수정: 상세 이미지 사진 첨부시 뷰 생성
async function addImagePreview(container, file) {
    let reader = new FileReader();

    return new Promise((resolve) => {
        reader.onload = function(e) {
            let containerDiv = $('<div>').addClass('preview_image_container');
            let previewImages = $('<div>').addClass('preview_images');
            let previewImage = $('<img>').addClass('preview_image').attr('src', e.target.result);
            let filenameContainer = $('<div>').addClass('filename_container');
            let fileName = $('<span>').addClass('file_name').text('파일명: ' + file.name);
            let deleteButton = $('<button>').addClass('delete_btn').append($('<span>').addClass('material-symbols-outlined').text('close'));

            deleteButton.click(function() {
                let deleteFilename = $(this).parent().siblings('.file_name').text().split(": ")[1];
                containerDiv.remove();
                updateFileCount(deleteFilename);
            });

            filenameContainer.append($('<span>').text('파일명: '), fileName);
            containerDiv.append(previewImages.append(previewImage), fileName, $('<div>').addClass('btn_wrap').append(deleteButton));
            container.append(containerDiv);

            updateFileCount();
            resolve();
        };

        reader.readAsDataURL(file);
    });
}

//상세이미지 파일 개수 수정
function updateFileCount(deleteFilename) {
    $('#detailsImages').prop('files', createFileList(deleteFilename));
}

//사용자가 선택하는 순서대로 상세이미지 첨부
function createFileList(deleteFilename) {
    let fileList = new DataTransfer();
    let files = $('#detailsImages')[0].files;
    for (let i = 0; i < files.length; i++) {
        if(files[i].name != deleteFilename) {
            fileList.items.add(files[i]);
        }
    }

    return fileList.files;
}

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


    let displayImageValue = $('#displayImage1')[0].files;
    let hoverImageValue = $('#displayImage2')[0].files;
    let detailsImagesValue = $('#detailsImages')[0].files;


    let nameRegex = /^[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9\s]+$/;
    if (nameValue === '') {
        alert('상품명을 입력해 주세요.');
        return false;
    }
    if (!nameRegex.test(nameValue)) {
        alert('상품명은 한글, 영문, 숫자, 공백만 허용됩니다.');
        return false;
    }

    if (categoryValue === '') {
        alert('카테고리를 선택해 주세요.');
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

//상품 단건 삭제
function deleteProduct(productId) {

    let csrfHeader = $("meta[name='_csrf_header']").attr("content");
    let csrfToken = $("meta[name='_csrf']").attr("content");

    if (confirm('삭제하시겠습니까?')) {
        $.ajax({
            type: 'DELETE',
            url: '/ajax/admin/product/delete',
            async: false,
            data: {productId: productId},
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken)
            },
            success: function (result) {
                if(result.code == 200) {
                    window.location.href = '/admin/products';
                } else {
                    alert(result.message);
                }
            },
            error: function () {
                alert('삭제 중 오류가 발생했습니다. 다시 시도해 주세요.');
            }
        })
    } else {
       return false;
    }
}

//상품 여러건 삭제
$('#deleteProductsBtn').click(function () {

    let productIds = [];
    let checkboxes = $('input[name=checkbox]:checked');
    let csrfHeader = $("meta[name='_csrf_header']").attr("content");
    let csrfToken = $("meta[name='_csrf']").attr("content");

    if (checkboxes.length == 0) {
        alert('삭제할 상품들을 선택해 주세요.');
        return false;
    } else {
        if (confirm(checkboxes.length + '개 항목을 삭제하시겠습니까?')) {
            $(checkboxes.each(function (v) {
                let productId = checkboxes[v].id.split('checkbox')[1];
                productIds.push(productId);
            }));

            $.ajax({
                type: 'DELETE',
                url: '/ajax/admin/products/delete',
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
                        alert(data.message);
                    }
                },
                error: function () {
                    alert('삭제중 오류가 발생했습니다. 다시 시도해 주세요.');
                }
            })
        } else {
            return false;
        }
    }
});