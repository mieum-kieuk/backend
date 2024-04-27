$(document).ready(function() {
    $(".remove_btn").click(function() {
        if (confirm("정말 삭제하시겠습니까?")) {
            $(this).closest('.wish_item').remove();
            alert("상품이 삭제되었습니다.");
        }
    });
    $("#deleteAllBtn").click(function() {
        if (confirm("정말 삭제하시겠습니까?")) {

            $(".wish_item").remove();
            alert("전체 삭제되었습니다.");
        }
    });

    $("#deleteBtn").click(function() {
        var checkedItems = $("input[name='wishCheckBox']:checked");
        if (checkedItems.length === 0) {
            alert("상품을 선택해 주세요.");
        } else {
            if (confirm("정말 삭제하시겠습니까?")) {

                checkedItems.closest('.wish_item').remove();
                alert("선택된 상품이 삭제되었습니다.");
            }
        }
    });
    $('#orderBtn').click(function() {
        $('#addCart').fadeIn();
    });

    $('.close_btn').click(function() {
        $('#addCart').fadeOut();
    });
});