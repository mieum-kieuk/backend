package archivegarden.shop.controller.user.delivery;

import archivegarden.shop.dto.delivery.DeliveryPopupDto;
import archivegarden.shop.dto.delivery.EditPopupDeliveryForm;
import archivegarden.shop.entity.Member;
import archivegarden.shop.service.mypage.DeliveryService;
import archivegarden.shop.web.annotation.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    /**
     * 주문서 페이지에서 이전 배송지 목록을 조회하는 요청을 처리하는 메서드
     */
    @GetMapping("/popup/deliveries")
    public String popupDeliveries(@CurrentUser Member loginMember, Model model) {
        List<DeliveryPopupDto> deliveries = deliveryService.getDeliveriesInPopup(loginMember.getId());
        model.addAttribute("deliveries", deliveries);
        return "user/order/checkout_delivery_popup";
    }

    /**
     * 주문서 페이지에서 배송지 수정 폼을 반환하는 메서드
     */
    @GetMapping("/popup/deliveries/{deliveryId}/edit")
    public String popupDeliveryEditForm(@PathVariable("deliveryId") Long deliveryId, Model model) {
        EditPopupDeliveryForm editDeliveryForm = deliveryService.getEditPopupDeliveryForm(deliveryId);
        model.addAttribute("form", editDeliveryForm);
        return "user/order/checkout_edit_delivery";
    }

    /**
     * 주문서 페이지에서 배송지 배송지 수정 요청을 처리하는 메서드
     */
    @PostMapping("/popup/deliveries/{deliveryId}/edit")
    public String popupDeliveryEdit(@ModelAttribute("form") EditPopupDeliveryForm form, @PathVariable("deliveryId") Long deliveryId) {
        deliveryService.editPopupDelivery(form, deliveryId);
        return "redirect:/popup/deliveries";
    }
}
