package archivegarden.shop.repository.wish;

import archivegarden.shop.dto.user.wish.MyWishDto;

import java.util.List;

public interface WishRepositoryCustom {

    List<MyWishDto> findMyWishList(Long memberId);
}
