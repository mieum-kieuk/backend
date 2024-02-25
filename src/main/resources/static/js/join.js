$(document).ready(function() {
    let isValidId = false;
    let isValidPw = false;
    let isValidName = false;
    let isValidEmail = false;
    let isValidPhone = false;
    let isValidAgree = false;
//    let checkNum = false;
    // 아이디 입력란 유효성 검사
    $('#loginId').on('focusout', function() {
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
            data: { loginId: loginId },
            success: function(result) {
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
    $('#password').on('focusout', function() {
        var password = $(this).val();

        if (password.trim() === '') {
            $('#pwMsg').text('비밀번호를 입력해주세요.');
            isValidPw = false;
            return;
        }

        var regex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,16}$/;
        if (!regex.test(password)) {
            $('#pwMsg').text('8~16자의 영문 대/소문자, 숫자, 특수문자 조합을 사용해 주세요.');
            isValidPw = false;
            return;
        }

        $('#pwMsg').text('');
        isValidPw = true;
    });

    // 비밀번호 확인란
    $('#passwordConfirm').on('focusout', function() {
        var password = $('#password').val();
        var confirmPassword = $(this).val();

        // 입력값이 비어 있는지 확인
        if (confirmPassword === '') {
            $('#pwconfirmMsg').text('');
            isValidPw = false;
        } else {

            if (password === confirmPassword) {
                $('#pwconfirmMsg').text('비밀번호가 일치합니다.');
                isValidPw = true;
            } else {
                $('#pwconfirmMsg').text('비밀번호가 일치하지 않습니다.');
                isValidPw = false;
            }
        }
    });


    // 이름 입력란 유효성 검사
    $('#name_content input').on('focusout', function() {
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

    $('#email').on('focusout', function() {
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

    var userVerifyMobile = {
            joinSendVerificationNumber: function() {
                // 인증번호를 발송하는 기능 구현
                console.log('Verification number sent');
            },
            joinVerifySmsNumberConfirm: function() {
                // SMS 인증번호를 확인하는 기능 구현
                console.log('Verification SMS number confirmed');
            }
        };
    $('#btn_action_verify_mobile').prop('disabled', true);

    $('#phonenumber2, #phonenumber3').on('input', function() {
        var phoneNumber2 = $('#phonenumber2').val();
        var phoneNumber3 = $('#phonenumber3').val();

        // 번호를 입력한 후에 버튼이 활성화됨
        if (phoneNumber2.length === 4 && phoneNumber3.length === 4) {
            $('#btn_action_verify_mobile').prop('disabled', false);
        } else {
            $('#btn_action_verify_mobile').prop('disabled', true);
        }
    });

//    $('#phonenumber2, #phonenumber3').on('focusout', function() {
//        var phoneNumber2 = $('#phonenumber2').val();
//        var phoneNumber3 = $('#phonenumber3').val();
//
//        if (phoneNumber2 !== '' && phoneNumber3 === '') {
//            $('#phoneMsg').text('번호를 입력해주세요.');
//        } else {
//            $('#phoneMsg').text('');
//        }
//    });

    //임시로 이렇게 해놨어 인증번호 뜨게
    $('#btn_action_verify_mobile').on('click', function() {
        $('#confirm_verify_mobile').css('display', 'flex');
        startTimer();
    });

//이부분이 안돼서 주석처리 해놨어
// $('#btn_action_verify_mobile').on('click', function() {
//        var phoneNumber = $('#phonenumber1').val() + $('#phonenumber2').val() + $('#phonenumber3').val();
//
//         AJAX 호출 - 인증번호 요청
//        $.ajax({
//            type: 'POST',
//            url: '/send/verificationNo',
//            data: { phoneNumber: phoneNumber },
//            success: function(response) {
//                if (response.success) {
//                    $('#confirm_verify_mobile').css('display', 'flex');
//                    $('#btn_action_verify_mobile').text('재전송');
//                    $('#expiryTime').text('3:00');
//                    startTimer();
//                } else {
//                    $('#result_send_verify_mobile_fail').text('인증번호 발송에 실패했습니다.');
//                    $('#result_send_verify_mobile_fail').removeClass('displaynone');
//                }
//            },
//            error: function(xhr, status, error) {
//                $('#result_send_verify_mobile_fail').text('인증번호 발송에 실패했습니다.');
//                $('#result_send_verify_mobile_fail').removeClass('displaynone');
//            }
//        });
//});
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
////회원가입 완료
//$("#submitBtn").click(function() {
//  if (!isValidId) {alert("아이디를 확인해주세요");return;}
//  if (!isValidPw) {alert("비밀번호를 확인해주세요");return;}
//  if ($("input[name='password']").val() != $("input[name='re-password']").val()) {
//    alert("비밀번호 확인란이 틀립니다");return;
//  }
//  if (!isValidName) {alert("이름 확인해주세요");return;}
////  if (!isValidPhone) {alert("전화번호를 확인해주세요");return;}
////  if (!checkNum) {alert("인증번호를 확인해주세요");return;}
//
//  let data = {
//    "loginId":$("#loginId").val(),
//    "password":$("#password").val(),
//    "name":$("#name").val(),
////    "phone":$("#phone").val(),
//  };
//
//  $.ajax({
//    url:`${path}/users`,
//    type:"POST",
//    data:data,
//    cache:false,
//    success : function(data){
//      if (data.success) {
//        alert("회원가입 성공");
//        location.href="../index.html";
//      } else {
//        alert(data.msg);
//      }
//    }
//  });
//});



