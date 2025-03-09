package archivegarden.shop.repository.cart;

import archivegarden.shop.dto.order.CartListDto;

import java.util.List;

public interface CartRepositoryCustom {

    List<CartListDto> findAllProducts(Long memberId);
}


