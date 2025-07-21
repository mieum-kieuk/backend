package archivegarden.shop.controller.user.mypage;

import archivegarden.shop.dto.ResultResponse;
import archivegarden.shop.service.mypage.DeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "배송지", description = "사용자 페이지에서 마이페이지 배송지 관련 AJAX API")
@RestController
@RequestMapping("/ajax/deliveries")
@RequiredArgsConstructor
public class MyDeliveryAjaxController {

    private final DeliveryService deliveryService;

    @Operation(
            summary = "배송지 삭제",
            description = "배송지를 삭제합니다",
            responses = {
                    @ApiResponse(responseCode = "200", description = "배송지 삭제"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 배송지"),
            }
    )
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping
    public ResultResponse deleteDelivery(@RequestParam("deliveryId") Long deliveryId) {
        deliveryService.deleteDelivery(deliveryId);
        return new ResultResponse(HttpStatus.OK.value(), "삭제가 완료되었습니다.");
    }
}
