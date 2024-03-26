$(document).ready(function() {
    $('.menu_toggle').click(function() {
        var dropdownMenu = $(this).siblings('.dropdown_menu');
        dropdownMenu.toggleClass('show');
    });
});
