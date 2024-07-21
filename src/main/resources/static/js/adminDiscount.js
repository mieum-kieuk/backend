$(document).ready(function () {
    // 현재 날짜와 시간 가져오기
    let currentDate = new Date();

    let minDate = currentDate.toISOString().slice(0, 16);
    $('#startedAt').attr('min', minDate);
    $('#expiredAt').attr('min', minDate);

    // 목록에서 체크박스 전체선택
    $('#selectAll').click(function () {
        let isChecked = $(this).prop('checked');
        $('.result_table input[type="checkbox"]').prop('checked', isChecked);
        $('.list_content input[type="checkbox"]').prop('checked', isChecked);
    });

    $('.result_table').on('change', 'input[type="checkbox"]', function () {
        let totalCheck = $('.result_table tbody input[type="checkbox"]').length ;
        let checkedBox = $('.result_table tbody input[type="checkbox"]:checked').length ;

        $('#selectAll').prop('checked', totalCheck === checkedBox);
    });

    $('.list_content').on('change', 'input[type="checkbox"]', function () {
        let totalCheck = $('.list_content .list input[type="checkbox"]').length;
        let checkedBox = $('.list_content .list input[type="checkbox"]:checked').length;

        $('#selectAll').prop('checked', totalCheck === checkedBox);
    });

    $('input[name="productType"]').on('change', function() {
        if ($(this).val() === 'specific') {
            $('.input_box_wrap.product').show();
        } else {
            $('.input_box_wrap.product').hide();
        }
    });
    $('#popupBtn').click(function () {
        window.open("./discount_popup.html", "_blank", "width=600px,height=450px");
    });
    $('.submit_btn').click(function(event) {
        if (!validateBeforeSubmit()) {
            event.preventDefault();
        }
    });

});
//유효성 검사
function validateBeforeSubmit() {
    let discountName = $('#name').val().trim();
    let discountPercent = $('#discountPercent').val().trim();
    let productType = $('input[name="productType"]:checked').val();
    let startedAt = $('#startedAt').val().trim();
    let expiredAt = $('#expiredAt').val().trim();
    console.log('Discount Name:', discountName);
    console.log('Discount Percent:', discountPercent);
    console.log('Started At:', startedAt);
    console.log('Expired At:', expiredAt);
    console.log('Product Type:', productType);
    if (discountName === '') {
        alert('할인명을 입력해 주세요.');
        return false;
    }
    if (startedAt === '' && expiredAt === '') {
        alert('할인 기간을 지정해 주세요.');
        return false;
    }
    if (startedAt === '') {
        alert('시작일시를 지정해 주세요.');
        return false;
    }
    if (expiredAt === '') {
        alert('종료일시를 지정해 주세요.');
        return false;
    }

    let startDate = new Date(startedAt);
    let endDate = new Date(expiredAt);

    if (startDate >= endDate) {
        alert('시작일시는 종료일시보다 이전이어야 합니다.');
        return false;
    }
    if (discountPercent === '') {
        alert('할인율을 입력해 주세요.');
        return false;
    }

    let discountPercentRegex = /^(?:1|[1-9]\d?|100)$/;
    if (!discountPercentRegex.test(discountPercent)) {
        alert('1부터 100 사이의 값을 입력해 주세요.');
        return false;
    }

    if (!productType) {
        alert('적용 상품을 선택해 주세요.');
        return false;
    }
    if (productType === 'specific') {
        let listItems = $('.list_item');
        if (listItems.length === 0) {
            alert('상품을 선택해 주세요.');
            return false;
        }
    }

    return true;
}

//상품 할인 단건 삭제
function deleteDiscount(discountId) {

    let csrfHeader = $("meta[name='_csrf_header']").attr("content");
    let csrfToken = $("meta[name='_csrf']").attr("content");

    if (confirm("삭제하시겠습니까?")) {
        $.ajax({
            type: 'DELETE',
            url: '/ajax/admin/discount/delete',
            async: false,
            data: {'discountId': discountId},
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken)
            },
            success: function (data) {
                if (data.code === 200) {
                    window.location.href = '/admin/discounts';
                } else {
                    alert(data.message);
                }
            },
            error: function () {
                alert('삭제중 오류가 발생했습니다. 다시 시도해 주세요.');
            }
        })
    } else {
        return false;
    }
};

//상품 할인 여러건 삭제
$('#deleteDiscountsBtn').click(function () {

    let discountIds = [];
    let checkboxes = $('input[name=checkbox]:checked');
    let csrfHeader = $("meta[name='_csrf_header']").attr("content");
    let csrfToken = $("meta[name='_csrf']").attr("content");

    if (checkboxes.length == 0) {
        alert('삭제할 항목들을 선택해 주세요.');
        return false;
    } else {
        if (confirm(checkboxes.length + '개 항목을 삭제하시겠습니까?')) {
            $(checkboxes.each(function (v) {
                let discountId = checkboxes[v].id.split('checkbox')[1];
                discountIds.push(discountId);
            }));

            $.ajax({
                type: 'DELETE',
                url: '/ajax/admin/discounts/delete',
                async: false,
                contentType: 'application/json',
                data: JSON.stringify(discountIds),
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(csrfHeader, csrfToken)
                },
                success: function (data) {
                    if (data.code === 200) {
                        window.location.href = '/admin/discounts';
                    } else {
                        alert(data.message);
                    }
                },
                error: function () {
                    alert('삭제중 오류가 발생했습니다. 다시 시도해 주세요.');
                }
            })
        } else {
            return false;
        }
    }
});