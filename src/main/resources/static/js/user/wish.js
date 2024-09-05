let csrfToken = $("meta[name='_csrf']").attr("content");
let csrfHeader = $("meta[name='_csrf_header']").attr("content");

$('.wish').click(function () {

    let heart = $(this);
    let productId = heart.attr('id');

    if (heart.hasClass('filled')) {
        $.ajax({
            url: '/ajax/wish/remove',
            type: 'POST',
            data: {productId: productId},
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken);
            },
            success: function () {
                heart.removeClass('filled');
            },
            error: function (xhr) {
                if(xhr.status == 401) {
                    Swal.fire({
                        text: "로그인이 필요한 서비스입니다.",
                        showConfirmButton: true,
                        confirmButtonText: '확인',
                        customClass: mySwal,
                        buttonsStyling: false,
                        preConfirm: () => {
                            window.location.href = '/login';
                        }
                    });
                } else {
                    Swal.fire({
                        html: "요청사항 진행 중 오류가 발생했습니다.<br>다시 시도해 주세요.",
                        showConfirmButton: true,
                        confirmButtonText: '확인',
                        customClass: mySwal,
                        buttonsStyling: false
                    });
                }
            }
        })
    } else {
        $.ajax({
            type: 'POST',
            url: '/ajax/wish/add',
            data: {productId: productId},
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken);
            },
            success: function (response) {
                heart.addClass('filled')
            },
            error: function (xhr) {
                if(xhr.status == 401) {
                    Swal.fire({
                        text: "로그인이 필요한 서비스입니다.",
                        showConfirmButton: true,
                        confirmButtonText: '확인',
                        customClass: mySwal,
                        buttonsStyling: false,
                        preConfirm: () => {
                            window.location.href = '/login';
                        }
                    });
                } else {
                    Swal.fire({
                        html: "요청사항 진행 중 오류가 발생했습니다.<br>다시 시도해 주세요.",
                        showConfirmButton: true,
                        confirmButtonText: '확인',
                        customClass: mySwal,
                        buttonsStyling: false
                    });
                }
            }
        })
    }
});

function addCart(productId) {

    $.ajax({
        type: 'POST',
        url: '/api/cart/add',
        data: {productId: productId, count: 1},
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
            xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
        },
        success: function (message) {
            Swal.fire({
                text: message,
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });

        },
        error: function() {
            window.location.href = '/login';
        }
    })
}

function deleteOk(wishId) {
    Swal.fire({
        text: "선택한 상품을 삭제하시겠습니까?",
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
            window.location.href = '/mypage/wish/' + wishId + "/delete";
        } else {
            return false;
        }
    })
}