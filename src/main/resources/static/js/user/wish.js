$(document).ready(function () {

    $('.deleteBtn').on('click', function () {
        let productId = $(this).data('id');
        deleteProduct(productId);
    });

    $('.cartBtn').on('click', function () {
        let productId = $(this).data('id');
        addCart(productId);
    });
});

$('.wish').click(function () {

    let heart = $(this);
    let productId = heart.attr('id');
    let isFilled = heart.attr('src').includes('favorite_filled');

    if (isFilled) {
        $.ajax({
                url: '/ajax/wish',
                type: 'DELETE',
                data: {'productId': productId},
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(csrfHeader, csrfToken);
                },
                success: function () {
                    heart.attr('src', '../../../images/favorite.svg');
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
            url: '/ajax/login/status',
            type: 'GET',
            success: function (result) {
                if (result.status == 200) {
                    addWish(heart, productId);
                }
            },
            error: function (xhr) {
                if(xhr.status == 401) {
                    Swal.fire({
                        text: '로그인이 필요한 기능입니다.',
                        showCancelButton: true,
                        cancelButtonText: '취소',
                        confirmButtonText: '로그인',
                        customClass: mySwalConfirm,
                        reverseButtons: true,
                        buttonsStyling: false,
                    }).then((result) => {
                        if (result.isConfirmed) {
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

function addWish(heart, productId) {
    $.ajax({
        type: 'POST',
        url: '/ajax/wish',
        data: {'productId': productId},
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        },
        success: function (result) {
            if(result.status == 200) {
                heart.attr('src', '../../../images/favorite_filled.svg');
            }
        },
        error: function (xhr) {
            if(xhr.status == 404) {
                Swal.fire({
                    html: xhr.message.replace('\n', '<br>'),
                    showConfirmButton: true,
                    confirmButtonText: '확인',
                    customClass: mySwal,
                    buttonsStyling: false
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
    });
}

function deleteProduct(productId) {
    $.ajax({
            url: '/ajax/wish',
            type: 'DELETE',
            data: {'productId': productId},
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken);
            },
            success: function (result) {
                if(result.status == 200) {
                    heart.attr('src', '../../../images/favorite.svg');
                }
            },
            error: function () {
                if(xhr.status == 404) {
                    Swal.fire({
                        html: xhr.message.replace('\n', '<br>'),
                        showConfirmButton: true,
                        confirmButtonText: '확인',
                        customClass: mySwal,
                        buttonsStyling: false
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
        }
    )
};

function addCart(productId) {
    $.ajax({
        type: 'POST',
        url: '/ajax/cart/add',
        data: {'productId': productId, 'count': 1},
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        },
        success: function (result) {
            $('#cartItemCount').text(result.cartItemCount);
            Swal.fire({
                text: '장바구니에 상품이 담겼습니다.',
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
        },
        error: function () {
            Swal.fire({
                html: '장바구니에 삼품을 담는 중 오류가 발생했습니다.<br>다시 시도해 주세요.',
                showConfirmButton: true,
                confirmButtonText: '확인',
                customClass: mySwal,
                buttonsStyling: false
            });
        }
    })
};
