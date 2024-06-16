$(document).ready(function () {

    let url = window.location.href;
    if (!url.includes('category')) {
        $('#all').addClass('selected');
    } else {
        if (url.includes('cafe')) {
            $('#cafe').addClass('selected');
        } else if (url.includes('ocean')) {
            $('#ocean').addClass('selected');
        } else if (url.includes('sunset')) {
            $('#sunset').addClass('selected');
        } else {
            $('#all').addClass('selected');
        }
    }

    if (url.includes('sorted_type')) {
        let sortedType = url.split('sorted_type=')[1];
        if (sortedType.includes('&')) {
            sortedType = sortedType.split('&page')[0];
        }
        if (sortedType === '0') {
            $('#sortedType0').addClass('selected');
        } else if (sortedType === '1') {
            $('#sortedType1').addClass('selected');
        } else if (sortedType === '2') {
            $('#sortedType2').addClass('selected');
        } else if (sortedType === '3') {
            $('#sortedType3').addClass('selected');
        } else if (sortedType === '4') {
            $('#sortedType4').addClass('selected');
        }
    }

    $('.display_img').click(function () {
        $('.display_img.selected').removeClass('selected');

        $(this).addClass('selected');

        let imgUrl = $(this).find('img').attr('src');
        $('.prd_img img').attr('src', imgUrl);
    });

    $('.quant_input').each(function () {
        let currentValue = parseInt($(this).val());
        let decreaseBtn = $(this).siblings('.decrease');
        if (currentValue === 1) {
            decreaseBtn.addClass('disabled');
        }
    });
    $('#increaseBtn').click(function (event) {
        event.preventDefault();
        let input = $(this).siblings('.quant_input');
        let currentValue = parseInt(input.val());
        let decreaseBtn = $(this).siblings('.decrease');
        input.val(currentValue + 1);
        if (currentValue === 1) { // 현재 값이 1일 때
            decreaseBtn.removeClass('disabled'); // 감소 버튼을 활성화
        }
        updateTotal(); // 총 수량 및 가격 업데이트
    });

    $('#decreaseBtn').click(function (event) {
        event.preventDefault();
        let input = $(this).siblings('.quant_input');
        let currentValue = parseInt(input.val());
        if (currentValue > 1) {
            input.val(currentValue - 1);
            updateTotal(); // 총 수량 및 가격 업데이트
        }
        if (currentValue === 1) { // 현재 값이 2 이하일 때
            $(this).addClass('disabled');
        }
    });

    function setProductState() {
        // 초기화
        $('.prd_price.price, .prd_price.discount, .prd_price.soldout').removeClass('active');
        $('.buy_btn .btn1').addClass('active');
        $('.buy_btn .soldout').removeClass('active');

        // 상태 설정
        if ($('.prd_price.discount').length) {
            $('.prd_price.discount').addClass('active');
        } else if ($('.prd_price.soldout').length) {
            $('.prd_price.soldout').addClass('active');
            $('.buy_btn .btn1').removeClass('active');
            $('.buy_btn .soldout').addClass('active');

        } else {
            $('.prd_price.price').addClass('active');
            $('#CartBtn, #BuyBtn').addClass('active');
        }

        updateTotal();
    }
    setProductState();

    function updateTotal() {
        let quantity = parseInt($('.quant_input').val());
        let isDiscount = $('.prd_price.discount').hasClass('active');
        let isSoldOut = $('.prd_price.soldout').hasClass('active');
        let currentPrice = isDiscount ? parseInt($('#salePrice').text().replace(/[^0-9]/g, '')) : parseInt($('#productPrice').text().replace(/[^0-9]/g, ''));
        let totalPrice = quantity * currentPrice;
        if (isSoldOut) {
            $('.prd_total').hide();
            $('.prd_quantity').hide();
            return;
        } else {
            $('.prd_total').show();
            $('.prd_quantity').show();
        }
        $('#totalQuantity').text(quantity + '개');
        $('#totalPrice').text(totalPrice.toLocaleString() + '원');
    }


    $('.prd_info_list > li').click(function () {
        let tabWrap = $(this).find('.tab_wrap');
        $('.prd_info_list > li .tab_wrap').not(tabWrap).slideUp();
        tabWrap.slideToggle();

        let expandIcon = $(this).find('.expand_icon');
        $('.prd_info_list > li .expand_icon').not(expandIcon).text('expand_more');
        expandIcon.text(function (_, text) {
            return text === 'expand_more' ? 'expand_less' : 'expand_more';
        });

        return false;
    });

    // 상품후기,상품문의
    let reviewModal = $("#reviewModal");
    let inquiryModal = $("#inquiryModal");
    let closeBtn = $(".close");

    $("#product .review_wrap #writeBtn").click(function() {
        $("#reviewModal").css("display", "flex");
    });
    $("#product .qna_wrap #writeBtn").click(function() {
        $("#inquiryModal").css("display", "flex");
    });

    closeBtn.click(function() {
        inquiryModal.hide();
        reviewModal.hide();
    });
    $(window).click(function(event) {
        if (event.target.id === "inquiryModal") {
            inquiryModal.hide();
        } else if (event.target.id === "reviewModal") {
            reviewModal.hide();
        }
    });

    $(".qna_items, .review_items").click(function() {
        let currentContent = $(this).next(".qna_content, .review_content");
        if (currentContent.is(":visible")) {
            currentContent.slideUp("fast");
        } else {
            $(".qna_content, .review_content").not(currentContent).slideUp("fast").promise().done(function() {
                currentContent.slideDown();
            });
        }
    });

    $("#submitBtn").show();
    $("#editSubmitBtn").hide();
    $(".edit_btn").click(function() {
        let questionText = $(this).closest(".qna_question").find(".qna_text").text();
        let answerText = $(this).closest(".qna_content").find(".qna_answer").text();

        $("#edit_title").val(questionText.trim());
        $("#edit_content").val(answerText.trim());

        $("#submitBtn").hide();
        $("#editSubmitBtn").show();

        $("#inquiryModal").css("display", "flex");
    });
    $(".edit_btn").click(function() {
        let reviewText = $(this).closest(".review_text_wrap").find(".review_text").text().trim();
        let reviewRating = $(this).closest(".review_cont").find(".star.filled_star").length;

        $("#content").val(reviewText);
        $("input[name='rating'][value='" + reviewRating + "']").prop('checked', true);

        $("#submitBtn").hide();
        $("#editSubmitBtn").show();

        $("#reviewModal").css("display", "flex");
    });
});

let csrfToken = $("meta[name='_csrf']").attr("content");
let csrfHeader = $("meta[name='_csrf_header']").attr("content");

$('.wish').click(function () {

    let heart = $(this);
    let productId = heart.attr('id');

    if (heart.hasClass('filled')) {
        $.ajax({
            url: '/api/wish/remove',
            type: 'POST',
            data: {productId: productId},
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken);
            },
            success: function () {
                heart.removeClass('filled');
            },
            error: function (errorResult) {
                alert(errorResult['message']);
            }
        })
    } else {
        $.ajax({
            type: 'POST',
            url: '/api/wish/add',
            data: {productId: productId},
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken);
            },
            success: function () {
                heart.addClass('filled')
            },
            error: function (errorResult) {
                alert(errorResult['message']);
            }
        })
    }
});