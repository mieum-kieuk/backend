$(document).ready(function () {
    $('.sortable-list li').each(function () {
        if ($(this).children('ol.sortable-list').length === 0) {
            $(this).append('<ol class="sortable-list"></ol>');
        }
    });

    $('.select_wrap select').on('change', function () {
        if ($(this).val() === '') {
            $(this).removeClass('selected');
        } else {
            $(this).addClass('selected');
        }
    });

    $(document).on('click', '.category-checkbox', function () {
        if ($(this).is(':checked')) {
            $('.category-checkbox').not(this).prop('checked', false);
        }
    });

    $('.add_btn').click(openAddModal);
    $('.close, .cancel_btn').click(closeModal);

    loadCategories();
    initSortable();
});

const maxDepth = 2;
let csrfHeader = $("meta[name='_csrf_header']").attr("content");
let csrfToken = $("meta[name='_csrf']").attr("content");

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
        start: function (event, ui) {
            ui.item.data('oldIndex', ui.item.index());
            ui.item.data('oldParent', ui.item.parent());
            $('.edit-btn, .delete-btn').prop('disabled', true);
        },
        stop: function (event, ui) {
            const item = ui.item;
            const id = item.data('id');
            const newIndex = item.index();

            const newParentLi = item.parent().closest('li');
            const newParentId = newParentLi.length > 0 ? newParentLi.data('id') : null;

            // 최대 깊이 체크
            if (!checkMaxDepth(item)) {
                rollbackPosition(item);
                Swal.fire({
                    text: `카테고리는 최대 ${maxDepth}단계까지만 허용됩니다.`,
                    showConfirmButton: true,
                    confirmButtonText: '확인',
                    customClass: mySwal,
                    buttonsStyling: false
                });
                $('.edit-btn, .delete-btn').prop('disabled', false);
                return;
            }

            // 서버에 이동 정보 전송
            $.ajax({
                url: '/api/admin/categories/move',
                method: 'PATCH',
                contentType: 'application/json; charset=utf-8',
                dataType: 'json',
                data: JSON.stringify(
                    {
                        id: id,
                        newParentId: newParentId,
                        newIndex: newIndex
                    }
                ),
                beforeSend: function(xhr) {
                    xhr.setRequestHeader(csrfHeader, csrfToken);
                }
            }).done(function() {
                refreshUI();
            }).fail(function (xhr) {
                rollbackPosition(item);

                const resp = xhr.responseJSON;
                let message = "카테고리 이동 중 오류가 발생했습니다.\n다시 시도해 주세요.";
                if (xhr.status === 400) message = resp?.message || '잘못된 요청입니다.';
                else if (xhr.status === 403) message = resp?.message || '접근 권한이 없습니다.';
                else if (xhr.status === 404) message = resp?.message || '상위 카테고리가 존재하지 않습니다.';
                else if (xhr.status === 409) message = resp?.message || '중복된 데이터가 존재합니다.';

                Swal.fire({
                    html: (message || '').replace(/\n/g, '<br>'),
                    showConfirmButton: true,
                    confirmButtonText: '확인',
                    customClass: mySwal,
                    buttonsStyling: false
                });
            }).always(function () {
                $('.edit-btn, .delete-btn').prop('disabled', false);
            });
        }
    }).disableSelection();

    function rollbackPosition(item) {
        const oldParent = item.data('oldParent');
        const oldIndex = item.data('oldIndex');
        const anchor = oldParent.children().eq(oldIndex);

        if (anchor.length > 0) {
            item.insertBefore(anchor);
        } else {
            oldParent.append(item);
        }
    }
}

function addClassToCategoryList() {
    $('.list.category li').removeClass('main_category sub_category');
    $('.list.category > ol > li').addClass('main_category');
    $('.list.category > ol > li > ol > li').addClass('sub_category');
}

// 카테고리 목록 렌더링
function renderCategories(categories) {
    if (!categories || categories.length === 0) {
        return '';
    }

    let categoryList = '<ol class="sortable-list">';
    categories.forEach(function (category) {
        categoryList += `<li data-id="${category.id}">
                   <input type="checkbox" class="category-checkbox" data-id="${category.id}" data-name="${category.name}" />
                   <span class="category-name">${category.name}</span>
                   ${renderCategories(category.children)}
                 </li>`;
    });
    categoryList += '</ol>';
    return categoryList;
}

// 카테고리 조회
function loadCategories() {
    $.ajax({
        type: 'GET',
        url: '/api/admin/categories',
        dataType: 'json',
        success: function (resp) {
            let container = $('#sortable');
            let categories = resp;

            if (categories && categories.length > 0) {
                let categoryList = renderCategories(categories);
                container.html(categoryList);
            } else {
                container.html('<p id="noDataMessage">등록된 카테고리가 없습니다.</p>');
            }

            initSortable();
            refreshUI();
        },
        error: function () {
            Swal.fire({
                text: "카테고리 목록을 불러오는 데 실패했습니다.",
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
        }
    });
}

function refreshUI() {
    updateCategoryOptions();
    addClassToCategoryList();
}

// 카테고리 수정
$('.edit_btn').click(function () {
    let checkedItem = $('.category-checkbox:checked');

    if (checkedItem.length !== 1) {
        Swal.fire({
            text: '수정할 카테고리를 선택해 주세요.',
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

    const parentId = $('#parentCategory').val() || null;

    $.ajax({
        type: 'POST',
        url: '/api/admin/categories',
        contentType: 'application/json; charset=utf-8',
        dataType: 'json',
        data: JSON.stringify(
            {
                name: categoryName,
                parentId: parentId
            }
        ),
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken)
        },
        success: function (resp) {
            $('#noDataMessage').remove();

            let newId = resp.id;

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
                rootList.append(newItemHtml);
            }
            initSortable();
            $('#categoryModal').hide();
            $('#addCategoryForm')[0].reset();
            refreshUI();
        },
        error: function (xhr) {
            let resp = xhr.responseJSON;
            let message = "카테고리 추가 중 문제가 발생했습니다.\n다시 시도해 주세요.";
            if (xhr.status === 400) {
                message = resp?.message || '잘못된 요청입니다.';
            } else if (xhr.status === 403) {
                message = resp?.message || '접근 권한이 없습니다.';
            } else if (xhr.status === 404) {
                message = resp?.message || '상위 카테고리가 존재하지 않습니다.';
            } else if (xhr.status === 409) {
                message = resp?.message || '중복된 데이터가 존재합니다.';
            }

            Swal.fire({
                html: message.replace('\n', '<br>'),
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
        type: 'PATCH',
        url: '/api/admin/categories/' + categoryId,
        contentType: 'application/json; charset=utf-8',
        dataType: 'json',
        data: JSON.stringify(
            {
                name: newName
            }
        ),
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken)
        },
        success: function () {
            let checkbox = $(`.category-checkbox[data-id="${categoryId}"]`);
            checkbox.data('name', newName);
            checkbox.siblings('.category-name').text(newName);
            $('#editCategoryModal').hide();
            refreshUI();
        },
        error: function (xhr) {
            let resp = xhr.responseJSON;
            let message = "수정 중 문제가 발생했습니다.\n다시 시도해 주세요.";
            if (xhr.status === 400) {
                message = resp?.message || '잘못된 요청입니다.';
            } else if (xhr.status === 403) {
                message = resp?.message || '접근 권한이 없습니다.';
            } else if (xhr.status === 404) {
                message = resp?.message || '카테고리가 존재하지 않습니다.';
            } else if (xhr.status === 409) {
                message = resp?.message || '중복된 데이터가 존재합니다.';
            }

            Swal.fire({
                html: message.replace('\n', '<br>'),
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
    let checkedItem = $('.category-checkbox:checked');
    if (checkedItem.length === 0) {
        Swal.fire({
            text: "삭제할 카테고리를 선택해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return;
    }

    let categoryId = checkedItem.data('id');
    let categoryName = checkedItem.data('name');
    let categoryLi = checkedItem.closest('li');

    let alertMessage = `'${categoryName}'(을)를 정말 삭제하시겠습니까?`;

    if (categoryLi.find('ol.sortable-list li').length > 0) {
        alertMessage = `'${categoryName}'을(를) 삭제하면 하위 카테고리도<br/>모두 삭제됩니다. 정말 삭제하시겠습니까?`;

    }

    Swal.fire({
        html: alertMessage,
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
                url: '/api/admin/categories/' + categoryId,
                dataType: 'json',
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(csrfHeader, csrfToken)
                },
                success: function () {
                    categoryLi.remove();

                    let categoryContainer = $('#sortable');
                    if (categoryContainer.find('li').length === 0) {
                        let noDataMessageHtml = '<p id="noDataMessage">등록된 카테고리가 없습니다.</p>';
                        categoryContainer.empty().html(noDataMessageHtml);
                    }
                    updateCategoryOptions();
                },
                error: function (xhr) {
                    let resp = xhr.responseJSON;
                    let message = "카테고리 삭제 중 문제가 발생했습니다.\n다시 시도해 주세요.";
                    if (xhr.status === 400) {
                        message = resp?.message || '잘못된 요청입니다.';
                    } else if (xhr.status === 403) {
                        message = resp?.message || '접근 권한이 없습니다.';
                    } else if (xhr.status === 404) {
                        message = resp?.message || '카테고리가 존재하지 않습니다.';
                    }

                    Swal.fire({
                        html: message.replace('\n', '<br>'),
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
    item.children('ol.sortable-list').children('li').each(function () {
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
            text: "카테고리명을 입력해 주세요.",
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
            text: "카테고리명은 한글, 영문, 숫자, 공백만 허용됩니다.",
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

