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
// 이미지 미리보기 함수
function previewImage1(event) {
    let input = event.target;
    let previewContainer = document.getElementById('previewContainer1');
    let fileNameElement = document.getElementById('displayImage1Name');

    // 선택된 파일 가져오기
    let selectedFile = input.files[0];

    if (selectedFile) {
        let reader = new FileReader();

        reader.onload = function(e) {
            // 이미지 미리보기 표시
            let previewImage = document.createElement('img');
            previewImage.src = e.target.result;
            previewImage.style.maxWidth = '200px';
            previewImage.style.maxHeight = '200px';
            previewImage.style.marginBottom = '10px';
            previewContainer.innerHTML = '';
            previewContainer.appendChild(previewImage);


            fileNameElement.textContent = selectedFile.name;
        };

        reader.readAsDataURL(selectedFile);
    }
}
function previewImage2(event) {
    let input = event.target;
    let previewContainer = document.getElementById('previewContainer2');
    let fileNameElement = document.getElementById('displayImage2Name');


    let selectedFile = input.files[0];

    if (selectedFile) {
        let reader = new FileReader();

        reader.onload = function(e) {

            let previewImage = document.createElement('img');
            previewImage.src = e.target.result;
            previewImage.style.maxWidth = '200px';
            previewImage.style.maxHeight = '200px';
            previewImage.style.marginBottom = '10px';
            previewContainer.innerHTML = '';
            previewContainer.appendChild(previewImage);


            fileNameElement.textContent = selectedFile.name;
        };

        reader.readAsDataURL(selectedFile);
    }
}
function previewImage3(event) {
    let input = event.target;
    let previewContainer = document.getElementById('previewContainer3');
    let fileNameElement = document.getElementById('detailImagesFileName');
    
    previewContainer.innerHTML = '';

    // 선택된 파일들을 반복하여 미리보기 표시
    for (let i = 0; i < input.files.length; i++) {
        let selectedFile = input.files[i]; // 선택된 파일 변수에 할당

        let reader = new FileReader();
        reader.onload = function(e) {
            let previewDiv = document.createElement('div');
            previewDiv.className = 'preview_image';
            previewDiv.style.marginBottom = '10px';

            let previewImage = document.createElement('img');
            previewImage.src = e.target.result;
            previewImage.style.maxWidth = '500px';
            previewImage.style.maxHeight = '500px';
            previewDiv.style.marginBottom = '10px';


            let fileNameSpan = document.createElement('span');
            fileNameSpan.textContent = '파일명: ' + selectedFile.name;

            let deleteButton = document.createElement('button');
            deleteButton.textContent = '삭제';
            deleteButton.className = 'delete-button';
            deleteButton.onclick = function() {
                previewDiv.remove();
            };

            previewDiv.appendChild(previewImage);
            previewDiv.appendChild(fileNameSpan);
            previewDiv.appendChild(deleteButton);
            previewContainer.appendChild(previewDiv);
        };
        reader.readAsDataURL(selectedFile);
    }
}

