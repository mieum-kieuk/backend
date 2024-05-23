package archivegarden.shop.service.mypage;

import archivegarden.shop.dto.mypage.address.AddAddressForm;
import archivegarden.shop.dto.mypage.address.AddressListDto;
import archivegarden.shop.dto.mypage.address.EditAddressForm;
import archivegarden.shop.dto.order.ShippingAddressDto;
import archivegarden.shop.entity.Member;
import archivegarden.shop.entity.ShippingAddress;
import archivegarden.shop.exception.NoSuchMemberException;
import archivegarden.shop.exception.NoSuchShippingAddressException;
import archivegarden.shop.exception.ajax.NoSuchShippingAddressAjaxException;
import archivegarden.shop.repository.MemberRepository;
import archivegarden.shop.repository.ShippingAddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ShippingAddressService {

    private final ShippingAddressRepository shippingAddressRepository;
    private final MemberRepository memberRepository;

    /**
     * 배송지 주소 저장
     *
     * @throws NoSuchMemberException
     */
    public Long saveAddress(AddAddressForm form, Long memberId) {
        //회원 조회
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchMemberException("존재하지 않는 회원입니다."));

        //기본 배송지 변경
        changeDefaultShippingAddress(form.getIsDefaultAddress(), memberId);

        //배송지 주소 엔티티 생성
        ShippingAddress shippingAddress = ShippingAddress.createShippingAddress(form, member);

        return shippingAddress.getId();
    }

    /**
     * 배송지 주소 목록 조회
     */
    @Transactional(readOnly = true)
    public List<AddressListDto> getAddresses(Member loginMember) {
        return shippingAddressRepository.findAllByMemberId(loginMember.getId()).stream()
                .map(AddressListDto::new)
                .collect(Collectors.toList());
    }

    /**
     * 배송지 주소 수정 폼 조회
     *
     * @throws NoSuchShippingAddressException
     */
    @Transactional(readOnly = true)
    public EditAddressForm getEditAddressForm(Long addressId) {
        ShippingAddress address = shippingAddressRepository.findById(addressId).orElseThrow(() -> new NoSuchShippingAddressException("존재하지 않는 배송지 주소입니다."));
        return new EditAddressForm(address);
    }

    /**
     * 배송지 주소 수정
     */
    public void editAddress(EditAddressForm form, Long addressId, Long memberId ) {
        //수정할 배송지 주소 엔티티 조회
        ShippingAddress address = shippingAddressRepository.findById(addressId).orElseThrow(() -> new NoSuchShippingAddressException("존재하지 않는 배송지 주소입니다."));

        //기본 배송지 변경
        changeDefaultShippingAddress(form.getIsDefaultAddress(), memberId);

        address.update(form);
    }

    /**
     * 배송지 주소 복수 삭제
     *
     * @throws NoSuchShippingAddressAjaxException
     */
    public void deleteAddresses(List<Long> addressIds) {
        addressIds.forEach(id -> {
            ShippingAddress shippingAddress = shippingAddressRepository.findById(id).orElseThrow(() -> new NoSuchShippingAddressAjaxException("존재하지 않는 배송지 주소입니다."));
            shippingAddressRepository.delete(shippingAddress);
        });
    }

    /**
     * 기본 배송지 조회
     *
     * @throws NoSuchShippingAddressException
     */
    @Transactional(readOnly = true)
    public ShippingAddressDto getDefaultShippingAddress(Long memberId) {
        ShippingAddress shippingAddress = shippingAddressRepository.findDefaultShippingAddress(memberId);
        return new ShippingAddressDto(shippingAddress);
    }

    /**
     * 기본 배송지 변경
     *
     *      * @throws NoSuchShippingAddressException
     */
    private void changeDefaultShippingAddress(Boolean form, Long memberId) {
        if(form) {
            ShippingAddress defaultShippingAddress = shippingAddressRepository.findDefaultShippingAddress(memberId);

            defaultShippingAddress.removeDefault();
        }
    }
}
