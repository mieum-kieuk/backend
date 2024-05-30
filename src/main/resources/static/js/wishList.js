function addCart(productId) {

    let csrfToken = $("meta[name='_csrf']").attr("content");
    let csrfHeader = $("meta[name='_csrf_header']").attr("content");

    $.ajax({
        type: 'POST',
        url: '/api/cart/add',
        data: {productId: productId, count: 1},
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
            xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
        },
        success: function (message) {
            alert(message);
        },
        error: function() {
            window.location.href = '/login';
        }
    })


}

function deleteOk(wishId) {
    if (!confirm('선택한 상품을 삭제하시겠습니까?')) {
        return false;
    } else {
        window.location.href = '/mypage/wish/' + wishId + "/delete";
        return true;
    }
}