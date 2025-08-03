$(document).ready(function () {
    // Nestable 초기화
    $('.dd').nestable({
        maxDepth: 3,
        handleClass: 'dd-handle'
    });

    let categoryModal = $('#categoryModal');
    let editCategoryModal = $('#editCategoryModal');
    let categoryForm = $('#addCategoryForm');
    let editCategoryId = $('#editCategoryId');
    let newCategoryName = $('#newCategoryName');
    let parentCategory = $('#parentCategory');

    // 상위 카테고리 옵션 업데이트
    function updateCategoryOptions() {
        parentCategory.empty().append(`<option value="">(대분류 추가)</option>`);
        $('.category-checkbox').each(function () {
            let id = $(this).data('id');
            let name = $(this).data('name');
            let item = $(this).closest('.dd-item');
            let depth = getDepth(item);

            // 최대 2단계까지만 상위 카테고리로 선택 가능
            if (depth < 3) {
                parentCategory.append(`<option value="${id}">${name}</option>`);
            }
        });
    }

    // 카테고리 depth 확인
    function getDepth(item) {
        let depth = 1;
        while (item.closest('.dd-list').closest('.dd-item').length) {
            item = item.closest('.dd-list').closest('.dd-item');
            depth++;
        }
        return depth;
    }

    // 모달 열기
    function openAddModal() {
        categoryForm[0].reset();
        editCategoryId.val('');
        updateCategoryOptions();
        categoryModal.css('display', 'flex');
    }

    function openEditModal(id, name) {
        $('#editCategoryId').val(id);
        $('#editCategoryName').val(name);
        editCategoryModal.css('display', 'flex');
    }

    // 모달 닫기
    function closeModal() {
        categoryModal.hide();
        editCategoryModal.hide();
        categoryForm[0].reset();
    }

    $('.add_btn').click(openAddModal);
    $('.close, .cancel_btn').click(closeModal);

    // 카테고리 수정
    $('.edit_btn').click(function () {
        let checkedItem = $('.category-checkbox:checked');

        if (checkedItem.length === 0 || checkedItem.length > 1) {
            Swal.fire({
                text: '수정할 카테고리를 1개만 선택해 주세요.',
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
            return;
        }

        let categoryId = checkedItem.data('id');
        let categoryName = checkedItem.data('name');
        openEditModal(categoryId, categoryName);
    });

    // 카테고리 삭제
    $('.delete_btn').click(function () {
        let checkedItems = $('.category-checkbox:checked');
        if (checkedItems.length === 0) {
            Swal.fire({
                text: "삭제할 카테고리를 선택해 주세요.",
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
            return;
        }

        Swal.fire({
            text: `선택된 ${checkedItems.length}개의 카테고리를 정말 삭제하시겠습니까?`,
            showCancelButton: true,
            cancelButtonText: '아니요',
            confirmButtonText: '예',
            customClass: mySwalConfirm,
            reverseButtons: true,
            buttonsStyling: false,
        }).then((result) => {
            if (result.isConfirmed) {
                let ids = checkedItems.map(function () {
                    return $(this).data('id');
                }).get();

                $.ajax({
                    type: 'POST',
                    url: '', // TODO: 삭제 API 주소 입력
                    data: {ids},
                    success: function () {
                        checkedItems.each(function () {
                            $(this).closest('.dd-item').remove();
                        });
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

    // 유효성 검사 함수
    function validateBeforeSubmit(categoryName) {
        let validCategoryName = categoryName.trim();
        if (!validCategoryName) {
            Swal.fire({
                text: "카테고리 이름을 입력해 주세요.",
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });

            return false;
        }
        let nameRegex = /^[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9\s]+$/;
        if (!nameRegex.test(validCategoryName)) {
            Swal.fire({
                text: "카테고리 이름은 한글, 영문, 숫자, 공백만 허용됩니다.",
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
            return false;
        }
        return true;
    }

    // 카테고리 등록
    $('#addCategoryForm').submit(function (event) {
        event.preventDefault();
        let categoryName = newCategoryName.val();
        if (!validateBeforeSubmit(categoryName)) return;

        let parentId = parentCategory.val();
        $.ajax({
            type: 'POST',
            url: '',
            data: {
                name: categoryName,
                parent_id: parentId || null
            },
            success: function (response) {
                let newId = response.id;
                let newItemHtml = `
                <li class="dd-item" data-id="${newId}">
                    <div class="dd-content">
                        <input type="checkbox" class="category-checkbox" data-id="${newId}" data-name="${categoryName}" />
                        <div class="dd-handle"><span>${categoryName}</span></div>
                    </div>
                </li>`;

                if (parentId) {
                    let parentItem = $(`.dd-item[data-id="${parentId}"]`);
                    let sublist = parentItem.children('.dd-list');
                    if (sublist.length === 0) {
                        parentItem.append('<ol class="dd-list"></ol>');
                        sublist = parentItem.children('.dd-list');
                    }
                    sublist.append(newItemHtml);
                } else {
                    $('.dd > .dd-list').append(newItemHtml);
                }

                categoryModal.hide();
                categoryForm[0].reset();
            },
            error: function () {
                Swal.fire({
                    html: "카테고리 추가 중 문제가 발생했습니다.<br>다시 시도해 주세요.",
                    showConfirmButton: true,
                    confirmButtonText: '확인',
                    customClass: mySwal,
                    buttonsStyling: false
                });
            }
        });
    });

    // 카테고리 수정
    $('#editCategoryForm').submit(function (event) {
        event.preventDefault();

        let categoryId = $('#editCategoryId').val();
        let newName = $('#editCategoryName').val();
        if (!validateBeforeSubmit(newName)) return;

        $.ajax({
            type: 'POST',
            url: '',
            data: {id: categoryId, name: newName},
            success: function () {
                let checkbox = $(`.category-checkbox[data-id="${categoryId}"]`);
                checkbox.data('name', newName);
                checkbox.closest('.dd-item').find('.dd-handle span').text(newName);
                editCategoryModal.hide();
            },
            error: function () {
                Swal.fire({
                    html: "수정 중 문제가 발생했습니다.<br>다시 시도해 주세요.",
                    showConfirmButton: true,
                    confirmButtonText: '확인',
                    customClass: mySwal,
                    buttonsStyling: false
                });
            }
        });
    });
});
