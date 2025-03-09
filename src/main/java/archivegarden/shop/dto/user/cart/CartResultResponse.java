package archivegarden.shop.dto.user.cart;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartResultResponse {

    private int code;
    private String message;
    private int cartItemCount;

    public CartResultResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
