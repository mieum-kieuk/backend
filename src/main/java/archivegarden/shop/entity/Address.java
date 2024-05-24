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

    public Address(String zipCode, String basicAddress, String detailAddress) {
        this.zipCode = zipCode;
        this.basicAddress = basicAddress;
        this.detailAddress = detailAddress;
    }

    public String fullAddress() {
        return "(" + getZipCode() + ") " + getBasicAddress() + " " + getDetailAddress();
    }
}
