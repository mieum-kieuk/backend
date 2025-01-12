package archivegarden.shop.controller.user.delivery;

import archivegarden.shop.dto.ResultResponse;
import archivegarden.shop.service.mypage.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ajax/delivery")
@RequiredArgsConstructor
public class DeliveryAjaxController {

    private final DeliveryService deliveryService;

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping
    public ResultResponse deleteDelivery(@RequestParam("deliveryId") Long deliveryId) {
        deliveryService.deleteDelivery(deliveryId);
        return new ResultResponse(HttpStatus.OK.value(), "삭제가 완료되었습니다.");
    }
}
