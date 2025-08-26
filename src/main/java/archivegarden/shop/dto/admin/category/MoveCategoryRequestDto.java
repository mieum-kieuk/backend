package archivegarden.shop.dto.admin.category;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record MoveCategoryRequestDto(
        @NotNull(message = "카테고리 ID는 필수 값입니다.")
        Long id,

        Long newParentId,

        @Min(value = 0, message = "순서는 0 이상의 정수여야 합니다.")
        int newIndex
) {}
