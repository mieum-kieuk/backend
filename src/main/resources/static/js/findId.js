$(document).ready(function() {

    $('#phonenumber_view').hide();
    $('#email_view').show();

    $('input[name="find_method"]').change(function() {
        if ($('#method_email').is(':checked')) {
            $('#email_view').show();
            $('#phonenumber_view').hide();
        } else if ($('#method_phone').is(':checked')) {
            $('#email_view').hide();
            $('#phonenumber_view').show();
        }
    });

    $('#loginId').on('focusout', function () {
        var loginId = $(this).val();
    });

});