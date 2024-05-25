$(document).ready(function() {


     $('#submitBtn').click(function () {

         if (!validateBeforeSubmit()) {
             return false;
         } else {
             $('#addProductForm').submit();
         }
     });

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

         let maxSizePerFile = 1 * 1024 * 1024;
         for (let i = 0; i < displayImageValue.length; i++) {
             let fileSize = displayImageValue[i].size;
             if (fileSize > maxSizePerFile) {
                 alert('섬네일 사진1의 크기가 1MB 이하여야 합니다.');
                 return false;
             }
         }

         for (let i = 0; i < hoverImageValue.length; i++) {
             let fileSize = hoverImageValue[i].size;
             if (fileSize > maxSizePerFile) {
                 alert('섬네일 사진2의 크기가 1MB 이하여야 합니다.');
                 return false;
             }
         }
         for (let i = 0; i < detailsImagesValue.length; i++) {
             let fileSize = detailsImagesValue[i].size;
             if (fileSize > maxSizePerFile) {
                 alert('첨부파일 하나의 크기가 1MB 이하여야 합니다.');
                 return false;
             }
         }

         let totalSizeLimit = 20 * 1024 * 1024;
         let totalSize = 0;
         for (let i = 0; i < detailsImagesValue.length; i++) {
             totalSize += detailsImagesValue[i].size;
         }
         if (totalSize > totalSizeLimit) {
             alert('첨부파일 전체의 크기가 20MB 이하여야 합니다.');
             return false;
         }

         return true;
     }
 });

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
$('.delete_btn').click(function() {
    let previewContainer = $(this).closest('.preview_container');

    previewContainer.find('.preview_image').attr('src', '');

    let fileInput = previewContainer.prev('.input_box_wrap').find('input[type="file"]');
    fileInput.val('');

    previewContainer.hide();
});

// displayImage 변경 이벤트 핸들러
$('#displayImage').change(function() {
    updatePreviewContainer($(this), 'previewContainer1');

});

// hoverImage 변경 이벤트 핸들러
$('#hoverImage').change(function() {
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
