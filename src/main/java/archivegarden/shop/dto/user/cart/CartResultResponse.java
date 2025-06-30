package archivegarden.shop.dto.user.cart;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartResultResponse {

    private int status;
    private String message;
    private int cartItemCount;

    public CartResultResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
