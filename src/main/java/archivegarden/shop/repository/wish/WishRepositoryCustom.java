package archivegarden.shop.repository.wish;

import archivegarden.shop.dto.mypage.MyWishDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WishRepositoryCustom {

    Page<MyWishDto> findDtoAll(Long memberId, Pageable pageable);
}
