package archivegarden.shop.dto.payment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Portone {

    private String paymentId;
    private String transactionType;
    private String txId;
    private String code;
    private String message;

    String status;  //결제 건 상태
    String transactionId;    //결제 건 포트원 채번 아이디
    Long orderId;
    String orderName;   //주문명
    Long amount; //총 결제금액
    Long vat;   //부가세액
    String currency;    //통화 단위

    String buyerName;    //이름
    String buyerEmail;  //이메일
    String buyerPhone;  //휴대전화번호

    String payMethod;   //결제수단 정보
    String pgProvider;  //PG사 결제 모듈

    //==PAID==//
    String paidAt;  //결제 완료 시점
    String cardName;    //카드 상품명
    Long cardQuota; //할부 개월 수

    //==FAILED==//
    String failedAt;    //결제 실패 시점

    //==CANCELLED==//
    String cancelledAt;    //결제 취소 시점
}
