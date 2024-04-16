$(document).ready(function() {
    $('#CartBtn').click(function() {
        $('#addCart').fadeIn();
    });

    $('.close_btn').click(function() {
        $('#addCart').fadeOut();
    });
});
