package archivegarden.shop.service.mypage;

import archivegarden.shop.dto.delivery.*;
import archivegarden.shop.entity.Delivery;
import archivegarden.shop.entity.Member;
import archivegarden.shop.exception.ajax.AjaxEntityNotFoundException;
import archivegarden.shop.exception.common.EntityNotFoundException;
import archivegarden.shop.repository.DeliveryRepository;
import archivegarden.shop.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class DeliveryService {

    private final MemberRepository memberRepository;
    private final DeliveryRepository deliveryRepository;

    /**
     * 배송지 저장
     *
     * @throws EntityNotFoundException
     */
    public Long saveDelivery(AddDeliveryForm form, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));

        //기존의 기본 배송지 제거
        if (form.isDefaultDelivery()) {
            updateDefaultDelivery(memberId);
        }

        Delivery delivery = Delivery.createDeliveryInMypage(form, member);
        deliveryRepository.save(delivery);

        return delivery.getId();
    }

    /**
     * 배송지 목록 조회
     */
    @Transactional(readOnly = true)
    public List<DeliveryListDto> getDeliveries(Long memberId) {
        return deliveryRepository.findAllByMemberId(memberId).stream()
                .map(DeliveryListDto::new)
                .collect(Collectors.toList());
    }

    /**
     * 배송지 수정 폼 조회
     *
     * @throws EntityNotFoundException
     */
    @Transactional(readOnly = true)
    public EditDeliveryForm getEditDeliveryForm(Long deliverId) {
        Delivery delivery = deliveryRepository.findById(deliverId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 배송지입니다."));
        return new EditDeliveryForm(delivery);
    }

    /**
     * 배송지 수정
     *
     * @throws EntityNotFoundException
     */
    public void editDelivery(EditDeliveryForm form, Long deliveryId, Long memberId) {
        Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 배송지 주소입니다."));

        if (form.isDefaultDelivery()) {
            updateDefaultDelivery(memberId);
        }

        delivery.update(form);
    }

    /**
     * 배송지 삭제
     *
     * @throws AjaxEntityNotFoundException
     */
    public void deleteDelivery(Long deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow(() -> new AjaxEntityNotFoundException("존재하지 않는 배송지입니다."));
        deliveryRepository.delete(delivery);
    }

    /**
     * 기본 배송지 조회
     */
    @Transactional(readOnly = true)
    public DeliveryDto getDefaultDelivery(Long memberId) {
        Delivery defaultDelivery = deliveryRepository.findDefaultDelivery(memberId);
        return new DeliveryDto(defaultDelivery);
    }

    /**
     * 기본 배송지 변경
     */
    private void updateDefaultDelivery(Long memberId) {
        Delivery defaultDelivery = deliveryRepository.findDefaultDelivery(memberId);
        defaultDelivery.removeDefault();
    }

    /**
     * 주문서 페이지 팝업창에서 배송지 목록 조회
     */
    @Transactional(readOnly = true)
    public List<DeliveryPopupDto> getDeliveriesInPopup(Long memberId) {
        return deliveryRepository.findAllByMemberId(memberId).stream()
                .map(DeliveryPopupDto::new)
                .collect(Collectors.toList());
    }

    /**
     * 주문서 페이지 팝업창에서 배송지 수정 폼 조회
     *
     * @throws EntityNotFoundException
     */
    @Transactional(readOnly = true)
    public EditPopupDeliveryForm getEditPopupDeliveryForm(Long deliverId) {
        Delivery delivery = deliveryRepository.findById(deliverId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 배송지입니다."));
        return new EditPopupDeliveryForm(delivery);
    }

    /**
     * 주문서 페이지 팝업창에서 배송지 수정
     *
     * @throws EntityNotFoundException
     */
    public void editPopupDelivery(EditPopupDeliveryForm form, Long deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 배송지 주소입니다."));
        delivery.updatePopup(form);
    }
}
