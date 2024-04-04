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
            alert(message);
        }
    }).done(alert('섬네일 사진1이 삭제되었습니다.'));
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
            alert(message);
        }
    }).done(alert('섬네일 사진2이 삭제되었습니다.'));
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
            alert(message);
        }
    }).done(alert('상세 페이지 첨부파일이 삭제되었습니다.'));
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