$(document).ready(function () {
    $('.deleteBtn').on('click', function () {
        let productId = $(this).data('id');
        deleteProduct(productId);
    });
});

$('.wish').click(function () {

    let heart = $(this);
    let productId = heart.attr('id');

    if (heart.hasClass('filled')) {
        $.ajax({
                url: '/ajax/wish',
                type: 'DELETE',
                data: {'productId': productId},
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(csrfHeader, csrfToken);
                },
                success: function () {
                    heart.removeClass('filled');
                },
                error: function () {
                    Swal.fire({
                        html: "요청사항 진행 중 오류가 발생했습니다.<br>다시 시도해 주세요.",
                        showConfirmButton: true,
                        confirmButtonText: '확인',
                        customClass: mySwal,
                        buttonsStyling: false
                    });
                }
            }
        )
    } else {
        $.ajax({
            url: '/ajax/check/login',
            type: 'GET',
            success: function (result) {
                if (result.code == 200) {
                    addWish(heart, productId);
                } else if (result.code == 401) {
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
                }
            },
            error: function () {
                Swal.fire({
                    html: "요청사항 진행 중 오류가 발생했습니다.<br>다시 시도해 주세요.",
                    showConfirmButton: true,
                    confirmButtonText: '확인',
                    customClass: mySwal,
                    buttonsStyling: false
                });
            }
        })
    }
});

function addWish(heart, productId) {
    $.ajax({
        type: 'POST',
        url: '/ajax/wish',
        data: {'productId': productId},
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        },
        success: function () {
            heart.addClass('filled');
        },
        error: function () {
            Swal.fire({
                html: "요청사항 진행 중 오류가 발생했습니다.<br>다시 시도해 주세요.",
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
        }
    });
}

function deleteProduct(productId) {
    let csrfToken = $("meta[name='_csrf']").attr("content");
    let csrfHeader = $("meta[name='_csrf_header']").attr("content");

    $.ajax({
            url: '/ajax/wish',
            type: 'DELETE',
            data: {'productId': productId},
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken);
            },
            success: function () {
                window.location.reload();
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
        }
    )
};

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
        error: function () {
            window.location.href = '/login';
        }
    })
};
