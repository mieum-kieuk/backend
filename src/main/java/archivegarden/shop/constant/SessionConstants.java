package archivegarden.shop.constant;

public final class SessionConstants {

    private SessionConstants(){
        throw new AssertionError("Final 클래스는 생성할 수 없습니다");
    }

    public static final String MEMBER_LOGIN_ERROR = "MEMBER_LOGIN_ERROR";

    public static final String ADMIN_LOGIN_ERROR  = "ADMIN_LOGIN_ERROR";

    public static final String CHECKOUT_PRODUCT_IDS = "CHECKOUT_PRODUCT_IDS";
}
