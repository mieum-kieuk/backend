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
    $('#displayImage1Btn').on('click', function() {
        $('#displayImage1').click();
    });
    $('#displayImage2Btn').on('click', function() {
        $('#displayImage2').click();
    });
    $('#detailsImagesBtn').on('click', function() {
        $('#detailsImages').click();
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
        await handleDetailsImagesChange();
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

const dataTransfer = new DataTransfer();

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
        alert('상세 페이지 사진 한 장의 크기가 3MB 이하여야 합니다.');
        $('#detailsImages').val('');
        return false;
    }

    if (exceedsMaxFiles) {
        alert('상세 페이지 사진은 최대 20장까지 가능합니다.');
        $('#detailsImages').val('');
        return false;
    }

    if(newFileArr != null && newFileSize > 0){
        for(let i = 0; i < newFileSize; i++){
            dataTransfer.items.add(newFileArr[i])
        }
        $('#detailsImages')[0].files = dataTransfer.files;
    }

    console.log($('#detailsImages')[0].files);

    for (let i = 0; i < newFileSize; i++) {
        await addImagePreview(previewContainer, newFileArr[i]);
    }

    previewContainer.css('display', 'flex');
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


    let displayImageValue = $('#displayImage1')[0].files;
    let hoverImageValue = $('#displayImage2')[0].files;
    let detailsImagesValue = $('#detailsImages')[0].files;

    let nameRegex = /^[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9\s]+$/;
    if(nameValue === '') {
        alert('상품명을 입력해 주세요.');
        return false;
    } else if (!nameRegex.test(nameValue)) {
        alert('상품명은 한글, 영문, 숫자, 공백만 허용됩니다.');
        return false;
    } else if(!isAvailableName) {
        alert('이미 존재하는 상품명입니다.');
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
        url: '/ajax/admin/products/check/name',
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
            isAvailableName = false;
        }
    });

    return;
}

//상품 단건 삭제
function deleteProduct(productId) {

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