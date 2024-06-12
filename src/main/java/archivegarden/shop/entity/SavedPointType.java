package archivegarden.shop.entity;

public enum SavedPointType {

    JOIN("회원가입 축하 적립금"), REVIEW("사진 리뷰 적립금"), ORDER("주문(배송완료) 적립금");

    private final String description;

    SavedPointType(String description) {
        this.description = description;
    }
}
