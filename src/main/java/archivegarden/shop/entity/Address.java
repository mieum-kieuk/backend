package archivegarden.shop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {

    @Column(name = "zip_code", length = 5, nullable = false)
    private String zipCode;

    @Column(name = "basic_address", length = 40, nullable = false)
    private String basicAddress;

    @Column(name = "detail_address", length = 40, nullable = false)
    private String detailAddress;


    //==비즈니스 로직==//
    /**
     * 전체 주소
     */
    public String fullAddress() {
        return "(" + getZipCode() + ") " + getBasicAddress() + " " + getDetailAddress();
    }

    /**
     * 주문서 페이지 팝업창
     * 상세 주소 변경
     */
    public void updateDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    //==생성자==//
    public Address(String zipCode, String basicAddress, String detailAddress) {
        this.zipCode = zipCode;
        this.basicAddress = basicAddress;
        this.detailAddress = detailAddress;
    }
}
