$(document).ready(function () {

    $('.deleteBtn').on('click', function () {
        let productId = $(this).data('id');
        removeWish(productId);
    });

    $('.cartBtn').on('click', function () {
        let productId = $(this).data('id');
        addCart(productId);
    });
});

let csrfHeader = $("meta[name='_csrf_header']").attr("content");
let csrfToken = $("meta[name='_csrf']").attr("content");

function removeWish(productId) {
    $.ajax({
            url: '/ajax/wish',
            type: 'DELETE',
            data: {'productId': productId},
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken);
            },
            success: function (result) {
                if(result.status == 200) {
                    window.location.reload();
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
