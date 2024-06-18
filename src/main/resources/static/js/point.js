$(document).ready(function() {
    $('.point_btn').click(function() {
        let selectedMember = $('.admins_table tbody tr input[type=checkbox]:checked');
        if (selectedMember.length === 0) {
            alert('회원을 선택해 주세요.');
            return;
        }

        let userId = selectedMember.closest('tr').find('td:nth-child(4)').text().trim();
        $('#userId').val(userId);

        $('#pointModal').css('display', 'flex');
    });

    let closeBtn = $(".modal_head .close");
    let cancelBtn = $(".btn_wrap .cancel_btn");

    closeBtn.add(cancelBtn).click(function() {
        $('#pointModal').hide();
        $('#addPointForm')[0].reset();
    });

    $(window).click(function(event) {
        if (event.target.id === "pointModal") {
            $('#pointModal').hide();
            $('#addPointForm')[0].reset();
        }
    });

    $('.reset_btn').click(function() {
        $('#adminsSearchForm')[0].reset();
    });
});

// 회원 선택 시 스타일 변경 및 선택 상태 관리
$('.admins_table tbody tr').click(function() {
    $(this).toggleClass('selected').siblings().removeClass('selected');
});

function validateBeforeSubmit() {
    let userId = $('#userId').val().trim();
    let pointContent = $('#pointContent').val().trim();
    let point = $('#point').val().trim();

    if (userId === '') {
        alert('아이디를 입력해 주세요.');
        return false;
    }

    if (pointContent === '') {
        alert('적립금 내용을 입력해 주세요.');
        return false;
    }

    if (point === '') {
        alert('적립금을 입력해 주세요.');
        return false;
    }

    return true;
}



