$(document).ready(function() {

    $('.select_wrap select').on('change', function() {
        if ($(this).val() === '') {
            $(this).removeClass('selected');
        } else {
            $(this).addClass('selected');
        }
    });

    $(document).on('click', function(event) {
        var dropdownMenus = $('.dropdown_menu');

        if ($(event.target).closest('.menu_toggle, .dropdown_menu').length) {
            return;
        }

        dropdownMenus.removeClass('show');
    });

    $('.menu_toggle').click(function () {
        var dropdownMenu = $(this).siblings('.dropdown_menu');
        $('.dropdown_menu').not(dropdownMenu).removeClass('show');
        dropdownMenu.toggleClass('show');
    });

    $('#selectAll').click(function () {
        if ($(this).prop('checked')) {
            $('.product_table tbody input[type="checkbox"]').prop('checked', true);
        } else {
            $('.product_table tbody input[type="checkbox"]').prop('checked', false);
        }
    });

    // 썸네일 사진1 수정
    $('#displayImage').change(async function() {
        await updatePreviewContainer($(this), 'previewContainer1', '섬네일 사진1');
    });

    // 썸네일 사진2 수정
    $('#hoverImage').change(async function() {
        await updatePreviewContainer($(this), 'previewContainer2', '섬네일 사진2');
    });

    // 상세 페이지 사진 변경 시 처리
    $('#detailsImages').change(async function() {
        await handleDetailsImagesChange(this.files);
    });

    // 삭제 버튼 클릭 시 동작
    $('.delete_btn').click(function() {
        let previewContainer = $(this).closest('.preview_container');
        previewContainer.find('.preview_image').attr('src', '');

        let fileInput = previewContainer.prev('.input_box_wrap').find('input[type="file"]');
        fileInput.val('');

        previewContainer.hide();
    });

});

async function handleDetailsImagesChange(files) {
    let previewContainer = $('#previewContainer3');
    previewContainer.find('.preview_image_container').remove();

    if (files.length === 0) {
        previewContainer.css('display', 'none');
        return;
    }

    let totalSize = 0;
    let maxSizePerFile = 1 * 1024 * 1024;

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

async function updatePreviewContainer(input, containerId, thumbnailType) {
    let file = input[0].files[0];

    if (!file) return;

    let maxSizePerFile = 3 * 1024 * 1024; // 3MB

    if (file.size > maxSizePerFile) {
        alert(thumbnailType + `의 크기가 3MB 이하여야 합니다.`);
        input.val('');
        return false;
    }

    let reader = new FileReader();

    return new Promise((resolve, reject) => {
        reader.onload = function(e) {
            let container = $('#' + containerId);
            let previewImages = container.find('.preview_images');
            let previewImage = $('<img>').addClass('preview_image').attr('src', e.target.result);
            let filenameContainer = $('<div>').addClass('filename_container');
            let fileName = $('<div>').addClass('file_name').text('파일명: ' + file.name);

            container.find('.preview_image').remove();
            container.find('.file_name').remove();

            filenameContainer.append(fileName);
            previewImages.append(previewImage);
            container.append(filenameContainer);

            container.css('display', 'flex');
            resolve();
        };

        reader.readAsDataURL(file);
    });
}

async function addImagePreview(container, file) {
    let reader = new FileReader();

    return new Promise((resolve, reject) => {
        reader.onload = function(e) {
            let containerDiv = $('<div>').addClass('preview_image_container');
            let previewImages = $('<div>').addClass('preview_images');
            let previewImage = $('<img>').addClass('preview_image').attr('src', e.target.result);
            let filenameContainer = $('<div>').addClass('filename_container');
            let fileName = $('<span>').addClass('file_name').text('파일명: ' + file.name);
            let deleteButton = $('<button>').addClass('delete_btn').append($('<span>').addClass('material-symbols-outlined').text('close'));

            deleteButton.click(function() {
                containerDiv.remove();
                updateFileCount();
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

function updateFileCount() {
    let fileCount = $('#previewContainer3 .preview_image_container').length;
    $('#detailsImages').prop('files', createFileList());
}

function createFileList() {
    let fileList = new DataTransfer();
    $('#previewContainer3 .preview_image_container .file_name').each(function() {
        let file = new File([], $(this).text());
        fileList.items.add(file);
    });
    return fileList.files;
}
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


    let displayImageValue = $('#displayImage')[0].files;
    let hoverImageValue = $('#hoverImage')[0].files;
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
            $('#displayImage').val('');
            return false;
        }
    }

    for (let i = 0; i < hoverImageValue.length; i++) {
        let fileSize = hoverImageValue[i].size;
        if (fileSize > maxSizePerFile) {
            alert('섬네일 사진2의 크기가 3MB 이하여야 합니다.');
            $('#hoverImage').val('');
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

function deleteOk(productId) {
    if (!confirm('삭제하시면 복구할 수 없습니다. \n정말로 삭제하시겠습니까?')) {
        return false;
    } else {
        window.location.href = '/admin/shop/products/' + productId + "/delete";
        return true;
    }
}

function deleteProducts() {

    let productIds = [];
    let checkboxes = $('input[name=checkbox]:checked');
    let csrfToken = $("meta[name='_csrf']").attr("content");
    let csrfHeader = $("meta[name='_csrf_header']").attr("content");

    if (checkboxes.length == 0) {
        alert('삭제할 상품을 선택해 주세요.');
        return false;
    } else {
        if(!confirm(checkboxes.length + '개 항목을 삭제하시겠습니까?')) {
            return false;
        } else {
            $(checkboxes.each(function (v) {
                let productId = checkboxes[v].id.split('checkbox')[1];
                productIds.push(productId);
            }))

            $.ajax({
                type: 'POST',
                url: '/admin/shop/products/delete',
                data: JSON.stringify(productIds),
                contentType: 'application/json',
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(csrfHeader, csrfToken);
                },
                success: function (result) {
                    window.location.href = '/admin/shop/products';
                }
            })
        }
    }
}
