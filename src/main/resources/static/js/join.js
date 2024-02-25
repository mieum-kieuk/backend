$(document).ready(function() {
    // 아이디 입력란 유효성 검사
    $('#loginId').on('input', function() {
        var loginId = $(this).val();

        if (loginId.trim() === '') {
            $('#idMsg').text('아이디를 입력해주세요.');
            $('#idMsg').removeClass('success error').addClass('error');
            return;
        }

        var regex = /^(?=.*[a-z])(?=.*\d)[a-z\d]{5,20}$/;
        if (!regex.test(loginId)) {
            $('#idMsg').text('5~20자의 영문 소문자, 숫자 조합을 사용해 주세요.');
            $('#idMsg').removeClass('success').addClass('error');
            return;
        }
        $('#idMsg').text('');

        // AJAX 호출
        $.ajax({
            type: 'POST',
            url: '/members/verification/loginId',
            data: { loginId: loginId },
            success: function(result) {
                if (result) {
                    $('#idMsg').text('사용 가능한 아이디입니다.');
                    $('#idMsg').removeClass('error').addClass('success');
                } else {
                    $('#idMsg').text('이미 사용 중인 아이디입니다.');
                    $('#idMsg').removeClass('success').addClass('error');
                }
            }
        });
    });

    // 비밀번호 입력란 유효성 검사
    $('#password').on('focusout', function() {
        var password = $(this).val();

        if (password.trim() === '') {
            $('#pwMsg').text('비밀번호를 입력해주세요.');
            return;
        }

        var regex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,16}$/;
        if (!regex.test(password)) {
            $('#pwMsg').text('8~16자의 영문 대/소문자, 숫자, 특수문자 조합을 사용해 주세요.');
            return;
        }

        $('#pwMsg').text('');
    });

    // 이름 입력란 유효성 검사
    $('#name_content input').on('focusout', function() {
        var name = $(this).val();

        if (name.trim() === '') {
            $('#nameMsg').text('이름을 입력해주세요.');
            return;
        }

        var regex = /^[가-힣a-zA-Z]{2,5}$/;
        if (!regex.test(name)) {
            $('#nameMsg').text('한글, 영문 대/소문자를 사용해 주세요. (특수기호, 공백 사용 불가)');
            return;
        }

        $('#nameMsg').text('');
    });

    // 비밀번호 확인란 입력 이벤트 처리
    $('#passwordConfirm').on('input', function() {
        var password = $('#password').val();
        var confirmPassword = $(this).val();

        // 비밀번호와 비밀번호 확인이 일치하는지 확인
        if (password === confirmPassword) {
            $('#pwconfirmMsg').text('비밀번호가 일치합니다.');
        } else {
            $('#pwconfirmMsg').text('비밀번호가 일치하지 않습니다.');
        }
    });
    $(document).ready(function() {
        $('#email').on('focusout', function() {
            var email = $(this).val();

            if (email.trim() === '') {
                $('#emailMsg').text('이메일을 입력해주세요.');
                return;
            }

            var regex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
            if (!regex.test(email)) {
                $('#emailMsg').text('유효한 이메일을 입력해주세요.');
                return;
            }

            $('#emailMsg').text('');
        });
    });


    // 아이디 입력란 포커스 아웃 이벤트 처리
    $('#loginId').on('focusout', function() {
        var loginId = $(this).val();

        if (loginId.trim() === '') {
            $('#idMsg').text('아이디를 입력해주세요.');
            $('#idMsg').removeClass('success error').addClass('error');
            return;
        }
    });

    $('#phonenumber2, #phonenumber3').on('input', function() {
        var phoneNumber2 = $('#phonenumber2').val();
        var phoneNumber3 = $('#phonenumber3').val();

        if (phoneNumber2.trim() !== '' && phoneNumber3.trim() !== '') {
            $('#btn_action_verify_mobile').prop('disabled', false);
        } else {
            $('#btn_action_verify_mobile').prop('disabled', true);
        }
    });

    // 인증번호 받기 버튼 클릭 시
    $('#btn_action_verify_mobile').on('click', function() {
        var phoneNumber = $('#phonenumber1').val() + $('#phonenumber2').val() + $('#phonenumber3').val();

        // AJAX 호출 - 인증번호 요청
        $.ajax({
            type: 'POST',
            url: '/send/verificationNumber',
            data: { phoneNumber: phoneNumber },
            success: function(response) {
                if (response.success) {
                    $('#confirm_verify_mobile').removeClass('displaynone');
                    $('#result_send_verify_mobile_success').removeClass('displaynone');
                    $('#expiryTime').text('3:00');
                    startTimer();
                } else {
                    $('#result_send_verify_mobile_fail').text('인증번호 발송에 실패했습니다.');
                    $('#result_send_verify_mobile_fail').removeClass('displaynone');
                }
            },
            error: function(xhr, status, error) {
                $('#result_send_verify_mobile_fail').text('인증번호 발송에 실패했습니다.');
                $('#result_send_verify_mobile_fail').removeClass('displaynone');
            }
        });
    });

    // 확인 버튼 클릭 시
    $('#btn_verify_mobile_confirm').on('click', function() {
        var verificationCode = $('#confirm_verify_mobile .input_box').val();

        // AJAX 호출 - 인증번호 확인
        $.ajax({
            type: 'POST',
            url: '/send/verificationNo',
            data: { verificationCode: verificationCode },
            success: function(result) {
                if (result.success) {
                    $('#confirm_verify_mobile').removeClass('displaynone');
                    $('#result_send_verify_mobile_success').removeClass('displaynone');
                    $('#result_send_verify_mobile_success').text('인증번호가 발송되었습니다.');
                    $('#btn_action_verify_mobile').text('재전송');
                    $('#expiryTime').text('3:00');
                    startTimer();
                } else {
                    // $('#result_send_verify_mobile_fail').text('인증번호 발송에 실패했습니다.');
                    // $('#result_send_verify_mobile_fail').removeClass('displaynone');
                    $('#confirm_verify_mobile').removeClass('displaynone');
                    $('#result_send_verify_mobile_success').removeClass('displaynone');
                    $('#result_send_verify_mobile_success').text('인증번호가 발송되었습니다.');
                    $('#btn_action_verify_mobile').text('재전송');
                    $('#expiryTime').text('3:00');
                    startTimer();
                }
            },
            error: function(xhr, status, error) {
                // $('#result_send_verify_mobile_fail').text('인증번호 발송에 실패했습니다.');
                // $('#result_send_verify_mobile_fail').removeClass('displaynone');
                $('#confirm_verify_mobile').removeClass('displaynone');
                $('#result_send_verify_mobile_success').removeClass('displaynone');
                $('#result_send_verify_mobile_success').text('인증번호가 발송되었습니다.');
                $('#btn_action_verify_mobile').text('재전송');
                $('#expiryTime').text('3:00');
                startTimer();
            }

        });
    });

    // 타이머 시작 함수
    function startTimer() {
        var timer = 180; // 3분 = 180초
        var interval = setInterval(function() {
            var minutes = Math.floor(timer / 60);
            var seconds = timer % 60;

            $('#expiryTime').text(minutes + ':' + (seconds < 10 ? '0' + seconds : seconds));

            if (--timer < 0) {
                clearInterval(interval);
                // 타이머 종료 시 처리할 내용
            }
        }, 1000);
    }

    $('#agree_group').on('click', "#agree_all", function() {
        var isChecked = $(this).prop('checked');

        if(isChecked){
            $(this).parents("#agree_group").find('input[type="checkbox"]').prop("checked", true);
        } else {
            $(this).parents("#agree_group").find('input[type="checkbox"]').prop("checked", false);
        }
    });

});


