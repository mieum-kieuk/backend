let isValidId = false;
let isValidPw = false;
let isValidPwConfirm = false;
let isValidName = false;
let isValidEmail = false;
let isValidPhone = false;
let isValidVerificationCode = false;
let isValidAgree = false;

$(document).ready(function () {

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
                console.log(result);
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

        var regex = /^[가-힣a-zA-Z]{2,5}$/;
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
        isValidEmail = true;
    });


    $('#btn_action_verify_mobile').on('click', function () {
        var phonenumber1 = $('#phonenumber1').val();
        var phonenumber2 = $('#phonenumber2').val();
        var phonenumber3 = $('#phonenumber3').val();

        //AJAX 호출 - 인증번호 요청
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

    $('#btn_verify_mobile_confirm').on('click', function () {
        var phonenumber1 = $('#phonenumber1').val();
        var phonenumber2 = $('#phonenumber2').val();
        var phonenumber3 = $('#phonenumber3').val();
        var verificationNo = $('#verificationNo').val();

        //AJAX 호출 - 인증번호 검증
        $.ajax({
            type: 'POST',
            url: '/members/verification/verificationNo',
            data: {
                phonenumber1: phonenumber1,
                phonenumber2: phonenumber2,
                phonenumber3: phonenumber3,
                verificationNo: verificationNo
            },
            success: function (result) {
                if (result) {
                    $('#expiryTime').css('display', 'none');
                    $('#verificationNo').val('');
                    $('#verificationNo').attr('placeholder', '핸드폰 인증 완료');
                    $('#verificationNo').attr('disabled', true);
                    $('#verificationNo').css('background-color', '#EFEFEF');
                    $('#btn_verify_mobile_confirm').attr('disabled', true);
                    $('#btn_verify_mobile_confirm').css('border', '1px solid #EFEFEF');
                    isValidVerificationCode = true;
                } else {
                    $('#verificationNo').val('');
                    alert('인증번호가 일치하지 않습니다.\n확인 후 다시 시도해 주세요.');
                    isValidVerificationCode = false;
                }
            }
        });
    });

    function startTimer() {
        var timer = 180; // 3분 = 180초
        var interval = setInterval(function () {
            var minutes = Math.floor(timer / 60);
            var seconds = timer % 60;

            $('#expiryTime').text(minutes + ':' + (seconds < 10 ? '0' + seconds : seconds));

            if (--timer < 0) {
                clearInterval(interval);
                $('#phoneNumberMsg').text('');
                $('#confirm_verify_mobile').css('display', 'none');
                $('#btn_action_verify_mobile').text('인증번호받기');
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
    console.log($('#agree_to_terms_of_use').val());
    if (!isValidId) {
        alert("아이디를 확인해주세요");
        return false;
    }
    if (!isValidPw) {
        alert("비밀번호를 확인해주세요");
        return false;
    }
    if (!isValidPwConfirm) {
        alert("비밀번호 확인란을 확인해주세요");
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

    //이용약관 안넘어가는검증필
    // if(!isValidAgree) {
        // alert("이용약관에 동의해주세요.");
    //     return false;
    // }

    window.location.href = 'join_success.html';
    return true;
}

$(document).ready(function() {

    $('#submitBtn').on('click', function() {
        signUp();
    });

    // var userInfo = {
    //     userId: 'user123',
    //     userName: '홍길동',
    //     userEmail: 'user123@example.com',
    //     memberGrade: '일반 회원'
    // };

    // $('#userId').text(userInfo.userId);
    // $('#userName').text(userInfo.userName);
    // $('#userEmail').text(userInfo.userEmail);
    // $('#memberGrade').text(userInfo.memberGrade);

    // $('.home_btn').click(function() {
    //     window.location.href = 'home.html';
    // });

});



