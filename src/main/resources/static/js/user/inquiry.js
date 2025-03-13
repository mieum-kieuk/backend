$(document).ready(function () {

    let productId = $('#product').data('id');

    popupButton();
    loadInquiries(productId, 1);

    $('.delete_btn').on('click', function () {
        let inquiryId = $(this).data('id');
        deleteInquiry(productId, inquiryId);
    });

    if ($('.qna_list #noDataMessage').length > 0) {
        $('.footer').addClass('fixed');
    }

    $(document).on("click", ".qna_items", function () {
        let currentContent = $(this).next(".qna_content");
        toggleContent(currentContent);
    });

    // 수정 버튼
    $(document).on("click", "#product .qna_table .edit_btn", function () {
        editInquiryModal(this); // 클릭된 버튼을 전달
    });

    $('#inquiryModal .close_btn').on('click', function () {
        closeInquiryModal();
    });

    // 삭제 버튼
    $(document).on("click", ".delete_btn", function () {
        let inquiryItem = $(this).closest(".qna_content").prev(".qna_items");
        let inquiryContent = inquiryItem.next(".qna_content");
        let inquiryId = $(this).data("id");

        deleteProductInquiry(inquiryId, inquiryItem, inquiryContent)
    });

    // 모달 닫기 버튼
    $('.close_btn').on('click', function () {
        $('#inquiryModal').hide();

        // 모달이 닫히면 qna_content 원래 상태로 복원
        let qnaContent = $('#inquiryModal').closest('.qna_content');
        qnaContent.find('.qna_cont').css({
            visibility: "visible",
            position: "relative"
        });

        // 첫 번째 tr의 border-top을 원래대로 되돌리기
        $('#product .qna_wrap.list .qna_table tbody tr:first-child').css({
            'border-top': 'none'
        });
    });

    $(document).on('click', '.page', function () {
        let currentPage = $(this).data('page');
        $('.page').removeClass('active');  // 기존의 .active 클래스 제거
        $(this).addClass('active');  // 클릭된 페이지에 .active 클래스 추가
        loadInquiries(currentPage);
    });

    $(document).on('click', '.prev_first, .next_last', function () {
        let currentPage = $(this).data('page');
        loadInquiries(currentPage);
    });
});

function loadInquiries(productId, currentPage) {
    $.ajax({
        type: 'GET',
        url: '/ajax/inquiries/' + productId,
        success: function (data) {
            renderPagination(data.totalElements, currentPage);
            let totalCount = $('.totalCount');
            totalCount.text(data.totalElements);
            if (data && data.content.length > 0) {
                let inquiryTable = $('#inquiryTable');
                let tbody = inquiryTable.find('tbody'); // tbody 요소 찾기

                if (tbody.length === 0) { // tbody 요소가 없으면 생성
                    tbody = $('<tbody></tbody>');
                    inquiryTable.append(tbody);
                } else {
                    tbody.empty(); // tbody 요소가 있으면 초기화
                }

                data.content.forEach(function (item) {
                    let inquiryItem = `
                        <tr class="qna_items">
                            <td class="title_td">
                                <div class="title">
                                    ${item.isSecret ? '<span class="material-symbols-outlined">lock</span>' : ''}
                                    <span class="title">${item.title}</span>
                                </div>
                            </td>
                            <td><span class="writer">${item.writer}</span></td>
                            <td><span class="date">${item.createdAt}</span></td>
                            <td><span class="answer">${item.isAnswered}</span></td>
                        </tr>
                        <tr class="qna_content">
                            <td colspan="4">
                                <div class="qna_cont" id="content">
                                    <div class="qna_question">
                                        <div class="qna_text">
                                            <span>Q</span>
                                            <span class="question">${item.title}</span>
                                        </div>
                                        <div class="menu">
                                            <button class="menu_toggle">
                                                <span class="material-symbols-outlined">more_horiz</span>
                                            </button>
                                            <div class="dropdown_menu">
                                                <ul>
                                                    <li>
                                                        <button type="button" class="edit_btn" data-id="${item.id}">수정</button>
                                                    </li>
                                                    <li>
                                                        <button type="button" class="delete_btn" data-id="${item.id}">삭제</button>
                                                    </li>
                                                </ul>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="qna_answer">
                                        <span>A</span>
                                        <span class="answer">${item.answer || '답변이 없습니다.'}</span>
                                    </div>
                                </div>
                            </td>
                        </tr>
                    `;
                    tbody.append(inquiryItem);
                });
            } else {
                let noDataMessage = `
                    <tr id="noDataMessage">
                        <td colspan="4">등록된 상품문의가 없습니다.</td>
                    </tr>
                `;
                tbody.append(noDataMessage);
            }
        },
        error: function () {
            Swal.fire({
                text: "상품 문의를 불러오는 데 실패했습니다.",
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
        }
    });
}

// 페이지네이션 렌더링
function renderPagination(totalElements, currentPage) {
    let paginationSize = 5; // 한 페이지네이션에 보여줄 페이지 번호 수
    const totalPages = Math.ceil(totalElements / 10);  // 총 페이지 수

    if (totalPages === 0) {
        $('#pagination').empty();
        return;
    }

    let pagination = $('#pagination');
    pagination.empty(); // 기존 페이지네이션 초기화

    let totalGroups = Math.ceil(totalPages / paginationSize); // 페이지 그룹 수

    // 현재 페이지가 속한 그룹 계산
    let currentGroup = Math.floor((currentPage - 1) / paginationSize) + 1;

    // 이전 페이지 버튼
    if (currentGroup > 1) {
        let prevPage = (currentGroup - 1) * paginationSize;
        pagination.append(`
        <li><a class="prev_first" data-page="${prevPage}">
            <span class="material-symbols-outlined">navigate_before</span>
        </a></li>
    `);
    } else {
        pagination.append(`
        <li><a class="prev_first disabled"><span class="material-symbols-outlined">navigate_before</span></a></li>
    `);
    }

    let startPage = (currentGroup - 1) * paginationSize + 1;  // 그룹 내 첫 번째 페이지
    let endPage = Math.min(currentGroup * paginationSize, totalPages);  // 그룹 내 마지막 페이지

    for (let i = startPage; i <= endPage; i++) {
        let isActive = (currentPage === i) ? 'active' : '';

        pagination.append(`
            <li><a class="page ${isActive}" data-page="${i}">${i}</a></li>
        `);
    }

    // 다음 페이지 버튼
    if (currentGroup < totalGroups) {
        let nextPage = (currentGroup * paginationSize) + 1;
        pagination.append(`
        <li><a class="next_last" data-page="${nextPage}">
            <span class="material-symbols-outlined">navigate_next</span>
        </a></li>
    `);
    } else {
        pagination.append(`
        <li><a class="next_last disabled"><span class="material-symbols-outlined">navigate_next</span></a></li>
    `);
    }
}

// 상품문의 폼 열기
function openInquiryModal() {
    let inquiryModal = $("#inquiryModal");
    inquiryModal.css("display", "flex");
}

function closeInquiryModal() {
    $('#inquiryModal').hide();

    // qna_content 원래 상태로 복원
    let qnaContent = $('#inquiryModal').closest('.qna_content');
    qnaContent.find('.qna_cont').css({
        visibility: "visible",
        position: "relative"
    });

    // 첫 번째 tr의 border-top을 원래대로 되돌리기
    $('#product .qna_wrap.list .qna_table tbody tr:first-child').css({
        'border-top': 'none'
    });
}

// 상품문의 수정 폼
function editInquiryModal(editButton) {
    let inquiryModal = $('#inquiryModal');

    let inquiryTitle = $(editButton).closest('.qna_cont').find('.question').text().trim();
    let inquiryContent = $(editButton).closest('.qna_cont').find('.answer').text().trim();

    $('#inquiryModal #title').val(inquiryTitle);
    $('#inquiryModal #content').text(inquiryContent);
    $("#inquiryModal .submit_btn").text("완료");

    let qnaContent = $(editButton).closest('.qna_cont').parent();

    qnaContent.append(inquiryModal.detach());

    qnaContent.css("position", "relative");

    qnaContent.children().not(inquiryModal).css({
        visibility: "hidden",
        position: "absolute"
    });

    inquiryModal.css({
        top: 0,
        left: 0,
        width: '100%',
        display: 'flex',
        zIndex: 10,
    }).show();
}

// 상품문의 클릭 시 내용 보이게
function toggleContent(content) {
    if (content.is(":visible")) {
        content.find("#inquiryModal").hide();
        content.children().css({
            visibility: "visible",
            position: "relative"
        });

        content.slideUp("fast", function () {
            content.css("border-bottom", "");
            content.prev(".qna_items").css("border-bottom", "");
        });

    } else {
        $(".qna_content").not(content).slideUp("fast").promise().done(function () {
            $(this).css("border-bottom", "").prev(".qna_items").css("border-bottom", "");

            // 기존 qna_content 내부 내용을 다시 보이게 설정
            $(this).children().css({
                visibility: "visible",
                position: "relative"
            });
        });

        content.slideDown("fast", function () {
            content.css("border-bottom", "1px solid #333");
            content.prev(".qna_items").css("border-bottom", "1px solid #333");
        });
    }
}

// 상세페이지 문의 삭제
function deleteProductInquiry(inquiryId, inquiryItem, inquiryContent) {

    Swal.fire({
        text: "삭제하시겠습니까?",
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
                url: '/ajax/inquiry',
                async: false,
                data: {'inquiryId': inquiryId},
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(csrfHeader, csrfToken)
                },
                success: function (data) {
                    if (data.code === 200) {
                        inquiryItem.remove(); // 질문 행 삭제
                        inquiryContent.remove(); // 내용 행 삭제
                        loadInquiries(currentPage);
                    }
                },
                error: function () {
                    Swal.fire({
                        html: "삭제중 오류가 발생했습니다.<br>다시 시도해 주세요.",
                        showConfirmButton: true,
                        confirmButtonText: '확인',
                        customClass: mySwal,
                        buttonsStyling: false
                    });
                }
            })
        }
    });
}

// 상세페이지 문의 제출 전 유효성 검사
function validateBeforeInquiryModalSubmit() {
    let title = $('#inquiryModal #title').val().trim();
    let content = $('#inquiryModal #content').val().trim();

    if (title === '') {
        Swal.fire({
            text: "제목을 작성해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }

    if (content === '') {
        Swal.fire({
            text: "내용을 작성해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }

    $('.submit_btn').prop('disabled', true);
    return true;
}

let popup = null;

// 팝업 열기 버튼
function popupButton() {
    $('#popupBtn').click(function () {
        $(this).prop('disabled', true);

        getProducts()
            .then((data) => {
                openPopup(data);
            })
            .catch(error => {
                Swal.fire({
                    text: "상품을 불러오는 데 실패했습니다.",
                    showConfirmButton: true,
                    confirmButtonText: '확인',
                    customClass: mySwal,
                    buttonsStyling: false
                });
            })
            .finally(() => {
                $('#popupBtn').prop('disabled', false);
            });
    });
}

//
// let csrfHeader = $("meta[name='_csrf_header']").attr("content");
// let csrfToken = $("meta[name='_csrf']").attr("content");

// 상품 불러오기
function getProducts() {
    return new Promise((resolve, reject) => {
        $.ajax({
            type: 'GET',
            url: '/ajax/products/search',
            success: function (data) {
                resolve(data);  // 요청 성공 시 받은 데이터를 resolve로 반환
            },
            error: function () {
                reject("상품을 불러오는 데 실패했습니다.");  // 실패 시 reject로 에러 메시지 반환
            }
        });
    });
}

// 팝업 창 열기
function openPopup(data) {
    if (popup && !popup.closed) {
        popup.focus();
        return;
    }
    const width = (screen.width) / 2;
    const height = screen.height;
    const left = (screen.width - width) / 2;
    const top = (screen.height - height) / 2;
    popup = window.open("/products/search", "_blank", `width=${width},height=${height},left=${left},top=${top},resizable=yes,scrollbars=yes`);

    // 팝업 창이 완전히 로드된 후에 renderProducts를 호출하여 데이터를 전달
    popup.onload = function () {
        popup.renderProducts(data); // 데이터를 전달하여 팝업에서 제품을 렌더링
        popup.renderPagination(data.totalElements, data.pageable.pageNumber + 1, data.pageable.pageSize); // 페이지네이션 호출
    }
}

// 팝업 창에서 선택한 상품 부모 창에 추가
window.getItem = function (items) {
    let listContainer = $('.input_box_wrap.product .list.product');
    items.forEach(function (item) {
        let newItemHtml = `
            <div class="list_item">
                <div class="item check">
                    <input type="checkbox" name="checkBox">
                </div>
                <div class="item img"><img src="${item.productImage}" alt="상품 이미지"></div>
                <div class="item name">${item.productName}</div>
                <div class="item price">${item.productPrice}</div>
            </div>
        `;
        listContainer.append(newItemHtml);
    });
}

// 상품 문의 삭제
function deleteInquiry(productId, inquiryId) {

    Swal.fire({
        text: "삭제하시겠습니까?",
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
                url: '/ajax/inquiry',
                data: {'inquiryId': inquiryId},
                async: false,
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(csrfHeader, csrfToken)
                },
                success: function (data) {
                    if (data.code === 200) {
                        loadInquiries(productId, 1);
                    }
                },
                error: function () {
                    Swal.fire({
                        html: "삭제 중 오류가 발생했습니다.<br>다시 시도해 주세요.",
                        showConfirmButton: true,
                        confirmButtonText: '확인',
                        customClass: mySwal,
                        buttonsStyling: false
                    });
                }
            })
        }
    });
}

$('#inquiryBtn').on('click', function () {
    $.ajax({
        url: '/ajax/check/login',
        type: 'GET',
        success: function (result) {
            if (result.code == 401) {
                Swal.fire({
                    html: result.message,
                    showConfirmButton: true,
                    confirmButtonText: '확인',
                    customClass: mySwal,
                    buttonsStyling: false,
                    preConfirm: () => {
                        window.location.href = '/login';
                    }
                });
            } else if (result.code == 200) {
                openInquiryModal();
                let inquiryModal = $('#inquiryModal');
                let qnaHead = $('.qna_head');
                // 모달을 qna_head 아래로 삽입
                inquiryModal.insertAfter(qnaHead).css({
                    position: 'relative',
                });

                // 첫 번째 tr에 border-top 스타일 변경
                $('#product .qna_wrap.list .qna_table tbody tr:first-child').css({
                    'border-top': '1px solid #d7d7d7'
                });
            }
        },
        error: function () {
            Swal.fire({
                text: "로그인 상태를 확인하는 중 오류가 발생했습니다.",
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
        }
    });
});

// 폼 제출 전 유효성 검사
function validateBeforeSubmit() {
    let selectedProduct = $('#productName').text().length;
    let title = $('#title').val().trim();
    let content = $('#content').val().trim();

    if (selectedProduct === 0) {
        Swal.fire({
            text: "상품을 선택해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }

    if (title === '') {
        Swal.fire({
            text: "제목을 작성해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }

    if (content === '') {
        Swal.fire({
            text: "내용을 작성해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }

    $('.submit_btn').prop('disabled', true);
    return true;
}
