$(document).ready(function() {
    $('#value').on('focusout', function() {
        validateDiscountValue();
    });

    $('#discountForm').submit(function(event) {
        event.preventDefault();

        var isValid = true;
        isValid = validateDiscountValue() && isValid;

        if (!isValid) {
            alert('모든 필드를 정확히 입력해주세요.');
        } else {
        }
    });


    function validateDiscountValue() {
        var discountValue = $('#value').val().trim();
        var discountType = $('input[name=options]:checked').val();

        if (discountValue === '') {
            alert('할인 설정을 입력해주세요.');
            return false;
        } else if (parseInt(discountValue) < 1) {
            alert('1보다 큰 값을 입력해주세요.');
            return false;
        }

        if (discountType === 'option1') {
            if (!/^[1-9][0-9]?$|^100$/.test(discountValue)) {
                alert('1부터 100사이의 값을 입력해주세요.');
                return false;
            }
        } else if (discountType === 'option2') {
            if (parseInt(discountValue) <= 1) {
                alert('1보다 큰 값을 입력해주세요.');
                return false;
            }
        }

        return true;
    }
});

