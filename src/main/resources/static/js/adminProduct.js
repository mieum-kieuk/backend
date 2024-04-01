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

function addImagePreview(container, file) {
    let reader = new FileReader();

    reader.onload = function(e) {

        let containerDiv = $('<div>').addClass('preview_image_container');
        let previewImages = $('<div>').addClass('preview_images');
        let previewImage = $('<img>').addClass('preview_image').attr('src', e.target.result);
        let fileName = $('<span>').addClass('file_name').text('파일명: ' + file.name);
        let deleteButton = $('<button>').addClass('delete_btn').append($('<span>').addClass('material-symbols-outlined').text('close'));

        deleteButton.click(function() {
            containerDiv.remove();
        });

        containerDiv.append(previewImages.append(previewImage), fileName, $('<div>').addClass('btn_wrap').append(deleteButton));
        container.append(containerDiv);
    };

    reader.readAsDataURL(file);
}

// displayImage1 변경 이벤트 핸들러
$('#displayImage1').change(function() {
    updatePreviewContainer($(this), 'previewContainer1');
});

// displayImage2 변경 이벤트 핸들러
$('#displayImage2').change(function() {
    updatePreviewContainer($(this), 'previewContainer2');
});

// detailsImages 변경 이벤트 핸들러
$('#detailsImages').change(function() {
    let files = this.files;
    let previewContainer = $('#previewContainer3');

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

$(document).ready(function () {
    $('.menu_toggle').click(function () {
        var dropdownMenu = $(this).siblings('.dropdown_menu');
        $('.dropdown_menu').not(dropdownMenu).removeClass('show');
        dropdownMenu.toggleClass('show');
    });

    $('#selectAll').click(function () {
        if ($(this).prop('checked')) {
            $('.discount_table tbody input[type="checkbox"]').prop('checked', true);
        } else {
            $('.discount_table tbody input[type="checkbox"]').prop('checked', false);
        }
    });
});


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
                success: function (result) {
                    window.location.href = '/admin/shop/products';
                }
            })
        }
    }
}