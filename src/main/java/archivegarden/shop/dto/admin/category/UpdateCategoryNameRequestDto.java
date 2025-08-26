package archivegarden.shop.dto.admin.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCategoryNameRequestDto(

        @NotBlank(message = "카테고리명을 입력해 주세요.")
        @Size(max = 50, message = "카테고리명은 100자 이하여야 합니다.")
        String name
) {}