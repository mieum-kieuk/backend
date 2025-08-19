package archivegarden.shop.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AvailabilityResponseDto {

    private boolean available;
    private String code;
    private String message;
}
