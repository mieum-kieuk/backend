$(document).ready(function() {

    $('#checkAll').change(function() {
        var isChecked = $(this).prop('checked');
        $('.checkbox').prop('checked', isChecked);
        updateTotalPrice();
    });



    $("#deleteBtn").click(function() {
        var checkedItems = $("input[name='addressCheckBox']:checked");
        if (checkedItems.length === 0) {
            alert("삭제할 항목을 선택해 주세요.");
        } else {
            if (confirm("정말 삭제하시겠습니까?")) {

                checkedItems.closest('.address_item').remove();
                alert("선택된 주소가 삭제되었습니다.");
            }
        }
    });
});