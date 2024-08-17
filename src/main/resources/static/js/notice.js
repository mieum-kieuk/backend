//유효성 검사
function validateBeforeSubmit() {
    let titleValue = $('#title').val().trim();
    let contentValue = $('#content').val().trim();
    let contentValueLength = contentValue.length;

    if (titleValue === '') {
        Swal.fire({
            text: "제목을 작성해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }

    if (contentValue === '') {
        Swal.fire({
            text: "내용을 작성해 주세요.",
            showConfirmButton: true,
            confirmButtonText: '확인',
            customClass: mySwal,
            buttonsStyling: false
        });
        return false;
    }

    if (contentValueLength > 2000) {
        Swal.fire({
            text: "내용은 최대 2000자까지 작성할 수 있습니다.",
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

//공지사항 삭제
function deleteNotice(noticeId) {

    let csrfHeader = $("meta[name='_csrf_header']").attr("content");
    let csrfToken = $("meta[name='_csrf']").attr("content");

    Swal.fire({
        text: '삭제하시겠습니까?',
        showCancelButton: true,
        cancelButtonText: '아니요',
        confirmButtonText: '예',
        closeOnConfirm: false,
        closeOnCancel: true,
        customClass: mySwalConfirm,
        reverseButtons: true,
        buttonsStyling: false,
    }).then((result) => {
        if (result.isConfirmed) {
            $.ajax({
                type: 'DELETE',
                url: '/ajax/admin/notice/delete',
                async: false,
                data: {noticeId: noticeId},
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(csrfHeader, csrfToken)
                },
                success: function (result) {
                    if (result.code === 200) {
                        window.location.href = '/admin/notice';
                    } else {
                        Swal.fire({
                            text: result.message,
                            showConfirmButton: true,
                            confirmButtonText: '확인',
                            customClass: mySwal,
                            buttonsStyling: false
                        });
                    }
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
}
