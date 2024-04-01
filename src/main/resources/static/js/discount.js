function validateBeforeSubmit() {
    let discountName = $('#name').val().trim();
    let discountPercent = $('#discountPercent').val().trim();
    let startDatetime = $('#startDatetime').val().trim();
    let endDatetime = $('#endDatetime').val().trim();

    if (discountName === '') {
        alert('할인 혜택명을 입력해 주세요.');
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



    if (startDatetime === '' && endDatetime === '') {
        alert('할인 기간을 입력해 주세요.');
        return false;
    }
    if (startDatetime === '' ) {
        alert('시작일시를 입력해 주세요.');
        return false;
    }
    if (endDatetime === '' ) {
        alert('종료일시를 입력해 주세요.');
        return false;
    }

    let startDate = new Date(startDatetime);
    let endDate = new Date(endDatetime);

    if (startDate >= endDate) {
        alert('시작일시는 종료일시보다 이전이어야 합니다.');
        return false;
    }

    return true;
}

$(document).ready(function () {

    $('.submit_btn').click(function () {
        // 버튼 클릭 시 유효성 검사 함수 호출
        if (!validateBeforeSubmit()) {
            return false; // 제출 중지
        } else {
            $('#discountForm').submit(); // 유효성 검사 통과 시 폼 제출
        }
    });

    $('.menu_toggle').click(function () {
        var dropdownMenu = $(this).siblings('.dropdown_menu');
        $('.dropdown_menu').not(dropdownMenu).removeClass('show');
        dropdownMenu.toggleClass('show');
    });

    $('#selectAll').click(function () {
        if ($(this).prop('checked')) {
            $('.discount_table tbody input[type="checkbox"]').prop('checked', true);
        } else {
            $('.discount_table tbody input[type="checkbox"]').prop('checked', false);
        }
    });
});

function deleteOk(discountId) {
    if (!confirm('삭제하시면 복구할 수 없습니다. \n정말로 삭제하시겠습니까?')) {
        return false;
    } else {
        window.location.href = '/admin/promotion/discounts/' + discountId + "/delete";
        return true;
    }
}

function deleteDiscount() {

    let discountIds = [];
    let checkboxes = $('input[name=checkbox]:checked');

    if (checkboxes.length == 0) {
        alert('삭제할 할인 혜택을 선택해 주세요.');
        return false;
    } else {
        if(!confirm(checkboxes.length + '개 항목을 삭제하시겠습니까?')) {
            return false;
        } else {
            $(checkboxes.each(function (v) {
                let discountId = checkboxes[v].id.split('checkbox')[1];
                discountIds.push(discountId);
            }))

            $.ajax({
                type: 'POST',
                url: '/admin/promotion/discounts/delete',
                data: JSON.stringify(discountIds),
                contentType: 'application/json',
                success: function () {
                    window.location.href = '/admin/promotion/discounts';
                },
                error: function (result) {
                    alert(result['message']);
                }
            })
        }
    }
}

// $(function() {
//     $(".datetimepicker").datetimepicker({
//         format: "Y-m-d H:i",
//     });
// });

$(document).ready(function() {
    // 현재 날짜와 시간 가져오기
    let currentDate = new Date();

    let minDate = currentDate.toISOString().slice(0,16);
    $('#startDatetime').attr('min', minDate);
    $('#endDatetime').attr('min', minDate);

});
