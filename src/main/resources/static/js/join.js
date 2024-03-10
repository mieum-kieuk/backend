let isValidId = false;
let isValidPw = false;
let isValidPwConfirm = false;
let isValidName = false;
let isValidEmail = false;
let isValidPhone = false;
let isValidVerificationCode = false;

$(document).ready(function () {

    $('input[type="text"]').on('input', function(e) {
        var maxLength = $(this).attr('maxlength');
        var textLength = e.target.value.length;

        // 최대 길이 초과 시 입력 제한
        if (textLength > maxLength) {
            var trimmedValue = e.target.value.substring(0, maxLength);
            $(this).val(trimmedValue);
        }
    });

    // 아이디 입력란 유효성 검사
    $('#loginId').on('focusout', function () {
        var loginId = $(this).val();

        if (loginId.trim() === '') {
            $('#idMsg').text('아이디를 입력해주세요.');
            $('#idMsg').removeClass('success error').addClass('error');
            isValidId = false;
            return;
        }

        var regex = /^(?=.*[a-z])(?=.*\d)[a-z\d]{5,20}$/;
        if (!regex.test(loginId)) {
            $('#idMsg').text('5~20자의 영문 소문자, 숫자 조합을 사용해 주세요.');
            $('#idMsg').removeClass('success').addClass('error');
            isValidId = false;
            return;
        }
        $('#idMsg').text('');

        // AJAX 호출
        $.ajax({
            type: 'POST',
            url: '/members/verification/loginId',
            data: {loginId: loginId},
            success: function (result) {
                if (result) {
                    $('#idMsg').text('사용 가능한 아이디입니다.');
                    $('#idMsg').removeClass('error').addClass('success');
                    isValidId = true;
                } else {
                    $('#idMsg').text('이미 사용 중인 아이디입니다.');
                    $('#idMsg').removeClass('success').addClass('error');
                    isValidId = false;
                }
            }
        });
    });

    // 비밀번호 입력란 유효성 검사
    $('#password').on('focusout', function () {
        var password = $(this).val();
        var confirmPassword = $('#passwordConfirm').val();

        if (password.trim() === '') {
            $('#pwMsg').text('비밀번호를 입력해주세요.');
            isValidPw = false;
            return;
        }

        var regex = /^(?=.*[a-z])(?=.*\d)(?=.*[@$!%*?&^()])[A-Za-z\d@$!%*?&^()]{8,16}$/;
        if (!regex.test(password)) {
            $('#pwMsg').text('8~16자의 영문 대/소문자, 숫자, 특수문자 조합을 사용해 주세요.');
            isValidPw = false;
            return;
        }

        $('#pwMsg').text('');
        isValidPw = true;

        // 비밀번호가 일치하면 확인 메시지를 비움
        if (password === confirmPassword) {
            $('#pwconfirmMsg').text('');
            isValidPwConfirm = true;
        }
    });


    // 비밀번호 확인란
    $('#passwordConfirm').on('focusout', function () {
        var password = $('#password').val();
        var confirmPassword = $(this).val();

        // 입력값이 비어 있는지 확인
        if (confirmPassword === '') {
            $('#pwconfirmMsg').text('');
            isValidPwConfirm = false;
        } else {
            if (password === confirmPassword) {
                $('#pwconfirmMsg').text('');
                isValidPwConfirm = true;
            } else {
                $('#pwconfirmMsg').text('비밀번호가 일치하지 않습니다.');
                isValidPwConfirm = false;
            }
        }

    });


    // 이름 입력란 유효성 검사
    $('#name_content input').on('focusout', function () {
        var name = $(this).val();

        if (name.trim() === '') {
            $('#nameMsg').text('이름을 입력해주세요.');
            isValidName = false;
            return;
        }

        var regex = /^[가-힣a-zA-Z]{2,12}$/;
        if (!regex.test(name)) {
            $('#nameMsg').text('한글, 영문 대/소문자를 사용해 주세요. (특수기호, 공백 사용 불가)');
            isValidName = false;
            return;
        }

        $('#nameMsg').text('');
        isValidName = true;
    });

    // 이메일 입력란 유효성 검사
    $('#email').on('focusout', function () {
        var email = $(this).val();

        if (email.trim() === '') {
            $('#emailMsg').text('이메일을 입력해주세요.');
            isValidEmail = false;
            return;
        }

        var regex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
        if (!regex.test(email)) {
            $('#emailMsg').text('유효한 이메일을 입력해주세요.');
            isValidEmail = false;
            return;
        }
        $('#emailMsg').text('');

        // AJAX 호출
        $.ajax({
            type: 'POST',
            url: '/members/verification/email',
            data: {email: email},
            success: function (result) {
                if (result) {
                    $('#emailMsg').text('사용 가능한 이메일입니다.');
                    $('#emailMsg').removeClass('error').addClass('success');
                    isValidEmail = true;
                } else {
                    $('#emailMsg').text('이미 사용 중인 이메일입니다.');
                    $('#emailMsg').removeClass('success').addClass('error');
                    isValidEmail = false;
                }
            }
        })
        isValidEmail = true;
    });

    $('input[id^="phonenumber"]').on('input', function () {
        var phonenumber1 = $('#phonenumber1').val();
        var phonenumber2 = $('#phonenumber2').val();
        var phonenumber3 = $('#phonenumber3').val();
        var regex = /^[0-9]{3,4}$/;
        var regex4 = /^[0-9]{4}$/; // phonenumber3는 4자리

        // 정규식 검사를 통해 유효한 휴대전화번호인지 확인
        var isValidPhoneNumber = regex.test(phonenumber1) && regex.test(phonenumber2) && regex4.test(phonenumber3);

        if (isValidPhoneNumber) {
            $('#btn_action_verify_mobile').removeClass('disabled'); // 활성화 클래스 추가
        } else {
            $('#btn_action_verify_mobile').addClass('disabled'); // 활성화 클래스 제거
        }
        // 인증번호 입력 칸 숨김
        $('#confirm_verify_mobile').hide();
        $('#phoneNumberMsg').text('');
        $('#btn_action_verify_mobile').text('인증번호 받기');
        // 타이머 초기화
        clearInterval(interval);
        $('#expiryTime').text('');
    });

    $('input[id^="phonenumber"]').on('focusout', function () {
        var phonenumber1 = $('#phonenumber1').val().trim(); // 공백 제거
        var phonenumber2 = $('#phonenumber2').val().trim();
        var phonenumber3 = $('#phonenumber3').val().trim();

        // 입력값이 비어 있는지 확인
        if (phonenumber1 === '' || phonenumber2 === '' || phonenumber3 === '') {
            $('#phoneNumberMsg').text('휴대전화번호를 입력해 주세요.');
            isValidPhone = false;
            return;
        }
    });

// 인증번호 받기 버튼 클릭 시 처리
    $('#btn_action_verify_mobile').on('click', function () {
        var phonenumber1 = $('#phonenumber1').val();
        var phonenumber2 = $('#phonenumber2').val();
        var phonenumber3 = $('#phonenumber3').val();

        // AJAX 호출 - 인증번호 요청
        $.ajax({
            type: 'POST',
            url: '/members/send/verificationNo',
            data: {phonenumber1: phonenumber1, phonenumber2: phonenumber2, phonenumber3: phonenumber3},
            dataType: 'json',
            success: function (result) {
                if (result['success']) {
                    $('#phoneNumberMsg').text(result['message']);
                    $('#confirm_verify_mobile').css('display', 'flex');
                    $('#btn_action_verify_mobile').text('재전송');
                    $('#expiryTime').text('3:00');
                    $('#verificationNo').val('');
                    startTimer();
                    isValidPhone = true;
                } else {
                    $('#confirm_verify_mobile').css('display', 'none');
                    $('#phoneNumberMsg').text(result['message']);
                    isValidPhone = false;
                }
            }
        });
    });

    var interval; // 전역 변수로 선언

    function startTimer() {
        var timer = 180; // 3분 = 180초
        interval = setInterval(function () {
            var minutes = Math.floor(timer / 60);
            var seconds = timer % 60;

            $('#expiryTime').text(minutes + ':' + (seconds < 10 ? '0' + seconds : seconds));

            if (--timer < 0) {
                clearInterval(interval);
                $('#phoneNumberMsg').text('');
                $('#confirm_verify_mobile').css('display', 'none');
                $('#btn_action_verify_mobile').text('인증번호 받기');
                $('#verificationNo').val('');
            }
        }, 1000);
    }



    $('#agree_group').on('click', "#agree_all", function () {
        var isChecked = $(this).prop('checked');

        if (isChecked) {
            $(this).parents("#agree_group").find('input[type="checkbox"]').prop("checked", true);
        } else {
            $(this).parents("#agree_group").find('input[type="checkbox"]').prop("checked", false);
        }
    });
});

//회원가입 완료
function signUp() {
    if (!isValidId) {
        alert("아이디를 확인해주세요");
        return false;
    }
    if (!isValidPw) {
        alert("비밀번호를 확인해주세요");
        return false;
    }
    if (!isValidPwConfirm) {
        alert("비밀번호가 일치하지 않습니다.");
        return false;
    }
    if (!isValidName) {
        alert("이름을 확인해주세요");
        return false;
    }
    if (!isValidEmail) {
        alert("이메일을 확인해주세요");
        return false;
    }
    if (!isValidPhone) {
        alert("핸드폰 번호를 확인해주세요");
        return false;
    }
    if (!isValidVerificationCode) {
        alert("핸드폰 번호 인증해주세요.");
        return false;
    }

    if(!$('#agree_to_terms_of_use').is(':checked')) {
        alert("이용약관 동의를 확인해 주세요.");
        return false;
    }

    if(!$('#agree_to_personal_information').is(':checked')) {
        alert("개인정보 수집 및 이용 동의를 확인해 주세요.");
        return false;
    }

    return true;
}


