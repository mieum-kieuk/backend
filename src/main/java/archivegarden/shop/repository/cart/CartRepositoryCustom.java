package archivegarden.shop.repository.cart;

import archivegarden.shop.dto.user.order.CartListDto;

import java.util.List;

public interface CartRepositoryCustom {

    List<CartListDto> findCartItems(Long memberId);
}


