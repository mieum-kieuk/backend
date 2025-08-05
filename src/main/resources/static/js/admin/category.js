$(document).ready(function () {
    $('.sortable-list li').each(function () {
        if ($(this).children('ol.sortable-list').length === 0) {
            $(this).append('<ol class="sortable-list"></ol>');
        }
    });

    $('.add_btn').click(openAddModal);
    $('.close, .cancel_btn').click(closeModal);

    initialTree = getCategoryList($('.list.category > ol.sortable-list'));

    initSortable();
    bindSortStopEvent();
    addClassToCategoryList();
    updateSaveBtnState();
});

const maxDepth = 2;

// sortable 초기화
function initSortable() {
    $('.sortable-list').sortable({
        connectWith: '.sortable-list',
        placeholder: 'sortable-placeholder',
        items: '> li',
        cursor: 'move',
        opacity: 0.7,
        tolerance: 'pointer',
        revert: 100,
        stop: function (event, ui) {
            let parentList = ui.item.parent();

            // 부모 리스트가 sortable-list가 아닐 때
            if (!parentList.hasClass('sortable-list')) {
                let parentLi = parentList.closest('li');
                if (parentLi.length) {
                    if (parentLi.children('ol.sortable-list').length === 0) {
                        parentLi.append('<ol class="sortable-list"></ol>');
                    }
                    ui.item.appendTo(parentLi.children('ol.sortable-list'));
                }
            }

            if (!checkMaxDepth(ui.item)) {
                $(this).sortable('cancel');
                Swal.fire({
                    text: `카테고리는 최대 ${maxDepth}단계까지만 허용됩니다.`,
                    showConfirmButton: true,
                    confirmButtonText: '확인',
                    customClass: mySwal,
                    buttonsStyling: false
                });
            }
            updateSaveBtnState();
        }
    }).disableSelection();
}

function addClassToCategoryList() {
    $('.list.category li').removeClass('main_category sub_category');
    $('.list.category > ol > li').addClass('main_category');
    $('.list.category > ol > li > ol > li').addClass('sub_category');
}

// 카테고리 수정
$('.edit_btn').click(function () {
    let checkedItem = $('.category-checkbox:checked');

    if (checkedItem.length !== 1) {
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

// 카테고리 추가
$('#addCategoryForm').submit(function (event) {
    event.preventDefault();

    let categoryName = $('#newCategoryName').val();
    if (!validateBeforeSubmit(categoryName)) return;

    let parentId = $('#parentCategory').val();

    $.ajax({
        type: 'POST',
        url: '',
        data: {
            name: categoryName,
            parent_id: parentId || null
        },
        success: function (response) {
            // let newId = response.id;
            let newId = response?.id || 'temp_' + Date.now();  // 예: temp_1691351351351

            let newItemHtml = `
                    <li data-id="${newId}">
                      <input type="checkbox" class="category-checkbox" data-id="${newId}" data-name="${categoryName}" />
                      <span class="category-name">${categoryName}</span>
                      <ol class="sortable-list ui-sortable"></ol>
                    </li>`;

            if (parentId) {
                let parentItem = $('#sortable').find(`li[data-id="${parentId}"]`);
                let sublist = parentItem.children('ol.sortable-list');

                if (sublist.length === 0) {
                    parentItem.append('<ol class="sortable-list"></ol>');
                    sublist = parentItem.children('ol.sortable-list');
                }

                sublist.append(newItemHtml);
            } else {
                let rootList = $('#sortable > ol.sortable-list');
                if (rootList.length === 0) {
                    $('#sortable').append('<ol class="sortable-list"></ol>');
                    rootList = $('#sortable > ol.sortable-list');
                }
                rootList.append(newItemHtml);                }
            initSortable();
            $('#categoryModal').hide();
            $('#addCategoryForm')[0].reset();

            updateCategoryOptions();
            bindSortStopEvent();
            addClassToCategoryList();
            updateSaveBtnState();
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
        data: { id: categoryId, name: newName },
        success: function () {
            let checkbox = $(`.category-checkbox[data-id="${categoryId}"]`);
            checkbox.data('name', newName);
            checkbox.siblings('.category-name').text(newName);
            $('#editCategoryModal').hide();
            updateCategoryOptions();
            addClassToCategoryList();
            updateSaveBtnState();
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
    $('#categoryList .category-checkbox').prop('checked', false);

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
                url: '',
                data: { ids },
                success: function () {
                    checkedItems.each(function () {
                        $(this).closest('li').remove();
                    });

                    updateCategoryOptions();
                    bindSortStopEvent();
                    updateSaveBtnState();
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

function getMaxSubtreeDepth(item) {
    let maxDepth = 0;
    // li 요소 바로 아래의 ol > li 찾기
    item.children('ol.sortable-list').children('li').each(function() {
        // 자식의 깊이 = 1 (자신) + 그 자식의 하위 트리 깊이
        const currentDepth = 1 + getMaxSubtreeDepth($(this));
        // 가장 깊은 값을 유지합니다.
        if (currentDepth > maxDepth) {
            maxDepth = currentDepth;
        }
    });
    return maxDepth;
}

function checkMaxDepth(draggedItem) {
    let currentDepth = 1;
    let parent = draggedItem.parent().closest('li');
    while (parent.length > 0) {
        currentDepth++;
        parent = parent.parent().closest('li');
    }

    // 2. 드래그된 항목이 가진 자식들의 최대 상대 깊이를 계산합니다.
    let subtreeDepth = getMaxSubtreeDepth(draggedItem);

    // 3. 최종 깊이 = 기본 깊이 + 자식의 최대 깊이
    let totalDepth = currentDepth + subtreeDepth;

    return totalDepth <= maxDepth;
}

// 상위 카테고리 옵션 업데이트
function updateCategoryOptions() {
    let parentCategory = $('#parentCategory');
    parentCategory.empty().append('<option value="">(대분류로 추가)</option>');

    $('.category-checkbox').each(function () {
        let id = $(this).data('id');
        let name = $(this).data('name');
        let item = $(this).closest('li');

        let depth = 1;
        let parent = item.parent().closest('li');
        while (parent.length) {
            depth++;
            parent = parent.parent().closest('li');
        }

        if (depth < maxDepth) {
            parentCategory.append(`<option value="${id}">${name}</option>`);
        }
    });
}

// 모달 열기 함수
function openAddModal() {
    $('#addCategoryForm')[0].reset();
    $('#editCategoryId').val('');
    updateCategoryOptions();
    $('#categoryModal').css('display', 'flex');
}

function openEditModal(id, name) {
    $('#editCategoryId').val(id);
    $('#editCategoryName').val(name);
    $('#editCategoryModal').css('display', 'flex');
}
// 모달 닫기 함수
function closeModal() {
    $('#categoryModal').hide();
    $('#editCategoryModal').hide();
    $('#addCategoryForm')[0].reset();
}

// 유효성 검사
function validateBeforeSubmit(categoryName) {
    let validName = categoryName.trim();
    if (!validName) {
        Swal.fire({
            text: "카테고리 이름을 입력해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }

    let regex = /^[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9\s]+$/;
    if (!regex.test(validName)) {
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

function getCategoryList(list) {
    let result = [];

    $(list).children('li').each(function (index) {
        let item = $(this);
        let id = item.data('id');
        let name = item.find('.category-name').text();

        let children = getCategoryList(item.children('ol.sortable-list'));

        result.push({
            id,
            name,
            order: index + 1,
            children: children.length ? children : null
        });
    });

    return result;
}

// 카테고리 변경 감지
function bindSortStopEvent() {
    $('.sortable-list').off('sortstop').on('sortstop', function () {
        updateSaveBtnState();
        addClassToCategoryList();
    });
}

function updateSaveBtnState() {
    let currentTree = getCategoryList($('.list.category > ol.sortable-list'));
    let changed = JSON.stringify(currentTree) !== JSON.stringify(initialTree);

    $('#saveCategoryBtn').prop('disabled', !changed);
    if (changed) {
        $('#saveCategoryBtn').removeClass('disabled');
    } else {
        $('#saveCategoryBtn').addClass('disabled');
    }
}

// 저장 버튼 눌렀을 때
$('#saveCategoryBtn').on('click', function () {
    let categoryList = getCategoryList($('.list.category > ol.sortable-list'));

    $.ajax({
        type: 'POST',
        url: '',
        data: JSON.stringify(categoryList),
        contentType: 'application/json',
        success: function () {
            Swal.fire({
                text: '카테고리가 저장되었습니다.',
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });

            initialTree = getCategoryList($('.list.category > ol.sortable-list'));
            updateSaveBtnState();        }
    });
});
