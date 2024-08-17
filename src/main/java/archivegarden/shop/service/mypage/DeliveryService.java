package archivegarden.shop.service.mypage;

import archivegarden.shop.dto.delivery.*;
import archivegarden.shop.entity.Delivery;
import archivegarden.shop.entity.Member;
import archivegarden.shop.exception.NotFoundException;
import archivegarden.shop.exception.ajax.AjaxNotFoundException;
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
     * @throws NotFoundException
     */
    public Long saveDelivery(AddDeliveryForm form, Long memberId) {
        //Member 조회
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));

        //기존의 기본 배송지 제거
        if (form.isDefaultDelivery()) {
            changeDefaultDelivery(memberId);
        }

        //Delivery 엔티티 생성
        Delivery delivery = Delivery.createDelivery(form, member);

        //Delivery 저장
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
     * @throws NotFoundException
     */
    @Transactional(readOnly = true)
    public EditDeliveryForm getEditDeliveryForm(Long deliverId) {
        //Delivery 조회
        Delivery delivery = deliveryRepository.findById(deliverId).orElseThrow(() -> new NotFoundException("존재하지 않는 배송지입니다."));

        return new EditDeliveryForm(delivery);
    }

    /**
     * 배송지 수정
     */
    public void editDelivery(EditDeliveryForm form, Long deliveryId, Long memberId) {
        //수정할 Delivery 조회
        Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow(() -> new NotFoundException("존재하지 않는 배송지 주소입니다."));

        //기존의 기본 배송지 제거
        if (form.isDefaultDelivery()) {
            changeDefaultDelivery(memberId);
        }

        //Delivery 수정
        delivery.update(form);
    }

    /**
     * Ajax
     * 배송지 삭제
     *
     * @throws AjaxNotFoundException
     */
    public void deleteDelivery(Long deliveryId) {
        //Delivery 조회
        Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow(() -> new AjaxNotFoundException("존재하지 않는 배송지입니다."));

        //Delivery 삭제
        deliveryRepository.delete(delivery);
    }

    /**
     * 기본 배송지 조회
     */
    @Transactional(readOnly = true)
    public DeliveryDto getDefaultDelivery(Long memberId) {
        //Delivery 조회
        Delivery defaultDelivery = deliveryRepository.findDefaultDelivery(memberId);

        return new DeliveryDto(defaultDelivery);
    }

    /**
     * 기본 배송지 변경
     */
    private void changeDefaultDelivery(Long memberId) {
        //기본 배송지 조회
        Delivery defaultDelivery = deliveryRepository.findDefaultDelivery(memberId);

        //기본 배송지 제거
        defaultDelivery.removeDefault();
    }

    /**
     * 주문서 페이지 팝업창
     * 배송지 수정 폼 조회
     *
     * @throws NotFoundException
     */
    @Transactional(readOnly = true)
    public EditPopupDeliveryForm getEditPopupDeliveryForm(Long deliverId) {
        Delivery delivery = deliveryRepository.findById(deliverId).orElseThrow(() -> new NotFoundException("존재하지 않는 배송지입니다."));
        return new EditPopupDeliveryForm(delivery);
    }

    /**
     * 주문서 페이지 팝업창
     * 배송지 수정
     *
     * @throws NotFoundException
     */
    public void editPopupDelivery(EditPopupDeliveryForm form, Long deliveryId) {
        //수정할 Delivery 조회
        Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow(() -> new NotFoundException("존재하지 않는 배송지 주소입니다."));

        //배송지 정보 수정
        delivery.updatePopup(form);
    }
}
