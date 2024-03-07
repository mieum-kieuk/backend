$(document).ready(function() {
    function checkInputs() {
        // 아이디와 비밀번호 중 하나라도 입력되지 않은 경우
        if ($('#loginId').val().trim() === '' || $('#password').val().trim() === '') {
            $('#loginBtn').prop('disabled', true); // 버튼을 비활성화
        } else {
            $('#loginBtn').prop('disabled', false); // 버튼을 활성화
        }
    }

    $('#loginId, #password').on('input', checkInputs);

    checkInputs();

    $('#loginBtn').click(function(event) {
        event.preventDefault();

        var id = $('#loginId').val();
        var password = $('#password').val();

        var data = {
            id: id,
            password: password
        };

        $.ajax({
            type: 'POST',
            url: '/login',
            contentType: '',
            data: JSON.stringify(data),
            success: function(result) {
                if (result.success) {
                    alert('로그인 성공!');
                    window.location.href = '';
                } else {
                    alert('로그인 실패: ' + result.message);

                }
            },
        });
    });
});
