package archivegarden.shop.entity;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {

    private String zipCode;
    private String basicAddress;
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
