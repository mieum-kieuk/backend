package archivegarden.shop.constant;

public final class SessionConstants {

    private SessionConstants(){
        throw new AssertionError("Final 클래스는 생성할 수 없습니다");
    }

    public static final String JOIN_MEMBER_ID_KEY = "JOIN_MEMBER_ID";

    public static final String FIND_LOGIN_ID_MEMBER_ID_KEY = "FIND_LOGIN_ID_MEMBER_ID";

    public static final String FIND_PASSWORD_EMAIL_KEY = "FIND_PASSWORD_EMAIL";

    public static final String CHECKOUT_PRODUCT_IDS = "CHECKOUT_PRODUCT_IDS";
}
