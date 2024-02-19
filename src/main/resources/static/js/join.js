$(document).ready(function() {
    $('#loginId').on('input', function() {
        var loginId = $(this).val();

        if (loginId.trim() === '') {
            $('#idMsg').text('아이디를 입력해주세요.');
            $('#idMsg').removeClass('success error').addClass('error');
            return;
        }

        var regex = /^[a-z0-9]{5,20}$/;
        if (!regex.test(loginId)) {
            $('#idMsg').text('5-20자의 영문 소문자, 숫자를 사용해주세요.');
            $('#idMsg').removeClass('success').addClass('error');
            return;
        }

        $.ajax({
            type: 'POST',
            url: '/members/verification/loginId',
            data: { loginId: loginId },
            success: function(data) {
                if (data) {
                    $('#idMsg').text('사용 가능한 아이디입니다.');
                    $('#idMsg').removeClass('error').addClass('success');
                } else {
                    $('#idMsg').text('이미 사용 중인 아이디입니다.');
                    $('#idMsg').removeClass('success').addClass('error');
                }
            },
            error: function(error) {
                alert("error : " + error);
            }
        });
    });
});