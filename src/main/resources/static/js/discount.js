function validateBeforeSubmit() {
    let discountName = $('#name').val().trim();
    let discountValue = $('#value').val().trim();
    let discountType = $('input[name=type]:checked').val();

    if (discountName === '') {
        alert('할인명을 입력해 주세요.');
        return false;
    }

    if (discountValue === '') {
        alert('할인율 또는 할인 금액을 입력해 주세요.');
        return false;
    }

    if (discountType === 'RATE') {
        if (!/^[1-9][0-9]?$|^100$/.test(discountValue)) {
            alert('정률 할인의 경우, 1부터 100사이의 값을 입력해 주세요.');
            return false;
        }
    } else if (discountType === 'FIX') {
        if (parseInt(discountValue) < 1) {
            alert('정액 할인의 경우, 1원 이상의 금액을 입력해 주세요.');
            return false;
        }
    }

    return true;
}

$(document).ready(function () {
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



